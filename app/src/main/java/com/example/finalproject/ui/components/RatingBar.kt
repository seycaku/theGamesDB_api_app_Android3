package com.example.finalproject.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalproject.ui.theme.RatingHigh
import com.example.finalproject.ui.theme.RatingLow
import com.example.finalproject.ui.theme.RatingMedium


@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        rating >= 4.0f -> RatingHigh
        rating >= 3.0f -> RatingMedium
        else -> RatingLow
    }
    
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "Rating: $rating",
        modifier = modifier.size(16.dp),
        tint = color
    )
}
