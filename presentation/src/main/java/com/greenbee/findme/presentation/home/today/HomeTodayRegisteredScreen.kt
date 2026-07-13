package com.greenbee.findme.presentation.home.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.common.showSnackbarWithAutoDismiss
import com.greenbee.findme.presentation.home.HomeFavoriteSnackbar
import com.greenbee.findme.presentation.home.component.AnimalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTodayRegisteredScreen(
    onBackClick: () -> Unit,
    onAnimalClick: (String) -> Unit,
    onNavigateToFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeTodayRegisteredViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(uiState.favoriteSnackbar) {
        when (uiState.favoriteSnackbar) {
            HomeFavoriteSnackbar.ADDED -> {
                val result = snackbarHostState.showSnackbarWithAutoDismiss(
                    message = resources.getString(R.string.favorite_added_message),
                    actionLabel = resources.getString(R.string.favorite_view_list),
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onNavigateToFavorite()
                }
                viewModel.onFavoriteSnackbarShown()
            }
            HomeFavoriteSnackbar.REMOVED -> {
                val result = snackbarHostState.showSnackbarWithAutoDismiss(
                    message = resources.getString(R.string.favorite_removed_message),
                    actionLabel = resources.getString(R.string.favorite_undo),
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> viewModel.undoRemoveFavorite()
                    SnackbarResult.Dismissed -> viewModel.onFavoriteSnackbarShown()
                }
            }
            null -> Unit
        }
    }

    LaunchedEffect(gridState, uiState.canLoadMore, uiState.isLoading, uiState.isLoadingMore, uiState.animals.size) {
        if (uiState.isLoading || uiState.animals.isEmpty() || !uiState.canLoadMore) {
            return@LaunchedEffect
        }

        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }.collect { shouldLoadMore ->
            if (shouldLoadMore) {
                viewModel.loadMore()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = { Text(stringResource(R.string.home_today_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null && uiState.animals.isEmpty() -> {
                    TodayRegisteredMessage(
                        message = uiState.errorMessage.orEmpty(),
                        actionLabel = stringResource(R.string.home_retry),
                        onAction = viewModel::retry,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                uiState.isEmpty -> {
                    TodayRegisteredMessage(
                        message = stringResource(R.string.home_today_empty),
                        actionLabel = stringResource(R.string.detail_back),
                        onAction = onBackClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                    TodayRegisteredGrid(
                        uiState = uiState,
                        gridState = gridState,
                        onAnimalClick = onAnimalClick,
                        onFavoriteToggle = viewModel::toggleFavorite,
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayRegisteredGrid(
    uiState: HomeTodayRegisteredUiState,
    gridState: LazyGridState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoadingMore) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = uiState.animals,
                key = { it.desertionNo },
            ) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = { onAnimalClick(animal.desertionNo) },
                    showFavoriteToggle = true,
                    isFavorite = animal.desertionNo in uiState.favoriteDesertionNos,
                    onFavoriteToggleClick = { onFavoriteToggle(animal.desertionNo) },
                )
            }

            if (uiState.isLoadingMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayRegisteredMessage(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onAction,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(actionLabel)
        }
    }
}

private const val LOAD_MORE_THRESHOLD = 4
