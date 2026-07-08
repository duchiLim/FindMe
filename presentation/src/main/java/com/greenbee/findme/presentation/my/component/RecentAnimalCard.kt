package com.greenbee.findme.presentation.my.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.home.component.AnimalKindTitleRow
import com.greenbee.findme.presentation.ui.theme.FindMeBackground

@Composable
fun RecentAnimalCard(
    animal: AbandonedAnimalUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            val primaryImageUrl = animal.imageUrls.firstOrNull()
            if (primaryImageUrl != null) {
                AsyncImage(
                    model = primaryImageUrl,
                    contentDescription = animal.kindName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_paw),
                    error = painterResource(R.drawable.ic_paw),
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_paw),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(FindMeBackground)
                        .padding(24.dp),
                )
            }

            AnimalKindTitleRow(
                kindName = animal.kindName,
                processState = animal.processState,
                statusType = animal.statusType,
                titleStyle = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp),
            )

            Text(
                text = animal.shelterName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}
