package com.github.se.wanderpals.ui.screens.DocsScreen

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect for loading items.
 *
 * @param isLoading Whether the item is loading.
 * @param content The content to display.
 * @param modifier The modifier for the item.
 */
@Composable
fun ShimmerItem(
    isLoading: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
  if (isLoading) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
      Box(modifier = Modifier.fillMaxWidth().height(20.dp).shimmerEffect())
    }
  } else {
    content()
  }
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.shimmerEffect(): Modifier = composed {
  var size by remember { mutableStateOf(IntSize.Zero) }
  val transition = rememberInfiniteTransition(label = "")
  val offset by
      transition.animateFloat(
          initialValue = -2 * size.width.toFloat(),
          targetValue = 2 * size.width.toFloat(),
          animationSpec =
              infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
          label = "")

  background(
          brush =
              Brush.linearGradient(
                  colors = listOf(Color(0xFFB8B5B5), Color(0xFF8F8B8B), Color(0xFFB8B5B5)),
                  start = Offset(offset, 0f),
                  end = Offset(offset + size.width, size.height.toFloat())))
      .onGloballyPositioned { size = it.size }
}
