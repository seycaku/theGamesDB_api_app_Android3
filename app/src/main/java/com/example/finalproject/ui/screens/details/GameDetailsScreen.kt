package com.example.finalproject.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.finalproject.domain.model.Game
import com.example.finalproject.ui.components.ErrorState
import com.example.finalproject.ui.components.GameCard
import com.example.finalproject.ui.components.LoadingState
import com.example.finalproject.ui.components.RatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    onGameClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: GameDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.game?.name ?: "Game Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.game == null -> {
                LoadingState(message = "Loading game details...")
            }
            
            uiState.error != null && uiState.game == null -> {
                ErrorState(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.refresh() }
                )
            }

            uiState.game != null -> {
                val game = uiState.game!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        GameDetailsHeader(
                            game = game,
                            isInWishlist = uiState.isInWishlist,
                            onWishlistToggle = { viewModel.toggleWishlist() }
                        )
                    }

                    item {
                        GameInfoSection(
                            game = game,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    item {
                        if (uiState.screenshots.isNotEmpty()) {
                            ScreenshotsGallery(
                                screenshots = uiState.screenshots,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }

                    item {
                        if (uiState.similarGames.isNotEmpty()) {
                            SimilarGamesSection(
                                similarGames = uiState.similarGames,
                                onGameClick = onGameClick,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameDetailsHeader(
    game: Game,
    isInWishlist: Boolean,
    onWishlistToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = game.backgroundImage,
            contentDescription = "${game.name} background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = game.name,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RatingBar(rating = game.rating)
                Text(
                    text = String.format("%.1f", game.rating),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                
                if (game.released != null) {
                    Text(
                        text = "• ${game.released}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FilledTonalButton(
                onClick = onWishlistToggle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isInWishlist) "Remove from wishlist" else "Add to wishlist"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isInWishlist) "Remove from Wishlist" else "Add to Wishlist"
                )
            }
        }
    }
}

@Composable
fun GameInfoSection(
    game: Game,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (game.genres.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Genres",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(game.genres) { genre ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = genre,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
        
        if (game.platforms.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Platforms",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(game.platforms) { platform ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = platform,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
        
        if (!game.description.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )
                var expanded by remember { mutableStateOf(false) }
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 5,
                    overflow = TextOverflow.Ellipsis
                )
                if (game.description.length > 200) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(if (expanded) "Show Less" else "Show More")
                    }
                }
            }
        }
        
        if (game.metacritic != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Metacritic:",
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        game.metacritic >= 75 -> Color(0xFF66BB6A)
                        game.metacritic >= 50 -> Color(0xFFFFA726)
                        else -> Color(0xFFEF5350)
                    }
                ) {
                    Text(
                        text = game.metacritic.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenshotsGallery(
    screenshots: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectedIndex = remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                listState.layoutInfo.visibleItemsInfo.first().index
            } else {
                0
            }
        }
    }
    
    Column(modifier = modifier) {
        Text(
            text = "Screenshots",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        LazyRow(
            state = listState,
            modifier = Modifier.height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(screenshots.size) { index ->
                AsyncImage(
                    model = screenshots[index],
                    contentDescription = "Screenshot ${index + 1}",
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        if (screenshots.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(screenshots.size) { iteration ->
                    val color = if (selectedIndex.value == iteration) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                            .clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(iteration)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarGamesSection(
    similarGames: List<Game>,
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Similar Games",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(similarGames) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClick(game.id) },
                    modifier = Modifier.width(300.dp)
                )
            }
        }
    }
}
