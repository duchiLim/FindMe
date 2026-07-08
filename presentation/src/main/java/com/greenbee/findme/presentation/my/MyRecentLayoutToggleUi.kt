package com.greenbee.findme.presentation.my

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.R

internal data class MyRecentLayoutToggleUi(
    val icon: ImageVector,
    val contentDescriptionRes: Int,
)

@Composable
internal fun myRecentLayoutToggleUi(current: HomeLayoutMode): MyRecentLayoutToggleUi =
    when (current) {
        HomeLayoutMode.GRID -> MyRecentLayoutToggleUi(
            icon = Icons.AutoMirrored.Rounded.ViewList,
            contentDescriptionRes = R.string.home_layout_switch_to_list,
        )
        HomeLayoutMode.LIST -> MyRecentLayoutToggleUi(
            icon = Icons.Rounded.Photo,
            contentDescriptionRes = R.string.home_layout_switch_to_cover,
        )
        HomeLayoutMode.COVER -> MyRecentLayoutToggleUi(
            icon = Icons.Rounded.GridView,
            contentDescriptionRes = R.string.home_layout_switch_to_grid,
        )
    }
