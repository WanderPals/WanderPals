package com.github.se.wanderpals

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.github.se.wanderpals.ui.screens.SignIn
import com.github.se.wanderpals.ui.screens.overview.Overview
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
  private lateinit var signInClient: GoogleSignInClient

  private lateinit var account: GoogleSignInAccount

  private lateinit var navController: NavHostController

  private lateinit var navigationActions: NavigationActions

  private lateinit var tripsRepository: TripsRepository

  private val launcher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        account = task.result
        val uid = account.id ?: ""
        tripsRepository = TripsRepository(uid, Dispatchers.IO)
        tripsRepository.initFirestore()
        Log.d("SignIn", "Login result " + account.displayName)
        navigationActions.navigateTo(Route.OVERVIEW)
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
          navigationActions = NavigationActions(navController)

          NavHost(navController = navController, startDestination = Route.SIGN_IN) {
            composable(Route.SIGN_IN) {
              SignIn(onClick = { launcher.launch(signInClient.signInIntent) })
            }
            composable(Route.OVERVIEW) {
              Overview(
                  overviewViewModel = OverviewViewModel(tripsRepository),
                  navigationActions = navigationActions)
            }
            composable(Route.TRIP + "/{tripId}") { navBackStackEntry ->
              val tripId = navBackStackEntry.arguments?.getString("tripId") ?: ""
              Trip(navigationActions, tripId)
            }
            composable(Route.CREATE_TRIP) {
              CreateTrip(OverviewViewModel(tripsRepository), navigationActions)
            }
          }
        }
      }
    }
  }
}
