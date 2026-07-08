package com.greenbee.findme.presentation.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.greenbee.findme.presentation.common.showSnackbarWithAutoDismiss
import com.greenbee.findme.presentation.detail.component.AnimalDetailBottomBar
import com.greenbee.findme.presentation.detail.component.AnimalDetailGallery
import com.greenbee.findme.presentation.detail.component.DetailInfoRow
import com.greenbee.findme.presentation.detail.component.DetailSectionCard
import com.greenbee.findme.presentation.detail.model.AnimalDetailUiModel
import com.greenbee.findme.presentation.home.component.AnimalKindTitleRow
import com.greenbee.findme.presentation.ui.theme.FindMePrimaryVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    onBackClick: () -> Unit,
    onShelterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.favoriteMessage) {
        when (uiState.favoriteMessage) {
            FavoriteMessage.ADDED -> {
                snackbarHostState.showSnackbar(context.getString(R.string.favorite_added_message))
                viewModel.onFavoriteMessageShown()
            }
            FavoriteMessage.REMOVED -> {
                val result = snackbarHostState.showSnackbarWithAutoDismiss(
                    message = context.getString(R.string.favorite_removed_message),
                    actionLabel = context.getString(R.string.favorite_undo),
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> viewModel.undoRemoveFavorite()
                    SnackbarResult.Dismissed -> viewModel.onFavoriteMessageShown()
                }
            }
            null -> Unit
        }
    }

    LaunchedEffect(uiState.navigateToShelterCareRegNo) {
        uiState.navigateToShelterCareRegNo?.let { careRegNo ->
            onShelterClick(careRegNo)
            viewModel.onShelterNavigationHandled()
        }
    }

    LaunchedEffect(uiState.showShelterNotFound) {
        if (uiState.showShelterNotFound) {
            Toast.makeText(
                context,
                context.getString(R.string.shelter_lookup_not_found),
                Toast.LENGTH_SHORT,
            ).show()
            viewModel.onShelterNotFoundShown()
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    val animal = uiState.animal
                    if (animal != null) {
                        AnimalKindTitleRow(
                            kindName = animal.kindName,
                            processState = animal.processState,
                            statusType = animal.statusType,
                            titleStyle = MaterialTheme.typography.titleLarge,
                            attachStatusToKindName = true,
                        )
                    } else {
                        Text(stringResource(R.string.home_detail_title))
                    }
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
                    if (uiState.animal != null) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (uiState.isFavorite) {
                                    Icons.Rounded.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = if (uiState.isFavorite) {
                                    stringResource(R.string.favorite_remove)
                                } else {
                                    stringResource(R.string.favorite_add)
                                },
                                tint = if (uiState.isFavorite) FindMePrimaryVariant else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            uiState.animal?.let { animal ->
                AnimalDetailBottomBar(
                    canCallShelter = animal.canCallShelter,
                    onCallClick = {
                        if (animal.shelterTel.isBlank()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.detail_no_phone),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${animal.shelterTel}"))
                            context.startActivity(intent)
                        }
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

            uiState.errorMessage != null && uiState.animal == null -> {
                DetailMessageContent(
                    message = uiState.errorMessage.orEmpty(),
                    actionLabel = stringResource(R.string.home_retry),
                    onAction = viewModel::retry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            uiState.isNotFound -> {
                DetailMessageContent(
                    message = stringResource(R.string.detail_not_found),
                    actionLabel = stringResource(R.string.detail_back),
                    onAction = onBackClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            uiState.animal != null -> {
                AnimalDetailContent(
                    animal = uiState.animal!!,
                    isRefreshing = uiState.isRefreshing,
                    onShelterNameClick = viewModel::onShelterNameClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun AnimalDetailContent(
    animal: AnimalDetailUiModel,
    isRefreshing: Boolean,
    onShelterNameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "gallery") {
                AnimalDetailGallery(
                    imageUrls = animal.imageUrls,
                    contentDescription = animal.kindName,
                )
            }

            item(key = "basic") {
                DetailSectionCard(title = stringResource(R.string.detail_section_basic)) {
                    DetailInfoRow(label = stringResource(R.string.detail_label_kind), value = animal.kindName)
                    DetailInfoRow(label = stringResource(R.string.detail_label_sex), value = animal.sexLabel)
                    DetailInfoRow(label = stringResource(R.string.detail_label_neuter), value = animal.neuterLabel)
                    DetailInfoRow(label = stringResource(R.string.detail_label_age), value = animal.age)
                    DetailInfoRow(label = stringResource(R.string.detail_label_weight), value = animal.weight)
                    DetailInfoRow(label = stringResource(R.string.detail_label_color), value = animal.color)
                }
            }

            item(key = "special") {
                DetailSectionCard(title = stringResource(R.string.detail_section_special)) {
                    Text(
                        text = animal.specialMark.ifBlank {
                            stringResource(R.string.detail_no_special_mark)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (animal.specialMark.isBlank()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }

            item(key = "discovery") {
                DetailSectionCard(title = stringResource(R.string.detail_section_discovery)) {
                    DetailInfoRow(
                        label = stringResource(R.string.detail_label_place),
                        value = animal.happenPlace,
                    )
                }
            }

            item(key = "shelter") {
                DetailSectionCard(title = stringResource(R.string.detail_section_shelter)) {
                    DetailInfoRow(
                        label = stringResource(R.string.detail_label_shelter_name),
                        value = animal.shelterName,
                        onValueClick = if (
                            animal.shelterName.isNotBlank() && animal.shelterName != "보호소 정보 없음"
                        ) {
                            onShelterNameClick
                        } else {
                            null
                        },
                    )
                    DetailInfoRow(
                        label = stringResource(R.string.detail_label_shelter_tel),
                        value = animal.shelterTel.ifBlank { stringResource(R.string.detail_no_phone) },
                    )
                }
            }

            item(key = "desertion_no") {
                Text(
                    text = stringResource(R.string.detail_desertion_no, animal.desertionNo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun DetailMessageContent(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        TextButton(
            onClick = onAction,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(actionLabel)
        }
    }
}
