package com.ait.oncue.ui.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait.oncue.data.DailyPrompt
import com.ait.oncue.data.OnCueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repo: OnCueRepository
) : ViewModel() {

    var prompt by mutableStateOf<DailyPrompt?>(null)
    var feedState by mutableStateOf<OnCueRepository.FeedState>(OnCueRepository.FeedState.Locked)

    fun loadPromptForToday() = viewModelScope.launch {
        val today = LocalDate.now().toString()
        try {
            val result = repo.getDailyPrompt(today)
            result.onSuccess { prompt = it }
                .onFailure {
                    println("Failed to load prompt: ${it.message}")
                }
        } catch (e: Exception) {
            println("Exception loading prompt: ${e.message}")
        }
    }

    fun loadFeed(promptId: String) = viewModelScope.launch {
        feedState = repo.getFeedForPrompt(promptId)
    }
}