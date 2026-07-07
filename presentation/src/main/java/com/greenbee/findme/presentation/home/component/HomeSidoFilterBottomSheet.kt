package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSidoFilterBottomSheet(
    visible: Boolean,
    sidoList: List<Region>,
    selectedSido: Region?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSidoSelected: (Region?) -> Unit,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Text(
            text = stringResource(R.string.home_sido_filter_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn {
                    item {
                        SidoFilterItem(
                            label = stringResource(R.string.home_filter_nationwide),
                            selected = selectedSido == null,
                            onClick = { onSidoSelected(null) },
                        )
                        HorizontalDivider()
                    }

                    items(
                        items = sidoList,
                        key = { it.code },
                    ) { sido ->
                        SidoFilterItem(
                            label = sido.name,
                            selected = selectedSido?.code == sido.code,
                            onClick = { onSidoSelected(sido) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun SidoFilterItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(label) },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick,
            )
        },
    )
}
