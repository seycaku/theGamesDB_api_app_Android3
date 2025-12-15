package com.example.finalproject.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.domain.model.Game
import com.example.finalproject.domain.repository.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gamesRepository: GamesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadGames()
    }

    fun loadGames() {
        loadTrendingGames()
        loadNewReleases()
        loadTopRatedGames()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )
        loadGames()
    }
    
    private fun loadTrendingGames() {
        viewModelScope.launch {
            gamesRepository.getTrendingGames()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Unknown error",
                        isLoading = false
                    )
                }
                .collect { result ->
                    result.onSuccess { games ->
                        _uiState.value = _uiState.value.copy(
                            trendingGames = games,
                            isLoading = false,
                            error = null
                        )
                    }.onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            error = e.message ?: "Failed to load trending games",
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    private fun loadNewReleases() {
        viewModelScope.launch {
            gamesRepository.getNewReleases()
                .catch { e ->
                }
                .collect { result ->
                    result.onSuccess { games ->
                        _uiState.value = _uiState.value.copy(
                            newReleases = games,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    private fun loadTopRatedGames() {
        viewModelScope.launch {
            gamesRepository.getTopRatedGames()
                .catch { e ->
                }
                .collect { result ->
                    result.onSuccess { games ->
                        _uiState.value = _uiState.value.copy(
                            topRated = games,
                            isLoading = false
                        )
                    }
                }
        }
    }
}

data class HomeUiState(
    val trendingGames: List<Game> = emptyList(),
    val newReleases: List<Game> = emptyList(),
    val topRated: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
