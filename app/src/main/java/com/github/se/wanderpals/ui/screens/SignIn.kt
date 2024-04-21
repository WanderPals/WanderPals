package com.github.se.wanderpals.ui.screens

// import Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.EMPTY_CODE
import com.github.se.wanderpals.R

/**
 * A composable for the sign in screen.
 *
 * @param onClick1 the action to be executed when the button is clicked.
 * @param onClick2 the action to be executed when the button is clicked.
 */
@Composable
fun SignIn(onClick1: () -> Unit, onClick2: () -> Unit, onClick3: (String, String) -> Unit) {
  var dialogIsOpen by remember { mutableStateOf(false) }
  if (dialogIsOpen) {
    DialogHandler(closeDialogueAction = { dialogIsOpen = false }, processMail = onClick3)
  }
  Column(
      verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.fillMaxSize()
              .background(Brush.linearGradient(listOf(Color.White, Color(0xff96d9d2))))) {
        Image(
            painter = painterResource(id = R.drawable.logo_projet),
            contentDescription = "Logo_project",
            modifier =
                Modifier.padding(top = 100.dp, bottom = 200.dp)
                    .size(150.dp)
                    .clip(RoundedCornerShape(16.dp)))
        GoogleButton(onClick = onClick1)

        EmailButton { dialogIsOpen = true }

        // create a button for sign-in anonymously
        AnonButton(onClick2 = onClick2)
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

@Composable
fun EmailButton(onClick2: () -> Unit) {
  OutlinedButton(onClick = onClick2, modifier = Modifier.testTag("EmailButton")) {
    Image(
        modifier = Modifier.size(20.dp).testTag("GoogleIcon"),
        painter = painterResource(id = R.drawable.logo_email),
        contentDescription = "image_description")
    Text(
        modifier = Modifier.width(125.dp).height(17.dp),
        text = "Sign in with e-mail",
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

@Composable
fun AnonButton(onClick2: () -> Unit) {
  OutlinedButton(onClick = onClick2, modifier = Modifier.testTag("AnonButton")) {
    Image(
        modifier = Modifier.size(20.dp).testTag("AnonIcon"),
        imageVector = Icons.Default.Person,
        contentDescription = "image_description")
    Text(
        modifier = Modifier.width(125.dp).height(17.dp),
        text = "Sign in anonymously",
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

@Composable
fun DialogHandler(closeDialogueAction: () -> Unit, processMail: (String, String) -> Unit) {

  // Mutable state to hold the email input
  var email by remember { mutableStateOf(EMPTY_CODE) }
  var password by remember { mutableStateOf(EMPTY_CODE) }

  // Dialog composable
  Dialog(
      onDismissRequest = {
        closeDialogueAction()
        email = EMPTY_CODE
      }) {
        Surface(
            modifier = Modifier.height(220.dp).testTag("dialog"),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)) {
              Column(
                  modifier = Modifier.padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        onValueChange = { email = it },
                        label = {
                          Text(
                              text = "Insert your email",
                              style =
                                  TextStyle(
                                      fontSize = 18.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        },
                        singleLine = true)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = {
                          Text(
                              text = "Insert your password",
                              style =
                                  TextStyle(
                                      fontSize = 18.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        },
                        singleLine = true)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Button to sign in
                    Button(
                        onClick = {
                          if (email != EMPTY_CODE && password != EMPTY_CODE) {
                            processMail(email, password)
                            closeDialogueAction()
                          }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)) {
                          Text(
                              text = "Sign in",
                              style =
                                  TextStyle(
                                      fontSize = 16.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        }
                  }
            }
      }
}
