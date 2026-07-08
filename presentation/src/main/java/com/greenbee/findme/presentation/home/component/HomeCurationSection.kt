package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

@Composable
fun HomeCurationSection(
    title: String,
    animals: List<AbandonedAnimalUiModel>,
    favoriteDesertionNos: Set<String>,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (animals.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(
                items = animals,
                key = { it.desertionNo },
            ) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = { onAnimalClick(animal.desertionNo) },
                    showFavoriteToggle = true,
                    isFavorite = animal.desertionNo in favoriteDesertionNos,
                    onFavoriteToggleClick = { onFavoriteToggle(animal.desertionNo) },
                    modifier = Modifier.width(156.dp),
                )
            }
        }
    }
}
