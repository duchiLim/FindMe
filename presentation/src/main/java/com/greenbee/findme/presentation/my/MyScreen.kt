package com.greenbee.findme.presentation.my

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.my.component.RecentAnimalCard

private const val DATA_GO_KR_URL = "https://www.data.go.kr/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(
    onAnimalClick: (String) -> Unit,
    onNavigateToRecent: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_my)) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.my_recent_section_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (uiState.isRecentViewingEnabled && uiState.recentAnimals.isNotEmpty()) {
                        TextButton(onClick = onNavigateToRecent) {
                            Text(stringResource(R.string.my_recent_see_all))
                        }
                    }
                }
            }

            item {
                when {
                    !uiState.isRecentViewingEnabled -> {
                        Text(
                            text = stringResource(R.string.my_recent_disabled),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    uiState.recentAnimals.isEmpty() -> {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = stringResource(R.string.my_recent_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = stringResource(R.string.my_recent_empty_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }

                    else -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(
                                items = uiState.recentAnimals,
                                key = { it.desertionNo },
                            ) { animal ->
                                RecentAnimalCard(
                                    animal = animal,
                                    onClick = { onAnimalClick(animal.desertionNo) },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.my_settings_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 8.dp,
                    ),
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.my_app_info_title)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable(onClick = onNavigateToAbout),
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    headlineContent = { Text(stringResource(R.string.my_data_source_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.my_data_source_summary))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Public,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DATA_GO_KR_URL))
                        context.startActivity(intent)
                    },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    headlineContent = { Text(stringResource(R.string.my_applications_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.my_applications_menu_summary))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Assignment,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable(onClick = onNavigateToApplications),
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    headlineContent = { Text(stringResource(R.string.my_favorite_shortcut_title)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable(onClick = onNavigateToFavorite),
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    headlineContent = { Text(stringResource(R.string.my_notification_settings_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.my_notification_settings_summary))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = uiState.isNotificationEnabled,
                            onCheckedChange = viewModel::onNotificationEnabledChange,
                        )
                    },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    headlineContent = { Text(stringResource(R.string.my_recent_viewing_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.my_recent_viewing_summary))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = uiState.isRecentViewingEnabled,
                            onCheckedChange = viewModel::onRecentViewingEnabledChange,
                        )
                    },
                )
            }

            item {
                Text(
                    text = stringResource(
                        R.string.my_footer_version,
                        uiState.appVersionName,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                )
            }
        }
    }
}
