package com.github.se.wanderpals.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    inputLazyColumn: @Composable () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
  var isRefreshing by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val pullToRefreshState = rememberPullToRefreshState()
  Box(modifier = modifier.fillMaxSize().nestedScroll(pullToRefreshState.nestedScrollConnection)) {
    inputLazyColumn()

    if (pullToRefreshState.isRefreshing) {
      LaunchedEffect(Unit) {
        scope.launch {
          isRefreshing = true
          onRefresh()
          delay(1000)
          isRefreshing = false
        }
      }
    }

    LaunchedEffect(isRefreshing) {
      if (isRefreshing) {
        pullToRefreshState.startRefresh()
      } else {
        pullToRefreshState.endRefresh()
      }
    }

    PullToRefreshContainer(
        state = pullToRefreshState,
        modifier = Modifier.align(Alignment.TopCenter).alpha(if (isRefreshing) 1f else 0f))
  }
}
