package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.ui.theme.FindMeBackground
import com.greenbee.findme.presentation.ui.theme.FindMePrimaryVariant

@Composable
fun AnimalCard(
    animal: AbandonedAnimalUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFavoriteRemove: Boolean = false,
    onFavoriteRemoveClick: (() -> Unit)? = null,
    showFavoriteToggle: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteToggleClick: (() -> Unit)? = null,
    isDimmed: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(FindMeBackground),
                contentAlignment = Alignment.Center,
            ) {
                val primaryImageUrl = animal.imageUrls.firstOrNull()
                if (primaryImageUrl != null) {
                    AsyncImage(
                        model = primaryImageUrl,
                        contentDescription = animal.kindName,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_paw),
                        error = painterResource(R.drawable.ic_paw),
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_paw),
                        contentDescription = null,
                        modifier = Modifier
                            .matchParentSize()
                            .padding(32.dp),
                    )
                }

                if (animal.imageUrls.size > 1) {
                    Surface(
                        modifier = Modifier
                            .align(
                                when {
                                    showFavoriteRemove || showFavoriteToggle -> Alignment.TopStart
                                    else -> Alignment.TopEnd
                                },
                            )
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Collections,
                            contentDescription = stringResource(R.string.home_multiple_photos),
                            modifier = Modifier
                                .padding(4.dp)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                if (isDimmed) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                    )
                }

                if (showFavoriteRemove && onFavoriteRemoveClick != null) {
                    IconButton(
                        onClick = onFavoriteRemoveClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = stringResource(R.string.favorite_remove),
                            tint = FindMePrimaryVariant,
                        )
                    }
                } else if (showFavoriteToggle && onFavoriteToggleClick != null) {
                    IconButton(
                        onClick = onFavoriteToggleClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Rounded.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = stringResource(
                                if (isFavorite) R.string.favorite_remove else R.string.favorite_add,
                            ),
                            tint = if (isFavorite) FindMePrimaryVariant else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                AnimalKindTitleRow(
                    kindName = animal.kindName,
                    processState = animal.processState,
                    statusType = animal.statusType,
                )
                Text(
                    text = animal.shelterName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
