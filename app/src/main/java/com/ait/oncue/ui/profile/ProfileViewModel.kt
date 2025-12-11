package com.ait.oncue.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait.oncue.data.OnCueRepository
import com.ait.oncue.data.OnCueUser
import com.ait.oncue.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: OnCueRepository
) : ViewModel() {

    var user by mutableStateOf<OnCueUser?>(null)
    var userPosts by mutableStateOf<List<Post>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    var showPinnedOnly by mutableStateOf(true)
        private set

    // Filtered posts for the PromptsTab
    val displayedPosts: List<Post>
        get() = if (showPinnedOnly) userPosts.filter { it.id.hashCode() % 2 == 0 } // example pinned logic
        else userPosts

    fun setShowPinned(value: Boolean) {
        showPinnedOnly = value
    }

    fun loadProfileAndHistory() {
        viewModelScope.launch {
            loading = true
            error = null

            val currentUser = repo.getCurrentUser()
            if (currentUser == null) {
                error = "Not logged in"
                loading = false
                return@launch
            }

            // Load profile
            repo.getUserProfile(currentUser.uid).onSuccess {
                user = it
            }.onFailure {
                error = it.message
            }

            // Load history
            repo.getUserHistory(currentUser.uid).onSuccess {
                userPosts = it
            }.onFailure {
                error = it.message
            }

            loading = false
        }
    }
}