package com.example.finalproject.ui.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.domain.model.Game
import com.example.finalproject.domain.repository.GamesRepository
import com.example.finalproject.domain.repository.WishlistSortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val gamesRepository: GamesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()
    
    init {
        loadWishlistGames()
    }

    private fun loadWishlistGames() {
        viewModelScope.launch {
            gamesRepository.getWishlistGamesSorted(_uiState.value.sortBy)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to load wishlist",
                        isLoading = false
                    )
                }
                .collect { games ->
                    _uiState.value = _uiState.value.copy(
                        games = games,
                        isEmpty = games.isEmpty(),
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun setSortBy(sortBy: WishlistSortOption) {
        _uiState.value = _uiState.value.copy(sortBy = sortBy, isLoading = true)
        viewModelScope.launch {
            gamesRepository.getWishlistGamesSorted(sortBy)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to load wishlist",
                        isLoading = false
                    )
                }
                .collect { games ->
                    _uiState.value = _uiState.value.copy(
                        games = games,
                        isEmpty = games.isEmpty(),
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun removeFromWishlist(gameId: Int) {
        viewModelScope.launch {
            gamesRepository.removeFromWishlist(gameId)
                .onSuccess {
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to remove game"
                    )
                }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            _uiState.value.games.forEach { game ->
                gamesRepository.removeFromWishlist(game.id)
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadWishlistGames()
    }
}

data class WishlistUiState(
    val games: List<Game> = emptyList(),
    val sortBy: WishlistSortOption = WishlistSortOption.DATE_ADDED,
    val isEmpty: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)