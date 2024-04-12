package com.github.se.wanderpals

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.BuildConfig.MAPS_API_KEY
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.github.se.wanderpals.ui.screens.SignIn
import com.github.se.wanderpals.ui.screens.overview.Overview
import com.github.se.wanderpals.ui.screens.suggestion.CreateSuggestion
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import java.security.MessageDigest

const val EMPTY_CODE = ""

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
          FirebaseAuth.getInstance()
              .signInWithCredential(credential)
              .addOnCompleteListener {
                if (it.isSuccessful) {
                  Log.d("MainActivity", "SignIn: Firebase Login Completed Successfully")
                  val uid = it.result?.user?.uid ?: ""
                  Log.d("MainActivity", "Firebase UID: $uid")
                  tripsRepository = TripsRepository(uid, Dispatchers.IO)
                  tripsRepository.initFirestore()
                  Log.d("MainActivity", "Firebase Initialized")
                  Log.d("SignIn", "Login result " + account.displayName)
                  navigationActions.navigateTo(Route.OVERVIEW)
                } else {
                  Log.d("MainActivity", "SignIn: Firebase Login Completed Unsuccessfully")
                }
              }
              .addOnFailureListener { Log.d("MainActivity", "SignIn: Firebase Login Failed") }
              .addOnCanceledListener { Log.d("MainActivity", "SignIn: Firebase Login Canceled") }
        }
      }

  private lateinit var placesClient: PlacesClient

  private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toString()
  }

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
              val context = ApplicationProvider.getApplicationContext<Context>()
              BackHandler(true) {}
              SignIn(onClick1 = { launcher.launch(signInClient.signInIntent) }, onClick2 = {

                tripsRepository = TripsRepository(it.md5(), Dispatchers.IO)
                tripsRepository.initFirestore(FirebaseApp.initializeApp(context)!!)
                navigationActions.navigateTo(Route.OVERVIEW)
              })
            }
            composable(Route.OVERVIEW) {
              BackHandler(true) {}
              Overview(
                  overviewViewModel = OverviewViewModel(tripsRepository),
                  navigationActions = navigationActions)
            }
            composable(Route.TRIP) {
              BackHandler(true) {}
              Trip(navigationActions, navigationActions.currentTrip, tripsRepository, placesClient)
            }
            composable(Route.CREATE_TRIP) {
              BackHandler(true) {}
              CreateTrip(OverviewViewModel(tripsRepository), navigationActions)
            }

            composable(Route.CREATE_SUGGESTION) { navBackStackEntry ->
              BackHandler(true) {}
              CreateSuggestion(
                  tripId = navigationActions.currentTrip,
                  viewModel = CreateSuggestionViewModel(tripsRepository))
            }
          }
        }
      }
    }
  }
}
