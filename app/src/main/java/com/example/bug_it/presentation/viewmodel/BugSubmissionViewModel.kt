package com.example.bug_it.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bug_it.domain.model.Bug
import com.example.bug_it.domain.repository.BugRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BugSubmissionViewModel @Inject constructor(
    private val repository: BugRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BugSubmissionUiState>(BugSubmissionUiState.Initial)
    val uiState: StateFlow<BugSubmissionUiState> = _uiState

    fun submitBug(title: String, description: String, imageUri: String?) {
        viewModelScope.launch {
            _uiState.value = BugSubmissionUiState.Loading

            try {
                val imageUrl = imageUri?.let { uri ->
                    repository.uploadImage(uri).getOrNull()
                } ?: ""

                val bug = Bug(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    imageUrl = imageUrl
                )

                repository.submitBug(bug).fold(
                    onSuccess = {
                        _uiState.value = BugSubmissionUiState.Success
                    },
                    onFailure = { error ->
                        Log.e("BugSubmit", "Submission failed", error)
                        _uiState.value = BugSubmissionUiState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = BugSubmissionUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class BugSubmissionUiState {
    data object Initial : BugSubmissionUiState()
    data object Loading : BugSubmissionUiState()
    data object Success : BugSubmissionUiState()
    data class Error(val message: String) : BugSubmissionUiState()
} 