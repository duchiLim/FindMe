package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.ui.theme.FindMeOnPrimary
import com.greenbee.findme.presentation.ui.theme.FindMePrimary

@Composable
fun HomeFilterBar(
    activeRegionFilterCount: Int,
    selectedUpKind: UpKind?,
    selectedState: AbandonmentState?,
    onRegionFilterClick: () -> Unit,
    onUpKindFilterClick: () -> Unit,
    onStateSelected: (AbandonmentState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RegionFilterChip(
                activeRegionFilterCount = activeRegionFilterCount,
                onClick = onRegionFilterClick,
            )
            UpKindFilterChip(
                selectedUpKind = selectedUpKind,
                onClick = onUpKindFilterClick,
            )
            QuickFilterChip(
                selected = selectedState == AbandonmentState.NOTICE,
                label = stringResource(R.string.home_filter_notice),
                onClick = { onStateSelected(AbandonmentState.NOTICE) },
            )
            QuickFilterChip(
                selected = selectedState == AbandonmentState.PROTECT,
                label = stringResource(R.string.home_filter_protect),
                onClick = { onStateSelected(AbandonmentState.PROTECT) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionFilterChip(
    activeRegionFilterCount: Int,
    onClick: () -> Unit,
) {
    BadgedBox(
        badge = {
            if (activeRegionFilterCount > 0) {
                Badge { Text(activeRegionFilterCount.toString()) }
            }
        },
    ) {
        FilterChip(
            selected = activeRegionFilterCount > 0,
            onClick = onClick,
            label = { Text(stringResource(R.string.home_filter_region)) },
            colors = filterChipColors(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpKindFilterChip(
    selectedUpKind: UpKind?,
    onClick: () -> Unit,
) {
    val label = when (selectedUpKind) {
        UpKind.DOG -> stringResource(R.string.home_filter_dog)
        UpKind.CAT -> stringResource(R.string.home_filter_cat)
        UpKind.ETC -> stringResource(R.string.home_filter_etc)
        null -> stringResource(R.string.home_filter_all_kind)
    }

    FilterChip(
        selected = selectedUpKind != null,
        onClick = onClick,
        label = { Text(label) },
        colors = filterChipColors(),
    )
}

@Composable
private fun QuickFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = filterChipColors(),
    )
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = FindMePrimary,
    selectedLabelColor = FindMeOnPrimary,
)
