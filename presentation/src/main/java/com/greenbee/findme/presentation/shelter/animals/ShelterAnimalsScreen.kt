package com.greenbee.findme.presentation.shelter.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.shelter.animals.component.ShelterAnimalsContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterAnimalsScreen(
    onBackClick: () -> Unit,
    onAnimalClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShelterAnimalsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()

    LaunchedEffect(
        gridState,
        listState,
        uiState.layoutMode,
        uiState.canLoadMore,
        uiState.isLoading,
        uiState.isLoadingMore,
        uiState.animals.size,
    ) {
        if (uiState.isLoading || uiState.animals.isEmpty() || !uiState.canLoadMore) {
            return@LaunchedEffect
        }

        snapshotFlow {
            when (uiState.layoutMode) {
                HomeLayoutMode.GRID -> {
                    val layoutInfo = gridState.layoutInfo
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
                }
                HomeLayoutMode.LIST, HomeLayoutMode.COVER -> {
                    val layoutInfo = listState.layoutInfo
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
                }
            }
        }.collect { shouldLoadMore ->
            if (shouldLoadMore) {
                viewModel.loadMore()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Column {
                        Text(
                            text = if (uiState.shelterName.isNotBlank()) {
                                uiState.shelterName
                            } else {
                                stringResource(R.string.shelter_animals_title_fallback)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = stringResource(R.string.shelter_animals_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                },
                actions = {
                    if (uiState.animals.isNotEmpty()) {
                        IconButton(onClick = viewModel::toggleLayoutMode) {
                            val (icon, contentDescriptionRes) = when (uiState.layoutMode) {
                                HomeLayoutMode.GRID -> {
                                    Icons.AutoMirrored.Rounded.ViewList to R.string.home_layout_switch_to_list
                                }
                                HomeLayoutMode.LIST -> {
                                    Icons.Rounded.Photo to R.string.home_layout_switch_to_cover
                                }
                                HomeLayoutMode.COVER -> {
                                    Icons.Rounded.GridView to R.string.home_layout_switch_to_grid
                                }
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = stringResource(contentDescriptionRes),
                            )
                        }
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
                    ShelterAnimalsMessage(
                        message = uiState.errorMessage.orEmpty(),
                        actionLabel = stringResource(R.string.home_retry),
                        onAction = viewModel::retry,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                uiState.isEmpty -> {
                    ShelterAnimalsMessage(
                        message = stringResource(R.string.shelter_animals_empty),
                        actionLabel = stringResource(R.string.detail_back),
                        onAction = onBackClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                    ShelterAnimalsContent(
                        uiState = uiState,
                        gridState = gridState,
                        listState = listState,
                        onAnimalClick = onAnimalClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShelterAnimalsMessage(
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
