package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.ui.theme.FindMeOnPrimary
import com.greenbee.findme.presentation.ui.theme.FindMePrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeFilterChipsRow(
    selectedSido: Region?,
    selectedUpKind: UpKind?,
    selectedState: AbandonmentState?,
    onSidoFilterClick: () -> Unit,
    onUpKindSelected: (UpKind?) -> Unit,
    onStateSelected: (AbandonmentState) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedSido != null,
            onClick = onSidoFilterClick,
            label = {
                Text(
                    text = selectedSido?.name ?: stringResource(R.string.home_filter_sido),
                )
            },
            colors = filterChipColors(),
        )
        FilterChip(
            selected = selectedUpKind == null,
            onClick = { onUpKindSelected(null) },
            label = { Text(stringResource(R.string.home_filter_all)) },
            colors = filterChipColors(),
        )
        FilterChip(
            selected = selectedUpKind == UpKind.DOG,
            onClick = { onUpKindSelected(UpKind.DOG) },
            label = { Text(stringResource(R.string.home_filter_dog)) },
            colors = filterChipColors(),
        )
        FilterChip(
            selected = selectedUpKind == UpKind.CAT,
            onClick = { onUpKindSelected(UpKind.CAT) },
            label = { Text(stringResource(R.string.home_filter_cat)) },
            colors = filterChipColors(),
        )
        FilterChip(
            selected = selectedState == AbandonmentState.NOTICE,
            onClick = { onStateSelected(AbandonmentState.NOTICE) },
            label = { Text(stringResource(R.string.home_filter_notice)) },
            colors = filterChipColors(),
        )
        FilterChip(
            selected = selectedState == AbandonmentState.PROTECT,
            onClick = { onStateSelected(AbandonmentState.PROTECT) },
            label = { Text(stringResource(R.string.home_filter_protect)) },
            colors = filterChipColors(),
        )
    }
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = FindMePrimary,
    selectedLabelColor = FindMeOnPrimary,
)
