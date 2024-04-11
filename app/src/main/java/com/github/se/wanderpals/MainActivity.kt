package com.github.se.wanderpals

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import com.github.se.wanderpals.BuildConfig.MAPS_API_KEY
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.github.se.wanderpals.ui.screens.SignIn
import com.github.se.wanderpals.ui.screens.overview.Overview
import com.github.se.wanderpals.ui.screens.trip.CreateSuggestion
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
  private lateinit var signInClient: GoogleSignInClient

  private lateinit var navController: NavHostController

  private lateinit var navigationActions: NavigationActions

  private lateinit var tripsRepository: TripsRepository

  private val launcher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        task.addOnSuccessListener { account ->
          val googleTokenId = account.idToken ?: ""
          val credential = GoogleAuthProvider.getCredential(googleTokenId, null)
          FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

              val uid = it.result?.user?.uid ?: ""
              tripsRepository = TripsRepository(uid, Dispatchers.IO)
              tripsRepository.initFirestore()
              Log.d("SignIn", "Login result " + account.displayName)
              navigationActions.navigateTo(Route.OVERVIEW)
              // previously sign out
            } else {
              Log.d("MainActivity", "SignIn: Firebase Login Failed")
            }
          }
        }
      }

  private lateinit var placesClient: PlacesClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

    signInClient = GoogleSignIn.getClient(this, gso)
    Places.initialize(applicationContext, MAPS_API_KEY)
    placesClient = Places.createClient(this)

    setContent {
      WanderPalsTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          navController = rememberNavController()
          navigationActions = NavigationActions(navController)

          NavHost(navController = navController, startDestination = Route.SIGN_IN) {
            composable(Route.SIGN_IN) {
              BackHandler(true) {}
              SignIn(onClick = { launcher.launch(signInClient.signInIntent) })
            }
            composable(Route.OVERVIEW) {
              BackHandler(true) {}
              Overview(
                  overviewViewModel = OverviewViewModel(tripsRepository),
                  navigationActions = navigationActions)
            }
            composable(Route.TRIP) { navBackStackEntry ->
              BackHandler(true) {}
              Trip(navigationActions, navigationActions.currentTrip, tripsRepository, placesClient)
            }
            composable(Route.CREATE_TRIP) {
              BackHandler(true) {}
              CreateTrip(OverviewViewModel(tripsRepository), navigationActions)
            }

            composable("${Route.CREATE_SUGGESTION}/{tripId}") { navBackStackEntry ->
              val tripId = navBackStackEntry.arguments?.getString("tripId") ?: ""
              CreateSuggestion(
                  tripId = tripId, viewModel = CreateSuggestionViewModel(tripsRepository))
            }
          }
        }
      }
    }
  }
}
