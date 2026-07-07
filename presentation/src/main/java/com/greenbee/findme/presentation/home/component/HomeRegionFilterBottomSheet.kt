package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRegionFilterBottomSheet(
    visible: Boolean,
    sidoList: List<Region>,
    sigunguList: List<Region>,
    draftSido: Region?,
    draftSigungu: Region?,
    isSidoListLoading: Boolean,
    isSigunguListLoading: Boolean,
    onDismiss: () -> Unit,
    onDraftSidoSelected: (Region?) -> Unit,
    onDraftSigunguSelected: (Region?) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.home_filter_region_sheet_title),
                style = MaterialTheme.typography.titleLarge,
            )

            if (isSidoListLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                RegionDropdownField(
                    label = stringResource(R.string.home_filter_sido),
                    selectedLabel = draftSido?.name ?: stringResource(R.string.home_filter_nationwide),
                    options = buildList {
                        add(stringResource(R.string.home_filter_nationwide) to null)
                        addAll(sidoList.map { it.name to it })
                    },
                    onOptionSelected = onDraftSidoSelected,
                )
            }

            RegionDropdownField(
                label = stringResource(R.string.home_filter_sigungu),
                selectedLabel = when {
                    draftSido == null -> stringResource(R.string.home_filter_select_sido_first)
                    draftSigungu == null -> stringResource(R.string.home_filter_sigungu_all)
                    else -> draftSigungu.name
                },
                enabled = draftSido != null && !isSigunguListLoading,
                isLoading = isSigunguListLoading,
                options = if (draftSido == null) {
                    emptyList()
                } else {
                    buildList {
                        add(stringResource(R.string.home_filter_sigungu_all) to null)
                        addAll(sigunguList.map { it.name to it })
                    }
                },
                onOptionSelected = onDraftSigunguSelected,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.home_filter_reset))
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.home_filter_apply))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionDropdownField(
    label: String,
    selectedLabel: String,
    options: List<Pair<String, Region?>>,
    onOptionSelected: (Region?) -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled && !isLoading,
        onExpandedChange = { if (enabled && !isLoading) expanded = it },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled && !isLoading,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (name, region) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onOptionSelected(region)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
