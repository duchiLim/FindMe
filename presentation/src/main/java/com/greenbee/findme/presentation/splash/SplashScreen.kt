package com.greenbee.findme.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.greenbee.findme.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val GrowDurationMs = 650
private const val FadeInDurationMs = 350
private const val HoldDurationMs = 500

@Composable
fun AnimatedSplashScreen(
    onReady: () -> Unit,
    onAnimationFinished: () -> Unit,
) {
    val splashColor = colorResource(R.color.primary)
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        onReady()

        val scaleSpec = tween<Float>(
            durationMillis = GrowDurationMs,
            easing = LinearOutSlowInEasing,
        )
        val alphaSpec = tween<Float>(
            durationMillis = FadeInDurationMs,
            easing = FastOutLinearInEasing,
        )
        launch { alpha.animateTo(1f, alphaSpec) }
        scale.animateTo(1f, scaleSpec)

        delay(HoldDurationMs.milliseconds)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(splashColor),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                },
        )
    }
}
