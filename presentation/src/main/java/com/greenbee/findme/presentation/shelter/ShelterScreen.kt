package com.greenbee.findme.presentation.shelter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.shelter.component.ShelterCard
import com.greenbee.findme.presentation.shelter.component.ShelterRegionFilterBottomSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShelterScreen(
    onShelterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShelterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    LaunchedEffect(uiState.locationMessage) {
        uiState.locationMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onLocationMessageShown()
        }
    }

    fun requestNearbyShelters() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    val showSearch = uiState.hasAppliedRegion || uiState.listMode == ShelterListMode.FAVORITES

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = { Text(stringResource(R.string.nav_shelter)) },
                actions = {
                    IconButton(onClick = { requestNearbyShelters() }) {
                        Icon(
                            imageVector = Icons.Rounded.MyLocation,
                            contentDescription = stringResource(R.string.shelter_nearby_action),
                        )
                    }
                    IconButton(onClick = viewModel::onRegionFilterClick) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = stringResource(R.string.shelter_select_region),
                        )
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
            ShelterFilterChips(
                listMode = uiState.listMode,
                sortMode = uiState.sortMode,
                canSortByDistance = uiState.userLocation != null,
                onListModeSelected = viewModel::onListModeSelected,
                onSortModeSelected = viewModel::onSortModeSelected,
                onRequestLocation = { requestNearbyShelters() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
            if (showSearch) {
                SearchBarDefaults.InputField(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text(stringResource(R.string.shelter_search_hint)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = viewModel::clearSearch) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.shelter_search_clear),
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            if (uiState.regionSummary.isNotBlank() && uiState.listMode == ShelterListMode.ALL) {
                Text(
                    text = uiState.regionSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = viewModel::onRegionFilterClick)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            if (uiState.isLocationLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                ShelterContent(
                    uiState = uiState,
                    onSelectRegion = viewModel::onRegionFilterClick,
                    onRetry = viewModel::retry,
                    onClearSearch = viewModel::clearSearch,
                    onShelterClick = onShelterClick,
                    onFavoriteClick = viewModel::toggleFavorite,
                    onShowAllShelters = { viewModel.onListModeSelected(ShelterListMode.ALL) },
                    onCallClick = { tel ->
                        if (tel.isBlank()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.detail_no_phone),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
                            context.startActivity(intent)
                        }
                    },
                )
            }
        }
    }

    ShelterRegionFilterBottomSheet(
        visible = uiState.isRegionFilterSheetVisible,
        sidoList = uiState.sidoList,
        sigunguList = uiState.sigunguList,
        draftSido = uiState.draftSido,
        draftSigungu = uiState.draftSigungu,
        isSidoListLoading = uiState.isSidoListLoading,
        isSigunguListLoading = uiState.isSigunguListLoading,
        onDismiss = viewModel::onRegionFilterDismiss,
        onDraftSidoSelected = viewModel::onDraftSidoSelected,
        onDraftSigunguSelected = viewModel::onDraftSigunguSelected,
        onReset = viewModel::onResetRegionDraft,
        onApply = viewModel::onApplyRegionFilter,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShelterFilterChips(
    listMode: ShelterListMode,
    sortMode: ShelterSortMode,
    canSortByDistance: Boolean,
    onListModeSelected: (ShelterListMode) -> Unit,
    onSortModeSelected: (ShelterSortMode) -> Unit,
    onRequestLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = listMode == ShelterListMode.ALL,
            onClick = { onListModeSelected(ShelterListMode.ALL) },
            label = { Text(stringResource(R.string.shelter_filter_all)) },
        )
        FilterChip(
            selected = listMode == ShelterListMode.FAVORITES,
            onClick = { onListModeSelected(ShelterListMode.FAVORITES) },
            label = { Text(stringResource(R.string.shelter_filter_favorites)) },
        )
        FilterChip(
            selected = sortMode == ShelterSortMode.DISTANCE,
            onClick = {
                if (!canSortByDistance) {
                    onRequestLocation()
                } else {
                    onSortModeSelected(
                        if (sortMode == ShelterSortMode.DISTANCE) {
                            ShelterSortMode.DEFAULT
                        } else {
                            ShelterSortMode.DISTANCE
                        },
                    )
                }
            },
            label = { Text(stringResource(R.string.shelter_sort_distance)) },
        )
    }
}

@Composable
private fun ShelterContent(
    uiState: ShelterUiState,
    onSelectRegion: () -> Unit,
    onRetry: () -> Unit,
    onClearSearch: () -> Unit,
    onShelterClick: (String) -> Unit,
    onFavoriteClick: (com.greenbee.findme.presentation.shelter.model.ShelterUiModel) -> Unit,
    onShowAllShelters: () -> Unit,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.listMode == ShelterListMode.FAVORITES && uiState.isFavoritesEmpty -> {
            ShelterMessageContent(
                message = stringResource(R.string.shelter_favorites_empty),
                description = stringResource(R.string.shelter_favorites_empty_description),
                actionLabel = stringResource(R.string.shelter_filter_all),
                onAction = onShowAllShelters,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.listMode == ShelterListMode.ALL && !uiState.hasAppliedRegion -> {
            ShelterMessageContent(
                message = stringResource(R.string.shelter_empty_region),
                description = stringResource(R.string.shelter_empty_region_description),
                actionLabel = stringResource(R.string.shelter_select_region),
                onAction = onSelectRegion,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.listMode == ShelterListMode.ALL && uiState.isShelterListLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.listMode == ShelterListMode.ALL && uiState.errorMessage != null -> {
            ShelterMessageContent(
                message = uiState.errorMessage.orEmpty(),
                actionLabel = stringResource(R.string.home_retry),
                onAction = onRetry,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.listMode == ShelterListMode.ALL && uiState.isEmpty -> {
            ShelterMessageContent(
                message = stringResource(R.string.shelter_empty_result),
                actionLabel = stringResource(R.string.shelter_select_region),
                onAction = onSelectRegion,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.isSearchEmpty -> {
            ShelterMessageContent(
                message = stringResource(R.string.shelter_search_empty),
                actionLabel = stringResource(R.string.shelter_search_clear),
                onAction = onClearSearch,
                modifier = modifier.fillMaxSize(),
            )
        }

        else -> {
            Column(modifier = modifier.fillMaxSize()) {
                if (uiState.isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        val displayCount = uiState.displayShelters.size
                        val countText = if (uiState.listMode == ShelterListMode.FAVORITES) {
                            stringResource(R.string.shelter_favorites_count, displayCount)
                        } else {
                            stringResource(R.string.shelter_total_count, displayCount)
                        }
                        Text(
                            text = countText,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    items(
                        items = uiState.displayShelters,
                        key = { it.careRegNo },
                    ) { shelter ->
                        ShelterCard(
                            shelter = shelter,
                            onClick = { onShelterClick(shelter.careRegNo) },
                            onCallClick = { onCallClick(shelter.tel) },
                            onFavoriteClick = { onFavoriteClick(shelter) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelterMessageContent(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
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
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        androidx.compose.material3.Button(
            onClick = onAction,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(actionLabel)
        }
    }
}
