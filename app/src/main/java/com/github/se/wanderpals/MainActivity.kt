package com.github.se.wanderpals

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : ComponentActivity() {
  private lateinit var signInClient: GoogleSignInClient

  private lateinit var navController: NavHostController

  private val launcher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.result
        Log.d("SignIn", "Login result " + account?.displayName)
        navController.navigate("greeting")
        signInClient.signOut()
      }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

    signInClient = GoogleSignIn.getClient(this, gso)

    setContent {
      WanderPalsTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          navController = rememberNavController()
          NavHost(navController = navController, startDestination = "sign_in") {
            composable("sign_in") {
              Google_button(onClick = { launcher.launch(signInClient.signInIntent) })
            }
            composable("greeting") { Greeting("Android") }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun Google_button(onClick: () -> Unit) {
  Row(
      horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  WanderPalsTheme { Greeting("Android") }
}
