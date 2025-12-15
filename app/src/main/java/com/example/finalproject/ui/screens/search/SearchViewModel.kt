package com.example.finalproject.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.domain.model.Game
import com.example.finalproject.domain.model.Genre
import com.example.finalproject.domain.repository.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val gamesRepository: GamesRepository
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
    }
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null
    
    init {
        loadGenres()
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery.collect { query ->
                searchJob?.cancel()
                
                _uiState.value = _uiState.value.copy(query = query)
                
                if (query.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        results = emptyList(),
                        isLoading = false
                    )
                } else {
                    searchJob = viewModelScope.launch {
                        delay(SEARCH_DEBOUNCE_DELAY)
                        performSearch(query)
                    }
                }
            }
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
        
        if (query.isNotBlank() && !_uiState.value.recentSearches.contains(query)) {
            val updatedRecent = (_uiState.value.recentSearches + query)
                .takeLast(10)
            _uiState.value = _uiState.value.copy(recentSearches = updatedRecent)
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        val ordering = when (_uiState.value.sortBy) {
            SortOption.RATING -> "-rating"
            SortOption.RELEASE_DATE -> "-released"
            SortOption.NAME -> "name"
        }
        
        val result = gamesRepository.searchGames(
            query = query,
            genreId = _uiState.value.selectedGenre?.id,
            ordering = ordering
        )
        
        result.onSuccess { games ->
            _uiState.value = _uiState.value.copy(
                results = games,
                isLoading = false,
                error = null
            )
        }.onFailure { e ->
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "Search failed",
                isLoading = false
            )
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            gamesRepository.getGenres()
                .onSuccess { genres ->
                    _uiState.value = _uiState.value.copy(availableGenres = genres)
                }
                .onFailure { e ->
                }
        }
    }

    fun selectGenre(genre: Genre?) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre)
        if (_searchQuery.value.isNotBlank()) {
            viewModelScope.launch {
                performSearch(_searchQuery.value)
            }
        }
    }
    

    fun setSortBy(sortBy: SortOption) {
        _uiState.value = _uiState.value.copy(sortBy = sortBy)
        if (_searchQuery.value.isNotBlank()) {
            viewModelScope.launch {
                performSearch(_searchQuery.value)
            }
        }
    }

    fun clearSearch() {
        updateQuery("")
    }

    fun selectRecentSearch(query: String) {
        updateQuery(query)
    }

    fun clearRecentSearches() {
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }
}

data class SearchUiState(
    val query: String = "",
    val results: List<Game> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val availableGenres: List<Genre> = emptyList(),
    val selectedGenre: Genre? = null,
    val sortBy: SortOption = SortOption.RATING,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SortOption {
    RATING,
    RELEASE_DATE,
    NAME
}