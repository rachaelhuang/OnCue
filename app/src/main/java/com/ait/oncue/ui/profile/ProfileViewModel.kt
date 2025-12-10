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

    fun loadProfile() = viewModelScope.launch {
        loading = true
        val currentUser = repo.getCurrentUser()

        if (currentUser != null) {
            repo.getUserProfile(currentUser.uid).onSuccess {
                user = it
            }.onFailure {
                error = it.message
            }
        }
        loading = false
    }

    fun loadUserHistory() = viewModelScope.launch {
        val currentUser = repo.getCurrentUser()

        if (currentUser != null) {
            repo.getUserHistory(currentUser.uid).onSuccess {
                userPosts = it
            }.onFailure {
                error = it.message
            }
        }
    }
}