package com.greenbee.findme.presentation.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCollapsingSearchFilterSection(
    scrollBehavior: TopAppBarScrollBehavior,
    searchQuery: String,
    activeRegionFilterCount: Int,
    selectedUpKind: UpKind?,
    selectedState: AbandonmentState?,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRegionFilterClick: () -> Unit,
    onUpKindFilterClick: () -> Unit,
    onStateSelected: (AbandonmentState) -> Unit,
    onHeightChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val heightOffset = scrollBehavior.state.heightOffset.roundToInt()
                    val placeable = measurable.measure(constraints)
                    val visibleHeight = (placeable.height + heightOffset).coerceAtLeast(0)
                    layout(constraints.maxWidth, visibleHeight) {
                        placeable.placeRelative(0, heightOffset)
                    }
                }
                .clipToBounds(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { onHeightChanged(it.height) },
            ) {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text(stringResource(R.string.home_search_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.home_search_clear),
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )

            HomeFilterBar(
                activeRegionFilterCount = activeRegionFilterCount,
                selectedUpKind = selectedUpKind,
                selectedState = selectedState,
                onRegionFilterClick = onRegionFilterClick,
                onUpKindFilterClick = onUpKindFilterClick,
                onStateSelected = onStateSelected,
            )
        }
    }
    }
}
