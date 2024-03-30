package com.github.se.wanderpals.ui.screens

// import Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R

/**
 * A composable for the sign in screen.
 *
 * @param onClick the action to be executed when the button is clicked.
 */
@Composable
fun SignIn(onClick: () -> Unit) {
  Column(
      verticalArrangement = Arrangement.spacedBy(300.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.fillMaxSize()
              .background(Brush.linearGradient(listOf(Color.White, Color(0xff96d9d2))))) {
        Image(
            painter = painterResource(id = R.drawable.logo_projet),
            contentDescription = "Logo_project",
            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(16.dp)))
        GoogleButton(onClick = onClick)
      }
}

/**
 * A composable that shows a Google sign in button.
 *
 * @param onClick the action to be executed when the button is clicked.
 */
@Composable
fun GoogleButton(onClick: () -> Unit) {
  OutlinedButton(onClick = onClick, modifier = Modifier.testTag("LoginButton")) {
    Image(
        modifier = Modifier.size(20.dp).testTag("GoogleIcon"),
        painter = painterResource(id = R.drawable.logo_google),
        contentDescription = "image_description")
    Text(
        modifier = Modifier.width(125.dp).height(17.dp),
        text = "Sign in with Google",
        style =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontFamily = FontFamily(Font(DeviceFontFamilyName("sans-serif-condensed"))),
                fontWeight = FontWeight(500),
                color = Color(0xFF3C4043),
                textAlign = TextAlign.Center,
                letterSpacing = 0.25.sp,
            ))
  }
}
