package com.github.se.wanderpals

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.BuildConfig.MAPS_API_KEY
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.NavigationActionsVariables
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.Route.ROOT_ROUTE
import com.github.se.wanderpals.ui.navigation.globalVariables
import com.github.se.wanderpals.ui.navigation.rememberMultiNavigationAppState
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

const val EMPTY_CODE = ""

class MainActivity : ComponentActivity() {

  private lateinit var signInClient: GoogleSignInClient

  private lateinit var navigationActions: NavigationActions

  private lateinit var tripsRepository: TripsRepository

  private lateinit var context: Context

  private val launcher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        task
            .addOnSuccessListener { account ->
              val googleTokenId = account.idToken ?: ""
              val credential = GoogleAuthProvider.getCredential(googleTokenId, null)
              FirebaseAuth.getInstance()
                  .signInWithCredential(credential)
                  .addOnCompleteListener {
                    if (it.isSuccessful) {
                      Log.d("MainActivity", "SignIn: Firebase Login Completed Successfully")
                      val uid = it.result?.user?.uid ?: ""
                      val displayName = account.displayName ?: ""
                      val email = account.email ?: ""
                      Log.d("MainActivity", "Firebase UID: $uid")
                      tripsRepository = TripsRepository(uid, Dispatchers.IO)
                      tripsRepository.initFirestore()
                      Log.d("MainActivity", "Firebase Initialized")
                      Log.d("SignIn", "Login result " + account.displayName)

                      // set SessionManager User information
                      SessionManager.setUserSession(userId = uid, name = displayName, email = email)

                      navigationActions.mainNavigation.navigateTo(Route.OVERVIEW)
                    } else {
                      Toast.makeText(context, "FireBase Failed", Toast.LENGTH_SHORT).show()
                    }
                  }
                  .addOnFailureListener {
                    Toast.makeText(context, "FireBase Failed", Toast.LENGTH_SHORT).show()
                  }
                  .addOnCanceledListener {
                    Toast.makeText(context, "FireBase Canceled", Toast.LENGTH_SHORT).show()
                  }
            }
            .addOnFailureListener {
              Toast.makeText(context, "Check Google Privacy Settings", Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
              Toast.makeText(context, "Check Google Privacy Settings", Toast.LENGTH_SHORT).show()
            }
      }

  private lateinit var placesClient: PlacesClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    context = this

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
          globalVariables = NavigationActionsVariables()
          navigationActions =
              NavigationActions(
                  mainNavigation = rememberMultiNavigationAppState(startDestination = ROOT_ROUTE),
                  tripNavigation =
                      rememberMultiNavigationAppState(startDestination = Route.DASHBOARD))

          NavHost(
              navController = navigationActions.mainNavigation.getNavController,
              startDestination = Route.SIGN_IN,
              route = ROOT_ROUTE) {
                composable(Route.SIGN_IN) {
                  SignIn(
                      onClick1 = { launcher.launch(signInClient.signInIntent) },
                      onClick2 = {
                        tripsRepository = TripsRepository(it.hashCode().toString(), Dispatchers.IO)
                        tripsRepository.initFirestore(FirebaseApp.initializeApp(context)!!)
                        val displayName = it.substringBefore('@')
                        SessionManager.setUserSession(
                            userId = it.hashCode().toString(), name = displayName, email = it)
                        navigationActions.mainNavigation.navigateTo(Route.OVERVIEW)
                      })
                }
                composable(Route.OVERVIEW) {
                  Overview(
                      overviewViewModel = OverviewViewModel(tripsRepository),
                      navigationActions = navigationActions)
                }
                composable(Route.TRIP) {
                  navigationActions.tripNavigation.setNavController(rememberNavController())
                  val routeToGo =
                      if (navigationActions.variables.suggestionId != "") {
                        Route.SUGGESTION
                      } else if (navigationActions.variables.currentAddress != "") {
                        Route.MAP
                      } else {
                        Route.DASHBOARD
                      }
                  val tripId = navigationActions.variables.currentTrip
                  navigationActions.tripNavigation.setNavController(rememberNavController())
                  navigationActions.tripNavigation.setStartDestination(routeToGo)
                  Trip(navigationActions, tripId, tripsRepository, placesClient)
                }
                composable(Route.CREATE_TRIP) {
                  CreateTrip(OverviewViewModel(tripsRepository), navigationActions)
                }

                composable(Route.CREATE_SUGGESTION) {
                  val loc = navigationActions.variables.currentAddress
                  val cord = navigationActions.variables.currentGeoCords
                  Log.d("CREATE_SUGGESTION", "Location: $loc")
                  Log.d("CREATE_SUGGESTION", "GeoCords: $cord")
                  val onAction: () -> Unit = {
                    if (loc != "") {
                      navigationActions.tripNavigation.setStartDestination(Route.MAP)
                      navigationActions.navigateTo(Route.TRIP) // Directly to map
                    } else {
                      navigationActions.tripNavigation.setStartDestination(Route.SUGGESTION)
                      navigationActions.navigateTo(Route.TRIP) // Directly to suggestion
                    }
                  }
                  CreateSuggestion(
                      tripId = navigationActions.variables.currentTrip,
                      viewModel = CreateSuggestionViewModel(tripsRepository),
                      addr = loc,
                      geoCords = cord,
                      onSuccess = onAction,
                      onCancel = onAction)
                }
              }
        }
      }
    }
  }
}
