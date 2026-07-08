package com.greenbee.findme.presentation.favorite.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.favorite.FavoriteSortOrder
import com.greenbee.findme.presentation.favorite.FavoriteUiState
import com.greenbee.findme.presentation.home.component.AnimalCard
import com.greenbee.findme.presentation.home.component.AnimalCoverCard
import com.greenbee.findme.presentation.home.component.AnimalListCard

@Composable
internal fun FavoriteAnimalContent(
    uiState: FavoriteUiState,
    gridState: LazyGridState,
    listState: LazyListState,
    onAnimalClick: (String) -> Unit,
    onFavoriteRemoveClick: (String) -> Unit,
    onCleanupEndedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState.layoutMode) {
        HomeLayoutMode.GRID -> {
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
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FavoriteSummarySection(
                        totalCount = uiState.favorites.size,
                        endedCount = uiState.endedCount,
                        sortOrder = uiState.sortOrder,
                        onCleanupEndedClick = onCleanupEndedClick,
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
                        onFavoriteRemoveClick = { onFavoriteRemoveClick(favorite.desertionNo) },
                        isDimmed = favorite.isEnded,
                    )
                }
            }
        }

        HomeLayoutMode.LIST -> {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
            ) {
                item(key = "summary") {
                    FavoriteSummarySection(
                        totalCount = uiState.favorites.size,
                        endedCount = uiState.endedCount,
                        sortOrder = uiState.sortOrder,
                        onCleanupEndedClick = onCleanupEndedClick,
                    )
                }

                items(
                    items = uiState.favorites,
                    key = { it.desertionNo },
                ) { favorite ->
                    AnimalListCard(
                        animal = favorite.animal,
                        onClick = { onAnimalClick(favorite.desertionNo) },
                        showFavoriteRemove = true,
                        onFavoriteRemoveClick = { onFavoriteRemoveClick(favorite.desertionNo) },
                        isDimmed = favorite.isEnded,
                    )
                }
            }
        }

        HomeLayoutMode.COVER -> {
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
                item(key = "summary") {
                    FavoriteSummarySection(
                        totalCount = uiState.favorites.size,
                        endedCount = uiState.endedCount,
                        sortOrder = uiState.sortOrder,
                        onCleanupEndedClick = onCleanupEndedClick,
                    )
                }

                items(
                    items = uiState.favorites,
                    key = { it.desertionNo },
                ) { favorite ->
                    AnimalCoverCard(
                        animal = favorite.animal,
                        onClick = { onAnimalClick(favorite.desertionNo) },
                        showFavoriteRemove = true,
                        onFavoriteRemoveClick = { onFavoriteRemoveClick(favorite.desertionNo) },
                        isDimmed = favorite.isEnded,
                    )
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
