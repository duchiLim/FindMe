package com.greenbee.findme.presentation.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.my.component.RecentAnimalListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecentScreen(
    onBackClick: () -> Unit,
    onAnimalClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyRecentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.recentAnimals.size) {
        if (uiState.recentAnimals.isNotEmpty()) {
            viewModel.refreshSnapshots()
        }
    }

    if (uiState.showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onClearAllDismiss,
            title = { Text(stringResource(R.string.my_recent_clear_confirm_title)) },
            text = { Text(stringResource(R.string.my_recent_clear_confirm_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::onClearAllConfirm) {
                    Text(stringResource(R.string.my_recent_clear_all))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onClearAllDismiss) {
                    Text(stringResource(R.string.my_dialog_close))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_recent_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                },
                actions = {
                    if (uiState.recentAnimals.isNotEmpty()) {
                        TextButton(onClick = viewModel::onClearAllClick) {
                            Text(stringResource(R.string.my_recent_clear_all))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (uiState.isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.recentAnimals.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.my_recent_empty),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(R.string.my_recent_empty_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.recentAnimals,
                            key = { it.desertionNo },
                        ) { animal ->
                            RecentAnimalListItem(
                                animal = animal,
                                onClick = { onAnimalClick(animal.desertionNo) },
                                onDeleteClick = { viewModel.onRemoveItem(animal.desertionNo) },
                            )
                        }
                    }
                }
            }
        }
    }
}
