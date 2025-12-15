package com.example.finalproject.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.domain.model.Game
import com.example.finalproject.domain.repository.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val gamesRepository: GamesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val gameId: Int = savedStateHandle.get<Int>("gameId") ?: 0
    
    private val _uiState = MutableStateFlow(GameDetailsUiState())
    val uiState: StateFlow<GameDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadGameDetails()
        checkWishlistStatus()
    }

    private fun loadGameDetails() {
        if (gameId == 0) {
            _uiState.value = _uiState.value.copy(
                error = "Invalid game ID",
                isLoading = false
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            gamesRepository.getGameDetails(gameId)
                .onSuccess { game ->
                    _uiState.value = _uiState.value.copy(
                        game = game,
                        isInWishlist = game.isInWishlist
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to load game details",
                        isLoading = false
                    )
                }
            
            gamesRepository.getGameScreenshots(gameId)
                .onSuccess { screenshots ->
                    _uiState.value = _uiState.value.copy(
                        screenshots = screenshots,
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            
            gamesRepository.getSimilarGames(gameId)
                .onSuccess { similarGames ->
                    _uiState.value = _uiState.value.copy(
                        similarGames = similarGames
                    )
                }
                .onFailure {
                }
        }
    }

    private fun checkWishlistStatus() {
        viewModelScope.launch {
            val isInWishlist = gamesRepository.isInWishlist(gameId)
            _uiState.value = _uiState.value.copy(isInWishlist = isInWishlist)
        }
    }

    fun toggleWishlist() {
        val currentGame = _uiState.value.game
        if (currentGame == null) return
        
        viewModelScope.launch {
            if (_uiState.value.isInWishlist) {
                gamesRepository.removeFromWishlist(gameId)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isInWishlist = false,
                            game = currentGame.copy(isInWishlist = false, addedToWishlistAt = null)
                        )
                    }
            } else {
                gamesRepository.addToWishlist(currentGame)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isInWishlist = true,
                            game = currentGame.copy(
                                isInWishlist = true,
                                addedToWishlistAt = System.currentTimeMillis()
                            )
                        )
                    }
            }
        }
    }

    fun refresh() {
        loadGameDetails()
        checkWishlistStatus()
    }
}

data class GameDetailsUiState(
    val game: Game? = null,
    val screenshots: List<String> = emptyList(),
    val similarGames: List<Game> = emptyList(),
    val isInWishlist: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
