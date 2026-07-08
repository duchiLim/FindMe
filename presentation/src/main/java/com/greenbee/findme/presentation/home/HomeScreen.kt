package com.greenbee.findme.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.common.showSnackbarWithAutoDismiss
import com.greenbee.findme.presentation.home.component.AnimalCard
import com.greenbee.findme.presentation.home.component.AnimalCoverCard
import com.greenbee.findme.presentation.home.component.AnimalListCard
import com.greenbee.findme.presentation.home.component.HomeAdvancedFilterBottomSheet
import com.greenbee.findme.presentation.home.component.HomeCollapsingSearchFilterSection
import com.greenbee.findme.presentation.home.component.HomeCurationSection
import com.greenbee.findme.presentation.home.component.HomeGridSkeleton
import com.greenbee.findme.presentation.home.component.HomeRegionFilterBottomSheet
import com.greenbee.findme.presentation.home.component.HomeUpKindFilterBottomSheet
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.home.model.HomeSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAnimalClick: (String) -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToRecent: () -> Unit,
    onNavigateToTodayRegistered: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var searchFilterHeightPx by remember { mutableFloatStateOf(0f) }
    val isScrollHideEnabled = !uiState.isLoading &&
        uiState.errorMessage == null &&
        !uiState.isSearchEmpty &&
        !(uiState.isEmpty && !uiState.isFilterLoading)
    val searchFilterScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(initialHeightOffsetLimit = -1f),
        canScroll = { isScrollHideEnabled && searchFilterHeightPx > 0f },
    )

    LaunchedEffect(searchFilterHeightPx, isScrollHideEnabled) {
        searchFilterScrollBehavior.state.heightOffsetLimit = -searchFilterHeightPx
        if (!isScrollHideEnabled) {
            searchFilterScrollBehavior.state.heightOffset = 0f
        }
    }

    LaunchedEffect(uiState.favoriteSnackbar) {
        when (uiState.favoriteSnackbar) {
            HomeFavoriteSnackbar.ADDED -> {
                val result = snackbarHostState.showSnackbarWithAutoDismiss(
                    message = context.getString(R.string.favorite_added_message),
                    actionLabel = context.getString(R.string.favorite_view_list),
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onNavigateToFavorite()
                }
                viewModel.onFavoriteSnackbarShown()
            }
            HomeFavoriteSnackbar.REMOVED -> {
                val result = snackbarHostState.showSnackbarWithAutoDismiss(
                    message = context.getString(R.string.favorite_removed_message),
                    actionLabel = context.getString(R.string.favorite_undo),
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoRemoveFavorite()
                } else {
                    viewModel.onFavoriteSnackbarShown()
                }
            }
            null -> Unit
        }
    }

    LaunchedEffect(
        gridState,
        listState,
        uiState.layoutMode,
        uiState.canLoadMore,
        uiState.isLoading,
        uiState.isFilterLoading,
        uiState.isLoadingMore,
        uiState.animals.size,
    ) {
        if (uiState.isLoading || uiState.isFilterLoading || uiState.animals.isEmpty() || !uiState.canLoadMore) {
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
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(searchFilterScrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    windowInsets = WindowInsets(0),
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
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = stringResource(R.string.home_sort_menu),
                                )
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.home_sort_latest)) },
                                    onClick = {
                                        isSortMenuExpanded = false
                                        viewModel.setSortOrder(HomeSortOrder.LATEST)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.home_sort_notice_end)) },
                                    onClick = {
                                        isSortMenuExpanded = false
                                        viewModel.setSortOrder(HomeSortOrder.NOTICE_END_SOON)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.home_sort_status)) },
                                    onClick = {
                                        isSortMenuExpanded = false
                                        viewModel.setSortOrder(HomeSortOrder.STATUS_NOTICE_FIRST)
                                    },
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
                HomeCollapsingSearchFilterSection(
                    scrollBehavior = searchFilterScrollBehavior,
                    searchQuery = uiState.searchQuery,
                    activeRegionFilterCount = uiState.activeRegionFilterCount,
                    selectedUpKind = uiState.selectedUpKind,
                    selectedState = uiState.selectedState,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onClearSearch = viewModel::clearSearch,
                    onRegionFilterClick = viewModel::onRegionFilterClick,
                    onUpKindFilterClick = viewModel::onUpKindFilterClick,
                    onStateSelected = viewModel::onStateSelected,
                    onHeightChanged = { searchFilterHeightPx = it.toFloat() },
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
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
                listState = listState,
                onAnimalClick = onAnimalClick,
                onFavoriteToggle = viewModel::toggleFavorite,
                onRetry = viewModel::retry,
                onClearFilters = viewModel::clearFilters,
                onClearSearch = viewModel::clearSearch,
                onNavigateToTodayRegistered = onNavigateToTodayRegistered,
            )
        }
    }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    gridState: LazyGridState,
    listState: LazyListState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onRetry: () -> Unit,
    onClearFilters: () -> Unit,
    onClearSearch: () -> Unit,
    onNavigateToTodayRegistered: () -> Unit,
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

            uiState.isSearchEmpty -> {
                HomeMessageContent(
                    message = stringResource(R.string.home_search_empty),
                    actionLabel = stringResource(R.string.home_search_clear),
                    onAction = onClearSearch,
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
                when (uiState.layoutMode) {
                    HomeLayoutMode.GRID -> {
                        HomeAnimalGrid(
                            uiState = uiState,
                            gridState = gridState,
                            onAnimalClick = onAnimalClick,
                            onFavoriteToggle = onFavoriteToggle,
                            onNavigateToTodayRegistered = onNavigateToTodayRegistered,
                        )
                    }
                    HomeLayoutMode.LIST -> {
                        HomeAnimalList(
                            uiState = uiState,
                            listState = listState,
                            onAnimalClick = onAnimalClick,
                            onFavoriteToggle = onFavoriteToggle,
                            onNavigateToTodayRegistered = onNavigateToTodayRegistered,
                        )
                    }
                    HomeLayoutMode.COVER -> {
                        HomeAnimalCoverList(
                            uiState = uiState,
                            listState = listState,
                            onAnimalClick = onAnimalClick,
                            onFavoriteToggle = onFavoriteToggle,
                            onNavigateToTodayRegistered = onNavigateToTodayRegistered,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeAnimalGrid(
    uiState: HomeUiState,
    gridState: LazyGridState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onNavigateToTodayRegistered: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.searchQuery.isBlank()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeCurationSection(
                    title = stringResource(R.string.home_curation_today),
                    animals = uiState.todayRegisteredAnimals,
                    favoriteDesertionNos = uiState.favoriteDesertionNos,
                    onAnimalClick = onAnimalClick,
                    onFavoriteToggle = onFavoriteToggle,
                    onSeeMoreClick = onNavigateToTodayRegistered,
                )
            }
        }

        if (uiState.adoptionImminentAnimals.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeCurationSection(
                    title = stringResource(R.string.home_curation_imminent),
                    animals = uiState.adoptionImminentAnimals,
                    favoriteDesertionNos = uiState.favoriteDesertionNos,
                    onAnimalClick = onAnimalClick,
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        items(
            items = uiState.displayAnimals,
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

@Composable
private fun HomeAnimalCoverList(
    uiState: HomeUiState,
    listState: LazyListState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onNavigateToTodayRegistered: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.searchQuery.isBlank()) {
            item(key = "curation_today") {
                HomeCurationSection(
                    title = stringResource(R.string.home_curation_today),
                    animals = uiState.todayRegisteredAnimals,
                    favoriteDesertionNos = uiState.favoriteDesertionNos,
                    onAnimalClick = onAnimalClick,
                    onFavoriteToggle = onFavoriteToggle,
                    onSeeMoreClick = onNavigateToTodayRegistered,
                )
            }
        }

        if (uiState.adoptionImminentAnimals.isNotEmpty()) {
            item(key = "curation_imminent") {
                HomeCurationSection(
                    title = stringResource(R.string.home_curation_imminent),
                    animals = uiState.adoptionImminentAnimals,
                    favoriteDesertionNos = uiState.favoriteDesertionNos,
                    onAnimalClick = onAnimalClick,
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        items(
            items = uiState.displayAnimals,
            key = { it.desertionNo },
        ) { animal ->
            AnimalCoverCard(
                animal = animal,
                onClick = { onAnimalClick(animal.desertionNo) },
                showFavoriteToggle = true,
                isFavorite = animal.desertionNo in uiState.favoriteDesertionNos,
                onFavoriteToggleClick = { onFavoriteToggle(animal.desertionNo) },
            )
        }

        if (uiState.isLoadingMore) {
            item(key = "loading_more") {
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

@Composable
private fun HomeAnimalList(
    uiState: HomeUiState,
    listState: LazyListState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onNavigateToTodayRegistered: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
    ) {
        if (uiState.searchQuery.isBlank()) {
            item(key = "curation_today") {
                HomeCurationSection(
                    title = stringResource(R.string.home_curation_today),
                    animals = uiState.todayRegisteredAnimals,
                    favoriteDesertionNos = uiState.favoriteDesertionNos,
                    onAnimalClick = onAnimalClick,
                    onFavoriteToggle = onFavoriteToggle,
                    onSeeMoreClick = onNavigateToTodayRegistered,
                )
            }
        }

        if (uiState.adoptionImminentAnimals.isNotEmpty()) {
            item(key = "curation_imminent") {
                HomeCurationSection(
                    title = stringResource(R.string.home_curation_imminent),
                    animals = uiState.adoptionImminentAnimals,
                    favoriteDesertionNos = uiState.favoriteDesertionNos,
                    onAnimalClick = onAnimalClick,
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        items(
            items = uiState.displayAnimals,
            key = { it.desertionNo },
        ) { animal ->
            AnimalListCard(
                animal = animal,
                onClick = { onAnimalClick(animal.desertionNo) },
                showFavoriteToggle = true,
                isFavorite = animal.desertionNo in uiState.favoriteDesertionNos,
                onFavoriteToggleClick = { onFavoriteToggle(animal.desertionNo) },
            )
        }

        if (uiState.isLoadingMore) {
            item(key = "loading_more") {
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
