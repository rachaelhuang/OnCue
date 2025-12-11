package com.ait.oncue

import android.net.Uri
import android.util.Log
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
class SubmitViewModel @Inject constructor(
    private val repo: OnCueRepository
) : ViewModel() {

    var loading by mutableStateOf(false)
    var success by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun submit(promptId: String, promptType: String, text: String?, uri: Uri?) {
        Log.d("SubmitViewModel", "Submitting with promptId: $promptId, type: $promptType")
        viewModelScope.launch {
            loading = true
            val result = repo.submitPost(promptId, text, uri)
            loading = false
            success = result.isSuccess
            error = result.exceptionOrNull()?.message

            if (result.isSuccess) {
                Log.d("SubmitViewModel", "Submit successful for promptId: $promptId")
            } else {
                Log.e("SubmitViewModel", "Submit failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}