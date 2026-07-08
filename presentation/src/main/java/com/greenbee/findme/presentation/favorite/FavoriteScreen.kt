package com.greenbee.findme.presentation.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.component.AnimalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onAnimalClick: (String) -> Unit,
    onBrowseAnimalsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uiState.favorites.size) {
        if (uiState.favorites.isNotEmpty()) {
            viewModel.refreshSnapshots()
        }
    }

    LaunchedEffect(uiState.showRemovedSnackbar) {
        if (!uiState.showRemovedSnackbar) return@LaunchedEffect

        val result = snackbarHostState.showSnackbar(
            message = context.getString(R.string.favorite_removed_message),
            actionLabel = context.getString(R.string.favorite_undo),
            duration = SnackbarDuration.Short,
        )
        when (result) {
            SnackbarResult.ActionPerformed -> viewModel.undoRemoveFavorite()
            SnackbarResult.Dismissed -> viewModel.onUndoSnackbarShown()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorite_screen_title)) },
                actions = {
                    if (!uiState.isEmpty) {
                        IconButton(onClick = { isSortMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = stringResource(R.string.favorite_sort_menu),
                            )
                        }
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.favorite_sort_saved_at)) },
                                onClick = {
                                    isSortMenuExpanded = false
                                    viewModel.setSortOrder(FavoriteSortOrder.SAVED_AT_DESC)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.favorite_sort_status)) },
                                onClick = {
                                    isSortMenuExpanded = false
                                    viewModel.setSortOrder(FavoriteSortOrder.STATUS_NOTICE_FIRST)
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refreshSnapshots,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (uiState.isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (uiState.refreshFailedCount > 0) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Text(
                            text = stringResource(R.string.favorite_refresh_failed_banner),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.isEmpty -> {
                        FavoriteEmptyContent(
                            onBrowseAnimalsClick = onBrowseAnimalsClick,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                FavoriteSummarySection(
                                    totalCount = uiState.favorites.size,
                                    endedCount = uiState.endedCount,
                                    sortOrder = uiState.sortOrder,
                                    onCleanupEndedClick = viewModel::clearEndedFavorites,
                                )
                            }

                            items(
                                items = uiState.favorites,
                                key = { it.desertionNo },
                            ) { favorite ->
                                AnimalCard(
                                    animal = favorite.animal,
                                    onClick = { onAnimalClick(favorite.desertionNo) },
                                    showFavoriteRemove = true,
                                    onFavoriteRemoveClick = { viewModel.removeFavorite(favorite.desertionNo) },
                                    isDimmed = favorite.isEnded,
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
private fun FavoriteSummarySection(
    totalCount: Int,
    endedCount: Int,
    sortOrder: FavoriteSortOrder,
    onCleanupEndedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = when (sortOrder) {
                FavoriteSortOrder.SAVED_AT_DESC -> stringResource(
                    R.string.favorite_total_count,
                    totalCount,
                )
                FavoriteSortOrder.STATUS_NOTICE_FIRST -> stringResource(
                    R.string.favorite_total_count_status_sort,
                    totalCount,
                )
            },
            style = MaterialTheme.typography.titleMedium,
        )
        if (endedCount > 0) {
            Text(
                text = stringResource(R.string.favorite_ended_count, endedCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onCleanupEndedClick) {
                Text(stringResource(R.string.favorite_cleanup_ended))
            }
        }
    }
}

@Composable
private fun FavoriteEmptyContent(
    onBrowseAnimalsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.favorite_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.favorite_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = onBrowseAnimalsClick,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.favorite_browse_animals))
        }
    }
}
