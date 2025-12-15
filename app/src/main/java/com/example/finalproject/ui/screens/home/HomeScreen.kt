package com.example.finalproject.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.finalproject.domain.model.Game
import com.example.finalproject.ui.components.ErrorState
import com.example.finalproject.ui.components.GameCard
import com.example.finalproject.ui.components.LoadingState
import com.example.finalproject.util.ShakeDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGameClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showAllTrending by remember { mutableStateOf(false) }
    var showAllNewReleases by remember { mutableStateOf(false) }
    var showAllTopRated by remember { mutableStateOf(false) }
    
    var showRandomGameDialog by remember { mutableStateOf(false) }
    var randomGame by remember { mutableStateOf<Game?>(null) }
    
    val pullToRefreshState = rememberPullToRefreshState()
    
    val allGames = uiState.trendingGames + uiState.newReleases + uiState.topRated
    
    val currentAllGames by androidx.compose.runtime.rememberUpdatedState(allGames)
    
    DisposableEffect(context) {
        val shakeDetector = ShakeDetector(context) {
            if (currentAllGames.isNotEmpty()) {
                randomGame = currentAllGames.random()
                showRandomGameDialog = true
            } else {
                Toast.makeText(context, "Shake to discover! Loading games...", Toast.LENGTH_SHORT).show()
            }
        }
        shakeDetector.start()
        
        onDispose {
            shakeDetector.stop()
        }
    }
    
    if (showRandomGameDialog && randomGame != null) {
        RandomGameDialog(
            game = randomGame!!,
            onDismiss = { showRandomGameDialog = false },
            onViewGame = { 
                showRandomGameDialog = false
                onGameClick(randomGame!!.id)
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Steam Mobile") },
                actions = {
                    IconButton(onClick = {
                        val allGames = uiState.trendingGames + uiState.newReleases + uiState.topRated
                        if (allGames.isNotEmpty()) {
                            randomGame = allGames.random()
                            showRandomGameDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Random Game"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.trendingGames.isEmpty() -> {
                    LoadingState()
                }
                
                uiState.error != null && uiState.trendingGames.isEmpty() -> {
                    ErrorState(
                        message = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.refresh() }
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        if (uiState.trendingGames.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Trending Now",
                                    showAll = showAllTrending,
                                    itemCount = uiState.trendingGames.size,
                                    onToggle = { showAllTrending = !showAllTrending }
                                )
                            }
                            item {
                                val displayGames = if (showAllTrending) uiState.trendingGames else uiState.trendingGames.take(5)
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(displayGames) { game ->
                                        GameCard(
                                            game = game,
                                            onClick = { onGameClick(game.id) },
                                            modifier = Modifier.width(280.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (uiState.newReleases.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "New Releases",
                                    showAll = showAllNewReleases,
                                    itemCount = uiState.newReleases.size,
                                    onToggle = { showAllNewReleases = !showAllNewReleases }
                                )
                            }
                            val displayGames = if (showAllNewReleases) uiState.newReleases else uiState.newReleases.take(3)
                            items(displayGames) { game ->
                                GameCard(
                                    game = game,
                                    onClick = { onGameClick(game.id) }
                                )
                            }
                        }
                        
                        if (uiState.topRated.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Top Rated",
                                    showAll = showAllTopRated,
                                    itemCount = uiState.topRated.size,
                                    onToggle = { showAllTopRated = !showAllTopRated }
                                )
                            }
                            val displayGames = if (showAllTopRated) uiState.topRated else uiState.topRated.take(3)
                            items(displayGames) { game ->
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
}

@Composable
fun SectionHeader(
    title: String,
    showAll: Boolean,
    itemCount: Int,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        if (itemCount > 3) {
            TextButton(onClick = onToggle) {
                Text(
                    text = if (showAll) "Show Less" else "See All ($itemCount)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RandomGameDialog(
    game: Game,
    onDismiss: () -> Unit,
    onViewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🎲 Random Pick!")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = game.backgroundImage,
                    contentDescription = game.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleLarge
                )
                if (game.genres.isNotEmpty()) {
                    Text(
                        text = game.genres.take(3).joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "⭐ ${String.format("%.1f", game.rating)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            Button(onClick = onViewGame) {
                Text("View Game")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
