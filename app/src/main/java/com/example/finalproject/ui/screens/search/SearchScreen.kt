package com.example.finalproject.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.finalproject.ui.components.EmptyState
import com.example.finalproject.ui.components.ErrorState
import com.example.finalproject.ui.components.GameCard
import com.example.finalproject.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onGameClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Games") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.query,
                onQueryChange = { viewModel.updateQuery(it) },
                onClear = { viewModel.clearSearch() },
                modifier = Modifier.padding(16.dp)
            )
            
            if (uiState.query.isNotBlank() || uiState.availableGenres.isNotEmpty()) {
                FiltersAndSortRow(
                    availableGenres = uiState.availableGenres,
                    selectedGenre = uiState.selectedGenre,
                    sortBy = uiState.sortBy,
                    onGenreSelected = { viewModel.selectGenre(it) },
                    onSortSelected = { viewModel.setSortBy(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            if (uiState.query.isBlank() && uiState.recentSearches.isNotEmpty()) {
                RecentSearchesSection(
                    recentSearches = uiState.recentSearches,
                    onSearchClick = { viewModel.selectRecentSearch(it) },
                    onClear = { viewModel.clearRecentSearches() },
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            when {
                uiState.isLoading && uiState.results.isEmpty() -> {
                    LoadingState(message = "Searching games...")
                }
                
                uiState.error != null && uiState.results.isEmpty() -> {
                    ErrorState(
                        message = uiState.error ?: "Unknown error",
                        onRetry = {
                            if (uiState.query.isNotBlank()) {
                                viewModel.updateQuery(uiState.query)
                            }
                        }
                    )
                }
                
                uiState.query.isNotBlank() && uiState.results.isEmpty() && !uiState.isLoading -> {
                    EmptyState(
                        message = "No games found for \"${uiState.query}\"",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                uiState.results.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.results) { game ->
                            GameCard(
                                game = game,
                                onClick = { onGameClick(game.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search games...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun FiltersAndSortRow(
    availableGenres: List<com.example.finalproject.domain.model.Genre>,
    selectedGenre: com.example.finalproject.domain.model.Genre?,
    sortBy: SortOption,
    onGenreSelected: (com.example.finalproject.domain.model.Genre?) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (availableGenres.isNotEmpty()) {
            Text(
                text = "Genres",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedGenre == null,
                        onClick = { onGenreSelected(null) },
                        label = { Text("All") }
                    )
                }
                items(availableGenres.take(10)) { genre ->
                    FilterChip(
                        selected = selectedGenre?.id == genre.id,
                        onClick = { onGenreSelected(genre) },
                        label = { 
                            Text(
                                text = genre.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) 
                        }
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort by:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            var expanded by remember { mutableStateOf(false) }
            
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(
                        text = when (sortBy) {
                            SortOption.RATING -> "Rating"
                            SortOption.RELEASE_DATE -> "Release Date"
                            SortOption.NAME -> "Name"
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sort options",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (option) {
                                        SortOption.RATING -> "Rating"
                                        SortOption.RELEASE_DATE -> "Release Date"
                                        SortOption.NAME -> "Name"
                                    }
                                )
                            },
                            onClick = {
                                onSortSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSearchesSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onClear) {
                Text("Clear")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        recentSearches.forEach { search ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick(search) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = search,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}