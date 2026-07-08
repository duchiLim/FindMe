package com.greenbee.findme.presentation.my

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAppInfoDialog by remember { mutableStateOf(false) }

    if (showAppInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAppInfoDialog = false },
            title = { Text(stringResource(R.string.my_app_info_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(
                            R.string.my_app_info_version,
                            uiState.appVersionName,
                            uiState.appVersionCode,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = stringResource(R.string.my_app_info_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppInfoDialog = false }) {
                    Text(stringResource(R.string.my_dialog_close))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_my)) },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.my_recent_section_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 12.dp,
                            ),
                        )
                    }

                    if (uiState.recentAnimals.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.my_recent_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                            Text(
                                text = stringResource(R.string.my_recent_empty_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                        }
                    } else {
                        item {
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
                            modifier = Modifier.clickable { showAppInfoDialog = true },
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
    }
}
