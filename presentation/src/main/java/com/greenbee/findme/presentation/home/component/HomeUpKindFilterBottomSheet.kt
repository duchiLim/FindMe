package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.ui.theme.FindMeOnPrimary
import com.greenbee.findme.presentation.ui.theme.FindMePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUpKindFilterBottomSheet(
    visible: Boolean,
    draftUpKind: UpKind?,
    onDismiss: () -> Unit,
    onDraftUpKindSelected: (UpKind?) -> Unit,
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
                text = stringResource(R.string.home_filter_upkind_sheet_title),
                style = MaterialTheme.typography.titleLarge,
            )

            UpKindFilterChips(
                draftUpKind = draftUpKind,
                onDraftUpKindSelected = onDraftUpKindSelected,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UpKindFilterChips(
    draftUpKind: UpKind?,
    onDraftUpKindSelected: (UpKind?) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        UpKindChip(
            selected = draftUpKind == null,
            label = stringResource(R.string.home_filter_all_kind),
            onClick = { onDraftUpKindSelected(null) },
        )
        UpKindChip(
            selected = draftUpKind == UpKind.DOG,
            label = stringResource(R.string.home_filter_dog),
            onClick = { onDraftUpKindSelected(UpKind.DOG) },
        )
        UpKindChip(
            selected = draftUpKind == UpKind.CAT,
            label = stringResource(R.string.home_filter_cat),
            onClick = { onDraftUpKindSelected(UpKind.CAT) },
        )
        UpKindChip(
            selected = draftUpKind == UpKind.ETC,
            label = stringResource(R.string.home_filter_etc),
            onClick = { onDraftUpKindSelected(UpKind.ETC) },
        )
    }
}

@Composable
private fun UpKindChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FindMePrimary,
            selectedLabelColor = FindMeOnPrimary,
        ),
    )
}
