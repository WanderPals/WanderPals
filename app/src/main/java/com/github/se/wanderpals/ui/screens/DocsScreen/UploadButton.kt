package com.github.se.wanderpals.ui.screens.DocsScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.github.se.wanderpals.R
import kotlinx.coroutines.delay

@Composable
fun UploadScreen() {
  var isUploading by remember { mutableStateOf(false) }
  var isUploaded by remember { mutableStateOf(false) }
  var isUploadedAfter by remember { mutableStateOf(false) }

  var launch by remember { mutableStateOf(false) }

  LaunchedEffect(key1 = launch) {
    delay(3000)
    isUploaded = true
    delay(2000)
    isUploading = false
    isUploadedAfter = true
    delay(1000)
    isUploaded = false
    isUploadedAfter = false
  }

  Box(
      modifier = Modifier.fillMaxSize().background(Color(0xFFeceffc)).size(50.dp),
      contentAlignment = Alignment.Center) {}
}

@Composable
fun UploadComponent(
    isUploading: Boolean,
    isUploaded: Boolean,
    isUploadedAfter: Boolean,
    onUploadClick: () -> Unit
) {
  val btnColor = Color(0xFF3bafda)
  val progressColor = Color(0xFF2d334c)

  Box(
      modifier =
          Modifier.background(Color.White, RoundedCornerShape(10.dp))
              .padding(16.dp)
              .clip(RoundedCornerShape(10.dp))
              .shadow(8.dp, RoundedCornerShape(10.dp)),
      contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.file_uploadupload),
                        contentDescription = "Clip",
                        modifier = Modifier.size(20.dp))
                    Text(text = "Document.pdf", modifier = Modifier.padding(start = 8.dp))
                  }

              AnimatedVisibility(
                  visible = isUploading,
                  enter =
                      fadeIn(animationSpec = tween(durationMillis = 400)) +
                          slideInVertically(animationSpec = tween(durationMillis = 400)) { -it },
                  exit =
                      fadeOut(animationSpec = tween(durationMillis = 400)) +
                          slideOutVertically(animationSpec = tween(durationMillis = 400)) { it }) {
                    Text(
                        text = "Uploading...",
                        modifier = Modifier.padding(top = 16.dp),
                        color = Color.White)
                  }

              AnimatedVisibility(
                  visible = !isUploading,
                  enter =
                      fadeIn(animationSpec = tween(durationMillis = 400)) +
                          slideInVertically(animationSpec = tween(durationMillis = 400)) { it },
                  exit =
                      fadeOut(animationSpec = tween(durationMillis = 400)) +
                          slideOutVertically(animationSpec = tween(durationMillis = 400)) { -it }) {
                    Button(
                        onClick = onUploadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = btnColor)) {
                          Text("Upload")
                        }
                  }

              AnimatedVisibility(
                  visible = isUploaded,
                  enter =
                      fadeIn(animationSpec = tween(durationMillis = 400)) +
                          slideInVertically(animationSpec = tween(durationMillis = 400)) { it },
                  exit =
                      fadeOut(animationSpec = tween(durationMillis = 400)) +
                          slideOutVertically(animationSpec = tween(durationMillis = 400)) { -it }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(progressColor).padding(8.dp)) {
                          Icon(
                              painter = painterResource(id = R.drawable.check),
                              contentDescription = "Check",
                              tint = Color.White,
                              modifier = Modifier.size(16.dp))
                          Text("Completed", color = Color.White)
                        }
                  }
            }
      }
}
