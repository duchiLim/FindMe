package com.greenbee.findme.presentation.my.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.component.AnimalKindTitleRow
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.my.model.RecentAnimalUiModel
import com.greenbee.findme.presentation.ui.theme.FindMeBackground

@Composable
fun RecentAnimalCard(
    animal: RecentAnimalUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RecentAnimalCardContent(
        kindName = animal.kindName,
        processState = animal.processState,
        statusType = animal.statusType,
        shelterName = animal.shelterName,
        imageUrls = animal.imageUrls,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun RecentAnimalListItem(
    animal: RecentAnimalUiModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RecentAnimalThumbnail(
                imageUrls = animal.imageUrls,
                contentDescription = animal.kindName,
                modifier = Modifier.size(90.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AnimalKindTitleRow(
                    kindName = animal.kindName,
                    processState = animal.processState,
                    statusType = animal.statusType,
                    titleStyle = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = animal.shelterName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = animal.viewedAtLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.my_recent_delete_item),
                )
            }
        }
    }
}

@Composable
private fun RecentAnimalCardContent(
    kindName: String,
    processState: String,
    statusType: com.greenbee.findme.presentation.home.model.ProcessStateType,
    shelterName: String,
    imageUrls: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            RecentAnimalThumbnail(
                imageUrls = imageUrls,
                contentDescription = kindName,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )

            AnimalKindTitleRow(
                kindName = kindName,
                processState = processState,
                statusType = statusType,
                titleStyle = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )

            Text(
                text = shelterName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
private fun RecentAnimalThumbnail(
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
