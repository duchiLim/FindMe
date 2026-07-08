package com.greenbee.findme.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import com.greenbee.findme.presentation.home.component.HomeAdvancedFilterBottomSheet
import com.greenbee.findme.presentation.home.component.HomeFilterBar
import com.greenbee.findme.presentation.home.component.HomeGridSkeleton
import com.greenbee.findme.presentation.home.component.HomeRegionFilterBottomSheet
import com.greenbee.findme.presentation.home.component.HomeUpKindFilterBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAnimalClick: (String) -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToRecent: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.favoriteSnackbar) {
        when (uiState.favoriteSnackbar) {
            HomeFavoriteSnackbar.ADDED -> {
                val result = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.favorite_added_message),
                    actionLabel = context.getString(R.string.favorite_view_list),
                )
                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    onNavigateToFavorite()
                }
                viewModel.onFavoriteSnackbarShown()
            }
            HomeFavoriteSnackbar.REMOVED -> {
                val result = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.favorite_removed_message),
                    actionLabel = context.getString(R.string.favorite_undo),
                )
                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    viewModel.undoRemoveFavorite()
                } else {
                    viewModel.onFavoriteSnackbarShown()
                }
            }
            null -> Unit
        }
    }

    LaunchedEffect(gridState, uiState.canLoadMore, uiState.isLoading, uiState.isFilterLoading, uiState.isLoadingMore, uiState.animals.size) {
        if (uiState.isLoading || uiState.isFilterLoading || uiState.animals.isEmpty() || !uiState.canLoadMore) {
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_screen_title)) },
                actions = {
                    if (uiState.isRecentViewingEnabled) {
                        IconButton(onClick = onNavigateToRecent) {
                            Icon(
                                imageVector = Icons.Rounded.Visibility,
                                contentDescription = stringResource(R.string.home_recent_menu),
                            )
                        }
                    }
                    BadgedBox(
                        badge = {
                            if (uiState.activeAdvancedFilterCount > 0) {
                                Badge { Text(uiState.activeAdvancedFilterCount.toString()) }
                            }
                        },
                    ) {
                        IconButton(onClick = viewModel::onAdvancedFilterClick) {
                            Icon(
                                imageVector = Icons.Rounded.FilterList,
                                contentDescription = stringResource(R.string.home_filter_menu),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            HomeFilterBar(
            activeRegionFilterCount = uiState.activeRegionFilterCount,
            selectedUpKind = uiState.selectedUpKind,
            selectedState = uiState.selectedState,
            onRegionFilterClick = viewModel::onRegionFilterClick,
            onUpKindFilterClick = viewModel::onUpKindFilterClick,
            onStateSelected = viewModel::onStateSelected,
        )

        HomeRegionFilterBottomSheet(
            visible = uiState.isRegionFilterSheetVisible,
            sidoList = uiState.sidoList,
            sigunguList = uiState.sigunguList,
            draftSido = uiState.draftSido,
            draftSigungu = uiState.draftSigungu,
            isSidoListLoading = uiState.isSidoListLoading,
            isSigunguListLoading = uiState.isSigunguListLoading,
            onDismiss = viewModel::onRegionFilterDismiss,
            onDraftSidoSelected = viewModel::onDraftSidoSelected,
            onDraftSigunguSelected = viewModel::onDraftSigunguSelected,
            onReset = viewModel::onResetRegionDraft,
            onApply = viewModel::onApplyRegionFilter,
        )

        HomeUpKindFilterBottomSheet(
            visible = uiState.isUpKindFilterSheetVisible,
            draftUpKind = uiState.draftUpKind,
            onDismiss = viewModel::onUpKindFilterDismiss,
            onDraftUpKindSelected = viewModel::onDraftUpKindSelected,
            onReset = viewModel::onResetUpKindDraft,
            onApply = viewModel::onApplyUpKindFilter,
        )

        HomeAdvancedFilterBottomSheet(
            visible = uiState.isAdvancedFilterSheetVisible,
            sidoList = uiState.sidoList,
            sigunguList = uiState.sigunguList,
            draftSido = uiState.draftSido,
            draftSigungu = uiState.draftSigungu,
            draftUpKind = uiState.draftUpKind,
            draftState = uiState.draftState,
            draftDateRange = uiState.draftDateRange,
            isSidoListLoading = uiState.isSidoListLoading,
            isSigunguListLoading = uiState.isSigunguListLoading,
            onDismiss = viewModel::onAdvancedFilterDismiss,
            onDraftSidoSelected = viewModel::onDraftSidoSelected,
            onDraftSigunguSelected = viewModel::onDraftSigunguSelected,
            onDraftUpKindSelected = viewModel::onDraftUpKindSelected,
            onDraftStateSelected = viewModel::onDraftStateSelected,
            onDraftDateRangeSelected = viewModel::onDraftDateRangeSelected,
            onReset = viewModel::onResetAdvancedFilterDraft,
            onApply = viewModel::onApplyAdvancedFilter,
        )

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            HomeContent(
                uiState = uiState,
                gridState = gridState,
                onAnimalClick = onAnimalClick,
                onFavoriteToggle = viewModel::toggleFavorite,
                onRetry = viewModel::retry,
                onClearFilters = viewModel::clearFilters,
            )
        }
    }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    gridState: LazyGridState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onRetry: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.isFilterLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (uiState.showOfflineBanner) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Text(
                    text = stringResource(R.string.home_offline_banner),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }

        when {
            uiState.isLoading -> {
                HomeGridSkeleton(modifier = Modifier.fillMaxSize())
            }

            uiState.errorMessage != null && uiState.animals.isEmpty() -> {
                HomeMessageContent(
                    message = uiState.errorMessage.orEmpty(),
                    actionLabel = stringResource(R.string.home_retry),
                    onAction = onRetry,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.isEmpty && !uiState.isFilterLoading -> {
                HomeMessageContent(
                    message = stringResource(R.string.home_empty),
                    actionLabel = stringResource(R.string.home_reset_filter),
                    onAction = onClearFilters,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
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
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        HomeResultSummary(
                            totalCount = uiState.totalCount,
                            filterSummary = uiState.filterSummary,
                        )
                    }

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
    }
}

@Composable
private fun HomeResultSummary(
    totalCount: Int,
    filterSummary: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.home_total_count, totalCount),
            style = MaterialTheme.typography.titleMedium,
        )
        if (filterSummary.isNotBlank()) {
            Text(
                text = filterSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeMessageContent(
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
