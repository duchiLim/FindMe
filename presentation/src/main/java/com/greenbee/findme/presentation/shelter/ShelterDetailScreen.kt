package com.greenbee.findme.presentation.shelter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.detail.component.DetailInfoRow
import com.greenbee.findme.presentation.detail.component.DetailSectionCard
import com.greenbee.findme.presentation.shelter.component.ShelterMapPlaceholder
import com.greenbee.findme.presentation.shelter.component.ShelterMapPreview
import com.greenbee.findme.presentation.shelter.model.ShelterUiModel
import com.greenbee.findme.presentation.shelter.util.openDirections
import com.greenbee.findme.presentation.shelter.util.openMap
import com.greenbee.findme.presentation.ui.theme.FindMePrimaryVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterDetailScreen(
    onBackClick: () -> Unit,
    onNavigateToAnimals: (careRegNo: String, shelterName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShelterDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val shelter = uiState.shelter
    val mapLocation = uiState.mapLocation ?: shelter?.mapLocation

    LaunchedEffect(uiState.favoriteMessage) {
        when (uiState.favoriteMessage) {
            ShelterFavoriteMessage.ADDED -> {
                snackbarHostState.showSnackbar(context.getString(R.string.shelter_favorite_added))
                viewModel.onFavoriteMessageShown()
            }
            ShelterFavoriteMessage.REMOVED -> {
                snackbarHostState.showSnackbar(context.getString(R.string.shelter_favorite_removed))
                viewModel.onFavoriteMessageShown()
            }
            null -> Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = shelter?.name ?: stringResource(R.string.shelter_detail_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                },
                actions = {
                    if (shelter != null) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (uiState.isFavorite) {
                                    Icons.Rounded.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = if (uiState.isFavorite) {
                                    stringResource(R.string.shelter_favorite_remove)
                                } else {
                                    stringResource(R.string.shelter_favorite_add)
                                },
                                tint = if (uiState.isFavorite) {
                                    FindMePrimaryVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (shelter != null) {
                ShelterDetailBottomBar(
                    shelter = shelter,
                    onCallClick = {
                        if (shelter.tel.isBlank()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.detail_no_phone),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shelter.tel}"))
                            context.startActivity(intent)
                        }
                    },
                    onViewAnimalsClick = {
                        onNavigateToAnimals(shelter.careRegNo, shelter.name)
                    },
                )
            }
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

            uiState.isNotFound -> {
                ShelterDetailMessage(
                    message = stringResource(R.string.shelter_detail_not_found),
                    actionLabel = stringResource(R.string.detail_back),
                    onAction = onBackClick,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            uiState.errorMessage != null -> {
                ShelterDetailMessage(
                    message = uiState.errorMessage.orEmpty(),
                    actionLabel = stringResource(R.string.home_retry),
                    onAction = viewModel::retry,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            shelter != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        when {
                            uiState.isMapLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            mapLocation != null -> {
                                ShelterMapPreview(
                                    location = mapLocation,
                                    onClick = {
                                        openMap(context, shelter.address, mapLocation)
                                    },
                                )
                            }
                            else -> {
                                ShelterMapPlaceholder()
                            }
                        }
                    }

                    item {
                        RowActions(
                            shelter = shelter,
                            mapLocation = mapLocation,
                        )
                    }

                    item {
                        DetailSectionCard(title = stringResource(R.string.detail_section_shelter)) {
                            if (shelter.orgName.isNotBlank()) {
                                DetailInfoRow(
                                    label = stringResource(R.string.shelter_detail_label_org),
                                    value = shelter.orgName,
                                )
                            }
                            DetailInfoRow(
                                label = stringResource(R.string.shelter_detail_label_address),
                                value = shelter.address,
                                modifier = Modifier.clickable {
                                    copyAddressToClipboard(context, shelter.address)
                                },
                            )
                            DetailInfoRow(
                                label = stringResource(R.string.detail_label_shelter_tel),
                                value = shelter.tel.ifBlank {
                                    stringResource(R.string.detail_no_phone)
                                },
                            )
                            DetailInfoRow(
                                label = stringResource(R.string.shelter_detail_label_reg_no),
                                value = shelter.careRegNo,
                            )
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.shelter_visit_notice),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowActions(
    shelter: ShelterUiModel,
    mapLocation: com.greenbee.findme.domain.model.GeoLocation?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = { openMap(context, shelter.address, mapLocation) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.shelter_open_map))
        }
        OutlinedButton(
            onClick = { openDirections(context, shelter.address, mapLocation) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.shelter_open_directions))
        }
    }
}

@Composable
private fun ShelterDetailBottomBar(
    shelter: ShelterUiModel,
    onCallClick: () -> Unit,
    onViewAnimalsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onCallClick,
            enabled = shelter.canCall,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.detail_call_shelter))
        }
        OutlinedButton(
            onClick = onViewAnimalsClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.shelter_view_animals))
        }
    }
}

@Composable
private fun ShelterDetailMessage(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onAction,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(actionLabel)
        }
    }
}

private fun copyAddressToClipboard(context: Context, address: String) {
    if (address.isBlank() || address == "주소 정보 없음") {
        return
    }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("address", address))
    Toast.makeText(context, context.getString(R.string.shelter_address_copied), Toast.LENGTH_SHORT).show()
}
