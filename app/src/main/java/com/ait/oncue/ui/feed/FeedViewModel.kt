package com.ait.oncue.ui.feed

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait.oncue.data.DailyPrompt
import com.ait.oncue.data.OnCueRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repo: OnCueRepository
) : ViewModel() {

    var prompt by mutableStateOf<DailyPrompt?>(null)
    var feedState by mutableStateOf<OnCueRepository.FeedState>(OnCueRepository.FeedState.Locked)

    // To keep track of prompts during rolls
    private val seenPromptIds = mutableSetOf<String>()

    fun loadPromptForToday() = viewModelScope.launch {
        val today = LocalDate.now().toString()
        try {
            // Load the FIRST prompt (can be any type - we'll let user roll to others)
            val result = repo.getDailyPrompt(today)
            result.onSuccess { dailyPrompt ->
                prompt = dailyPrompt
                // Only add to seen if prompt is not null and has an id
                if (dailyPrompt != null && dailyPrompt.id.isNotEmpty()) {
                    seenPromptIds.add(dailyPrompt.id)
                    Log.d("FeedViewModel", "Loaded prompt: id=${dailyPrompt.id}, type=${dailyPrompt.type}")
                }
            }.onFailure {
                Log.e("FeedViewModel", "Failed to load prompt: ${it.message}")
            }
        } catch (e: Exception) {
            Log.e("FeedViewModel", "Exception loading prompt: ${e.message}")
        }
    }

    // Default number of rolls is 2
    var rollsLeft by mutableIntStateOf(2)

    fun rollPrompt() {
        if (rollsLeft <= 0) return

        viewModelScope.launch {
            try {
                val snapshot = Firebase.firestore.collection("prompts")
                    .get()
                    .await() // suspend function, requires kotlinx-coroutines-play-services

                val prompts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DailyPrompt::class.java)
                }

                if (prompts.isNotEmpty()) {
                    // Pick a random prompt - don't count the current prompt
                    val filtered = prompts.filter { it.id !in seenPromptIds }

                    // If filtering removes everything (only 1 prompt exists), fall back safely
                    val pool = if (filtered.isEmpty()) prompts else filtered

                    val randomPrompt = pool.random()

                    prompt = randomPrompt
                    seenPromptIds.add(randomPrompt.id)
                    rollsLeft--
                }

            } catch (e: Exception) {
                Log.e("FeedViewModel", "Failed to roll prompt", e)
            }
        }
    }

    fun loadFeed() = viewModelScope.launch {
        feedState = repo.getFeedForToday()
    }
}