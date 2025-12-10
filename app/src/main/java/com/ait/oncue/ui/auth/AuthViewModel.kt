package com.ait.oncue.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait.oncue.data.OnCueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: OnCueRepository
) : ViewModel() {

    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var authSuccess by mutableStateOf(false)

    fun signIn(email: String, password: String) = viewModelScope.launch {
        loading = true
        error = null

        repo.signIn(email, password)
            .onSuccess {
                authSuccess = true
            }
            .onFailure {
                error = it.message ?: "Login failed"
            }

        loading = false
    }

    fun signUp(email: String, password: String, username: String) = viewModelScope.launch {
        loading = true
        error = null

        repo.signUp(email, password, username)
            .onSuccess {
                authSuccess = true
            }
            .onFailure {
                error = it.message ?: "Sign up failed"
            }

        loading = false
    }

    fun clearError() {
        error = null
    }
}