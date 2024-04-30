package com.github.se.wanderpals

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.MainViewModel
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.MapManager
import com.github.se.wanderpals.service.NotificationPermission
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.service.ShowRationalPermissionDialog
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.Route.ROOT_ROUTE
import com.github.se.wanderpals.ui.navigation.rememberMultiNavigationAppState
import com.github.se.wanderpals.ui.screens.Admin
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.github.se.wanderpals.ui.screens.SignIn
import com.github.se.wanderpals.ui.screens.overview.Overview
import com.github.se.wanderpals.ui.screens.suggestion.CreateSuggestion
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging

const val EMPTY_CODE = ""

lateinit var navigationActions: NavigationActions

class MainActivity : ComponentActivity() {

  private lateinit var signInClient: GoogleSignInClient

  private lateinit var mapManager: MapManager

  private lateinit var context: Context

  private val viewModel: MainViewModel by viewModels {
    MainViewModel.MainViewModelFactory(application)
  }

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
                      Log.d("MainActivity", "Firebase UID: $uid")
                      viewModel.initRepository(uid)
                      Log.d("MainActivity", "Firebase Initialized")
                      Log.d("SignIn", "Login result " + account.displayName)

                      // set SessionManager User information
                      SessionManager.setUserSession(
                          userId = uid,
                          name = account.displayName ?: "",
                          email = account.email ?: "",
                          profilePhoto = it.result.user?.photoUrl.toString())

                      navigationActions.navigateTo(Route.OVERVIEW)
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

  private val locationPermissionRequest =
      registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions
        ->
        when {
          permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
            Log.d("MapActivity", "Fine location access granted.")
          }
          permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
            Log.d("MapActivity", "Coarse location access granted.")
          }
          else -> {
            Log.d("MapActivity", "Location access denied.")
          }
        }
      }

  @Suppress("DEPRECATION")
  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    super.onBackPressed()
    if (::navigationActions.isInitialized) {
      navigationActions.updateCurrentRoute()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    context = this

    val gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

    signInClient = GoogleSignIn.getClient(this, gso)

    mapManager = MapManager(this)
    mapManager.initClients()
    mapManager.setPermissionRequest(locationPermissionRequest)

    setContent {
      WanderPalsTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NotificationPermission(context = context)

            var token = ""

            /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                //Send the token to the server
                token = task.result
                Log.d(TAG, token)

            })*/
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
                        FirebaseAuth.getInstance()
                            .signInAnonymously()
                            .addOnSuccessListener { result ->
                              val uid = result.user?.uid ?: ""
                              viewModel.initRepository(uid)
                              SessionManager.setUserSession(
                                  userId = uid, name = "Anonymous User", email = "")
                              navigationActions.navigateTo(Route.OVERVIEW)
                            }
                            .addOnFailureListener {
                              Toast.makeText(context, "FireBase Failed", Toast.LENGTH_SHORT).show()
                            }
                      },
                      onClick3 = { email, password ->
                        val onSucess = { result: AuthResult ->
                          val uid = result.user?.uid ?: ""
                          viewModel.initRepository(uid)
                          SessionManager.setUserSession(
                              userId = uid,
                              name = result.user?.displayName ?: "",
                              email = result.user?.email ?: "")
                          navigationActions.navigateTo(Route.OVERVIEW)
                        }
                        FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result -> onSucess(result) }
                            .addOnFailureListener {
                              FirebaseAuth.getInstance()
                                  .createUserWithEmailAndPassword(email, password)
                                  .addOnSuccessListener { result -> onSucess(result) }
                                  .addOnFailureListener {
                                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                  }
                            }
                      })
                }
                composable(Route.OVERVIEW) {
                  val overviewViewModel: OverviewViewModel =
                      viewModel(
                          factory =
                              OverviewViewModel.OverviewViewModelFactory(
                                  viewModel.getTripsRepository()),
                          key = "Overview")
                  Overview(
                      overviewViewModel = overviewViewModel, navigationActions = navigationActions)
                }
                composable(Route.TRIP) {
                  navigationActions.tripNavigation.setNavController(rememberNavController())
                  val tripId = navigationActions.variables.currentTrip
                  navigationActions.tripNavigation.setNavController(rememberNavController())
                  Trip(navigationActions, tripId, viewModel.getTripsRepository(), mapManager)
                }
                composable(Route.CREATE_TRIP) {
                  val overviewViewModel: OverviewViewModel =
                      viewModel(
                          factory =
                              OverviewViewModel.OverviewViewModelFactory(
                                  viewModel.getTripsRepository()),
                          key = "Overview")
                  CreateTrip(overviewViewModel, navigationActions)
                }

                composable(Route.CREATE_SUGGESTION) {
                  val suggestion = navigationActions.variables.currentSuggestion
                  Log.d("CREATE_SUGGESTION", "Location: ${suggestion.stop.address}")
                  Log.d("CREATE_SUGGESTION", "GeoCords: ${suggestion.stop.geoCords}")
                  val onAction: () -> Unit = { navigationActions.goBack() }

                  val createSuggestionViewModel: CreateSuggestionViewModel =
                      viewModel(
                          factory =
                              CreateSuggestionViewModel.CreateSuggestionViewModelFactory(
                                  viewModel.getTripsRepository()),
                          key = "CreateSuggestion")
                  CreateSuggestion(
                      tripId = navigationActions.variables.currentTrip,
                      viewModel = createSuggestionViewModel,
                      suggestion = suggestion,
                      onSuccess = onAction,
                      onCancel = onAction)
                }
                composable(Route.ADMIN_PAGE) {
                  Admin(
                      adminViewModel =
                          viewModel(
                              factory =
                                  AdminViewModel.AdminViewModelFactory(
                                      navigationActions.variables.currentTrip,
                                      viewModel.getTripsRepository()),
                              key = "AdminPage"))
                }
              }
        }
      }
    }
  }
}
