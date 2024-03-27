package com.github.se.wanderpals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
  GoogleButton(onClick = onClick)
}

/**
 * A composable that shows a Google sign in button.
 *
 * @param onClick the action to be executed when the button is clicked.
 */
@Composable
fun GoogleButton(onClick: () -> Unit) {
  Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(top = 200.dp)) {
        OutlinedButton(onClick = onClick, modifier = Modifier.padding(10.dp)) {
          Image(
              modifier = Modifier.size(20.dp),
              painter = painterResource(id = R.drawable.logo_google),
              contentDescription = "image description")
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
}
