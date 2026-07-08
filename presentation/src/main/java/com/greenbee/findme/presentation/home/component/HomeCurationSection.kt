package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.ui.theme.FindMeBackground

@Composable
fun HomeCurationSection(
    title: String,
    animals: List<AbandonedAnimalUiModel>,
    favoriteDesertionNos: Set<String>,
    onAnimalClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onSeeMoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (animals.isEmpty() && onSeeMoreClick == null) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            if (onSeeMoreClick != null) {
                TextButton(onClick = onSeeMoreClick) {
                    Text(stringResource(R.string.home_curation_see_more))
                }
            }
        }

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

            if (onSeeMoreClick != null) {
                item(key = "see_more") {
                    HomeCurationSeeMoreCard(
                        onClick = onSeeMoreClick,
                        modifier = Modifier.width(156.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeCurationSeeMoreCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(FindMeBackground),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_home_today_see_more),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Text(
                text = stringResource(R.string.home_curation_see_more),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            )
        }
    }
}
