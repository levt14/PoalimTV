package com.lev.poalimtv.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.ui.UiState
import com.lev.poalimtv.ui.components.MediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onDismiss: () -> Unit,
    onMediaClick: (MediaItem) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    title = {
                        TextField(
                            value = query,
                            onValueChange = viewModel::onQueryChange,
                            placeholder = { Text("Search movies & shows…") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                        )
                    },
                    actions = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                )
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

            when {
                query.isBlank() -> CenteredHint(
                    text = "Search for movies and TV shows",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = contentModifier,
                )
                else -> when (val state = results) {
                    is UiState.Loading -> Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }

                    is UiState.Error -> CenteredHint(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = contentModifier,
                    )

                    is UiState.Success -> if (state.data.isEmpty()) {
                        CenteredHint(
                            text = "No results for \"$query\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = contentModifier,
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = contentModifier,
                        ) {
                            items(
                                items = state.data,
                                key = { "${it.id}_${it.mediaType}" },
                            ) { item ->
                                MediaCard(
                                    item = item,
                                    onClick = { onMediaClick(item) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun CenteredHint(text: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
        )
    }
}
