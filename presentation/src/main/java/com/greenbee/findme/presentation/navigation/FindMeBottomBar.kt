package com.greenbee.findme.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.greenbee.findme.presentation.R

private data class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(
        route = FindMeRoute.HOME,
        labelRes = R.string.nav_home,
        iconRes = R.drawable.ic_home_pet,
    ),
    TopLevelDestination(
        route = FindMeRoute.FAVORITE,
        labelRes = R.string.nav_favorite,
        iconRes = R.drawable.ic_paw,
    ),
    TopLevelDestination(
        route = FindMeRoute.SHELTER,
        labelRes = R.string.nav_shelter,
        iconRes = R.drawable.ic_dog_face,
    ),
    TopLevelDestination(
        route = FindMeRoute.MY,
        labelRes = R.string.nav_my,
        iconRes = R.drawable.ic_cat_face,
    ),
)

@Composable
fun FindMeBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    NavigationBar {
        topLevelDestinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconRes),
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.labelRes)) },
            )
        }
    }
}
