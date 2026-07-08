package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.R
import com.greenbee.findme.presentation.home.model.HomeDateRange
import com.greenbee.findme.presentation.ui.theme.FindMeOnPrimary
import com.greenbee.findme.presentation.ui.theme.FindMePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdvancedFilterBottomSheet(
    visible: Boolean,
    sidoList: List<Region>,
    sigunguList: List<Region>,
    draftSido: Region?,
    draftSigungu: Region?,
    draftUpKind: UpKind?,
    draftState: AbandonmentState?,
    draftDateRange: HomeDateRange,
    isSidoListLoading: Boolean,
    isSigunguListLoading: Boolean,
    onDismiss: () -> Unit,
    onDraftSidoSelected: (Region?) -> Unit,
    onDraftSigunguSelected: (Region?) -> Unit,
    onDraftUpKindSelected: (UpKind?) -> Unit,
    onDraftStateSelected: (AbandonmentState?) -> Unit,
    onDraftDateRangeSelected: (HomeDateRange) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = stringResource(R.string.home_filter_advanced_sheet_title),
                style = MaterialTheme.typography.titleLarge,
            )

            FilterSection(title = stringResource(R.string.home_filter_region)) {
                if (isSidoListLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    HomeRegionDropdownField(
                        label = stringResource(R.string.home_filter_sido),
                        selectedLabel = draftSido?.name ?: stringResource(R.string.home_filter_nationwide),
                        options = buildList {
                            add(stringResource(R.string.home_filter_nationwide) to null)
                            addAll(sidoList.map { it.name to it })
                        },
                        onOptionSelected = onDraftSidoSelected,
                    )
                }

                HomeRegionDropdownField(
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
            }

            FilterSection(title = stringResource(R.string.home_filter_upkind_sheet_title)) {
                UpKindFilterChips(
                    draftUpKind = draftUpKind,
                    onDraftUpKindSelected = onDraftUpKindSelected,
                )
            }

            FilterSection(title = stringResource(R.string.home_filter_state)) {
                StateFilterChips(
                    draftState = draftState,
                    onDraftStateSelected = onDraftStateSelected,
                )
            }

            FilterSection(title = stringResource(R.string.home_filter_period)) {
                DateRangeFilterChips(
                    draftDateRange = draftDateRange,
                    onDraftDateRangeSelected = onDraftDateRangeSelected,
                )
            }

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

@Composable
private fun FilterSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UpKindFilterChips(
    draftUpKind: UpKind?,
    onDraftUpKindSelected: (UpKind?) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterOptionChip(
            selected = draftUpKind == null,
            label = stringResource(R.string.home_filter_all_kind),
            onClick = { onDraftUpKindSelected(null) },
        )
        FilterOptionChip(
            selected = draftUpKind == UpKind.DOG,
            label = stringResource(R.string.home_filter_dog),
            onClick = { onDraftUpKindSelected(UpKind.DOG) },
        )
        FilterOptionChip(
            selected = draftUpKind == UpKind.CAT,
            label = stringResource(R.string.home_filter_cat),
            onClick = { onDraftUpKindSelected(UpKind.CAT) },
        )
        FilterOptionChip(
            selected = draftUpKind == UpKind.ETC,
            label = stringResource(R.string.home_filter_etc),
            onClick = { onDraftUpKindSelected(UpKind.ETC) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StateFilterChips(
    draftState: AbandonmentState?,
    onDraftStateSelected: (AbandonmentState?) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterOptionChip(
            selected = draftState == null,
            label = stringResource(R.string.home_filter_all),
            onClick = { onDraftStateSelected(null) },
        )
        FilterOptionChip(
            selected = draftState == AbandonmentState.NOTICE,
            label = stringResource(R.string.home_filter_notice),
            onClick = { onDraftStateSelected(AbandonmentState.NOTICE) },
        )
        FilterOptionChip(
            selected = draftState == AbandonmentState.PROTECT,
            label = stringResource(R.string.home_filter_protect),
            onClick = { onDraftStateSelected(AbandonmentState.PROTECT) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DateRangeFilterChips(
    draftDateRange: HomeDateRange,
    onDraftDateRangeSelected: (HomeDateRange) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterOptionChip(
            selected = draftDateRange == HomeDateRange.DAYS_7,
            label = stringResource(R.string.home_filter_period_7),
            onClick = { onDraftDateRangeSelected(HomeDateRange.DAYS_7) },
        )
        FilterOptionChip(
            selected = draftDateRange == HomeDateRange.DAYS_30,
            label = stringResource(R.string.home_filter_period_30),
            onClick = { onDraftDateRangeSelected(HomeDateRange.DAYS_30) },
        )
        FilterOptionChip(
            selected = draftDateRange == HomeDateRange.DAYS_90,
            label = stringResource(R.string.home_filter_period_90),
            onClick = { onDraftDateRangeSelected(HomeDateRange.DAYS_90) },
        )
    }
}

@Composable
private fun FilterOptionChip(
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
