package com.greenbee.findme.presentation.my.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.home.component.AnimalCard
import com.greenbee.findme.presentation.home.component.AnimalCoverCard
import com.greenbee.findme.presentation.home.component.AnimalListCard
import com.greenbee.findme.presentation.my.model.RecentAnimalUiModel
import com.greenbee.findme.presentation.my.model.toAnimalUiModel

@Composable
internal fun MyRecentAnimalContent(
    layoutMode: HomeLayoutMode,
    recentAnimals: List<RecentAnimalUiModel>,
    gridState: LazyGridState,
    listState: LazyListState,
    onAnimalClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (layoutMode) {
        HomeLayoutMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = recentAnimals,
                    key = { it.desertionNo },
                ) { animal ->
                    AnimalCard(
                        animal = animal.toAnimalUiModel(),
                        onClick = { onAnimalClick(animal.desertionNo) },
                        onDeleteClick = { onDeleteClick(animal.desertionNo) },
                    )
                }
            }
        }
        HomeLayoutMode.LIST -> {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(
                    items = recentAnimals,
                    key = { it.desertionNo },
                ) { animal ->
                    AnimalListCard(
                        animal = animal.toAnimalUiModel(),
                        onClick = { onAnimalClick(animal.desertionNo) },
                        onDeleteClick = { onDeleteClick(animal.desertionNo) },
                        viewedAtLabel = animal.viewedAtLabel,
                    )
                }
            }
        }
        HomeLayoutMode.COVER -> {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = recentAnimals,
                    key = { it.desertionNo },
                ) { animal ->
                    AnimalCoverCard(
                        animal = animal.toAnimalUiModel(),
                        onClick = { onAnimalClick(animal.desertionNo) },
                        onDeleteClick = { onDeleteClick(animal.desertionNo) },
                    )
                }
            }
        }
    }
}
