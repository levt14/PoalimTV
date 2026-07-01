package com.lev.poalimtv.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lev.poalimtv.domain.model.MediaType
import com.lev.poalimtv.ui.UiState
import androidx.core.net.toUri

private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280"
private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val detailState by viewModel.detail.collectAsState()
    val trailerKey by viewModel.trailerKey.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val context = LocalContext.current

    Scaffold { innerPadding ->
        when (val state = detailState) {
            is UiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is UiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onBack) { Text("Go Back") }
                }
            }

            is UiState.Success -> {
                val detail = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Backdrop with back button overlay
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = detail.backdropPath?.let { "$BACKDROP_BASE_URL$it" }
                                ?: detail.posterPath?.let { "$POSTER_BASE_URL$it" },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                        )
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        // Title
                        Text(
                            text = detail.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(Modifier.height(8.dp))

                        // Rating + year + runtime/seasons row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "%.1f".format(detail.rating),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            detail.releaseDate?.take(4)?.let { year ->
                                Text(
                                    text = "• $year",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            detail.runtime?.let { mins ->
                                Text(
                                    text = "• ${formatRuntime(mins)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            detail.numberOfSeasons?.let { seasons ->
                                Text(
                                    text = "• $seasons season${if (seasons != 1) "s" else ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        // Genres
                        if (detail.genres.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = detail.genres.joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        // Status
                        detail.status?.let { status ->
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = status,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Overview
                        Text(
                            text = detail.overview,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Spacer(Modifier.height(24.dp))

                        // Play Trailer + Share
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (trailerKey != null) {
                                Button(
                                    onClick = {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                "https://www.youtube.com/watch?v=$trailerKey".toUri(),
                                            )
                                        )
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Play Trailer")
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    val typeSegment = if (detail.mediaType == MediaType.MOVIE) "movie" else "tv"
                                    val url = "https://www.themoviedb.org/$typeSegment/${detail.id}"
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "${detail.title}\n$url")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, null))
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Share")
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Favorite toggle
                        OutlinedButton(
                            onClick = { viewModel.toggleFavorite(detail) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites")
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

private fun formatRuntime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
