package com.example.bug_it.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bug_it.domain.model.Bug
import com.example.bug_it.domain.repository.BugRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BugsViewModel @Inject constructor(
    private val repository: BugRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BugsUiState>(BugsUiState.Loading)
    val uiState: StateFlow<BugsUiState> = _uiState.asStateFlow()

    init {
        getBugs()
    }

    fun getBugs() {
        viewModelScope.launch {
            try {
                _uiState.value = BugsUiState.Loading
                val bugs = repository.getBugs().first()
                _uiState.value = BugsUiState.Success(bugs)
            } catch (e: Exception) {
                _uiState.value = BugsUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class BugsUiState {
    data object Loading : BugsUiState()
    data class Success(val bugs: List<Bug>) : BugsUiState()
    data class Error(val message: String) : BugsUiState()
} 