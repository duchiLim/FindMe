package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Close
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
fun AnimalListCard(
    animal: AbandonedAnimalUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFavoriteToggle: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteToggleClick: (() -> Unit)? = null,
    showFavoriteRemove: Boolean = false,
    onFavoriteRemoveClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    viewedAtLabel: String? = null,
    isDimmed: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp)
                    .aspectRatio(1f),
            ) {
                AnimalListCardThumbnail(
                    imageUrls = animal.imageUrls,
                    contentDescription = animal.kindName,
                    modifier = Modifier.fillMaxSize(),
                )

                if (animal.imageUrls.size > 1) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Collections,
                            contentDescription = stringResource(R.string.home_multiple_photos),
                            modifier = Modifier
                                .padding(4.dp)
                                .size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                if (isDimmed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.35f)),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = animal.kindName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ProcessStateChip(
                        processState = animal.processState,
                        statusType = animal.statusType,
                    )
                    Text(
                        text = animal.happenPlace.ifBlank {
                            stringResource(R.string.home_happen_place_unknown)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = animal.shelterName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (!viewedAtLabel.isNullOrBlank()) {
                        Text(
                            text = viewedAtLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            when {
                onDeleteClick != null -> {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.my_recent_delete_item),
                        )
                    }
                }
                showFavoriteRemove && onFavoriteRemoveClick != null -> {
                    IconButton(onClick = onFavoriteRemoveClick) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = stringResource(R.string.favorite_remove),
                            tint = FindMePrimaryVariant,
                        )
                    }
                }
                showFavoriteToggle && onFavoriteToggleClick != null -> {
                    IconButton(onClick = onFavoriteToggleClick) {
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
        }
    }
}

@Composable
private fun AnimalListCardThumbnail(
    imageUrls: List<String>,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val primaryImageUrl = imageUrls.firstOrNull()
    if (primaryImageUrl != null) {
        AsyncImage(
            model = primaryImageUrl,
            contentDescription = contentDescription,
            modifier = modifier.clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_paw),
            error = painterResource(R.drawable.ic_paw),
        )
    } else {
        Image(
            painter = painterResource(R.drawable.ic_paw),
            contentDescription = null,
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(FindMeBackground)
                .padding(16.dp),
        )
    }
}
