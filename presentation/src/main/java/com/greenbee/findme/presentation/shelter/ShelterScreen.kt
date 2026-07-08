package com.greenbee.findme.presentation.shelter

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.greenbee.findme.presentation.shelter.component.ShelterCard
import com.greenbee.findme.presentation.shelter.component.ShelterRegionFilterBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterScreen(
    modifier: Modifier = Modifier,
    viewModel: ShelterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_shelter)) },
                actions = {
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
            if (uiState.regionSummary.isNotBlank()) {
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

            ShelterContent(
                uiState = uiState,
                onSelectRegion = viewModel::onRegionFilterClick,
                onRetry = viewModel::retry,
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

@Composable
private fun ShelterContent(
    uiState: ShelterUiState,
    onSelectRegion: () -> Unit,
    onRetry: () -> Unit,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        !uiState.hasAppliedRegion -> {
            ShelterMessageContent(
                message = stringResource(R.string.shelter_empty_region),
                description = stringResource(R.string.shelter_empty_region_description),
                actionLabel = stringResource(R.string.shelter_select_region),
                onAction = onSelectRegion,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.isShelterListLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null -> {
            ShelterMessageContent(
                message = uiState.errorMessage.orEmpty(),
                actionLabel = stringResource(R.string.home_retry),
                onAction = onRetry,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.isEmpty -> {
            ShelterMessageContent(
                message = stringResource(R.string.shelter_empty_result),
                actionLabel = stringResource(R.string.shelter_select_region),
                onAction = onSelectRegion,
                modifier = modifier.fillMaxSize(),
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = stringResource(R.string.shelter_total_count, uiState.totalCount),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }

                items(
                    items = uiState.shelters,
                    key = { it.careRegNo },
                ) { shelter ->
                    ShelterCard(
                        shelter = shelter,
                        onCallClick = { onCallClick(shelter.tel) },
                    )
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
        Button(
            onClick = onAction,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(actionLabel)
        }
    }
}
