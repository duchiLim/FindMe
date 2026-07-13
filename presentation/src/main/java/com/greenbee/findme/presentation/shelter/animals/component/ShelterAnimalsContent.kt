package com.greenbee.findme.presentation.shelter.animals.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.component.AnimalCard
import com.greenbee.findme.presentation.home.component.AnimalCoverCard
import com.greenbee.findme.presentation.home.component.AnimalListCard
import com.greenbee.findme.presentation.shelter.animals.ShelterAnimalsUiState

@Composable
internal fun ShelterAnimalsContent(
    uiState: ShelterAnimalsUiState,
    gridState: LazyGridState,
    listState: LazyListState,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoadingMore) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        when (uiState.layoutMode) {
            HomeLayoutMode.GRID -> {
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
                        ShelterAnimalsTotalCount(totalCount = uiState.totalCount)
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
                            ShelterAnimalsLoadingMoreIndicator()
                        }
                    }
                }
            }

            HomeLayoutMode.LIST -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ),
                ) {
                    item(key = "total_count") {
                        ShelterAnimalsTotalCount(totalCount = uiState.totalCount)
                    }

                    items(
                        items = uiState.animals,
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
                            ShelterAnimalsLoadingMoreIndicator()
                        }
                    }
                }
            }

            HomeLayoutMode.COVER -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(key = "total_count") {
                        ShelterAnimalsTotalCount(totalCount = uiState.totalCount)
                    }

                    items(
                        items = uiState.animals,
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
                            ShelterAnimalsLoadingMoreIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelterAnimalsTotalCount(
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.shelter_animals_total_count, totalCount),
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun ShelterAnimalsLoadingMoreIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
