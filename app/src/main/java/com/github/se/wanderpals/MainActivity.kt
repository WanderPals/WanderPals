package com.github.se.wanderpals

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.MainViewModel
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.LocationService
import com.github.se.wanderpals.service.MapManager
import com.github.se.wanderpals.service.NetworkHelper
import com.github.se.wanderpals.service.NotificationPermission
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.service.SharedPreferencesManager
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
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.storage

const val EMPTY_CODE = ""

lateinit var navigationActions: NavigationActions
@SuppressLint("StaticFieldLeak") lateinit var mapManager: MapManager

class MainActivity : ComponentActivity() {

  private lateinit var signInClient: GoogleSignInClient

  private lateinit var context: Context

  private lateinit var networkHelper: NetworkHelper

  private val viewModel: MainViewModel by viewModels {
    MainViewModel.MainViewModelFactory(application)
  }

  // private val viewModelAPI: NotificationAPI by viewModels()

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
                      networkHelper = NetworkHelper(context, viewModel.getTripsRepository())

                      Log.d("MainActivity", "Firebase Initialized")
                      Log.d("SignIn", "Login result " + account.displayName)

                      // set SessionManager User information
                      SessionManager.setUserSession(
                          userId = uid,
                          name = account.displayName ?: "",
                          email = account.email ?: "",
                          profilePhoto = it.result?.user?.photoUrl.toString(),
                          nickname = viewModel.getUserName(account.email ?: ""))
                      viewModel.setUserName(account.email ?: "")
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

  override fun onDestroy() {
    super.onDestroy()
    if (isMapManagerInitialized()) {
      mapManager.executeLocationIntentStop()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val storage = Firebase.storage

    val storageRef = storage.reference

    context = this

    SharedPreferencesManager.init(context)

    val gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

    signInClient = GoogleSignIn.getClient(this, gso)

    mapManager = MapManager(this)
    mapManager.initClients()
    mapManager.setPermissionRequest(locationPermissionRequest)
    mapManager.setLocationIntentStart {
      Intent(applicationContext, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        startService(this)
      }
    }
    mapManager.setLocationIntentStop {
      Intent(applicationContext, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
        startService(this)
      }
    }

    setContent {
      WanderPalsTheme {
        val statusBarColor =
            MaterialTheme.colorScheme.primary.copy(alpha = 1.0f) // Ensuring full opacity
        val activity = LocalContext.current as Activity

        // Updating status bar color based on the theme dynamically
        SideEffect {
          activity.window.statusBarColor = statusBarColor.toArgb()
          WindowInsetsControllerCompat(activity.window, activity.window.decorView)
              .isAppearanceLightStatusBars = statusBarColor.luminance() > 0.5
        }
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Log.d("Hello", "Hello")

          FirebaseMessaging.getInstance()
              .token
              .addOnCompleteListener(
                  OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                      Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                      return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    // Send the token to the server
                    val token = task.result
                    SessionManager.setNotificationToken(task.result)

                    Log.d(TAG, token)
                  })

          navigationActions =
              NavigationActions(
                  mainNavigation = rememberMultiNavigationAppState(startDestination = ROOT_ROUTE),
                  tripNavigation =
                      rememberMultiNavigationAppState(startDestination = Route.DASHBOARD))
          val currentUser = FirebaseAuth.getInstance().currentUser
          val startDestination = if (currentUser != null) Route.OVERVIEW else Route.SIGN_IN
          if (currentUser != null) {
            Log.d("MainActivity", "User is already signed in")
            viewModel.initRepository(currentUser.uid)
            networkHelper = NetworkHelper(context, viewModel.getTripsRepository())

            SessionManager.setUserSession(
                userId = currentUser.uid,
                name =
                    currentUser.isAnonymous.takeIf { it }?.let { "Anonymous User" }
                        ?: currentUser.displayName
                        ?: "",
                email = currentUser.email ?: "",
                profilePhoto = currentUser.photoUrl.toString(),
                nickname =
                    currentUser.isAnonymous.takeIf { it }?.let { "Anonymous User" }
                        ?: viewModel.getUserName(currentUser.email ?: ""))
          }

          NavHost(
              navController = navigationActions.mainNavigation.getNavController,
              startDestination = startDestination,
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
                              networkHelper = NetworkHelper(context, viewModel.getTripsRepository())
                              SessionManager.setUserSession(
                                  userId = uid,
                                  name = "Anonymous User",
                                  email = "",
                                  profilePhoto = result.user?.photoUrl.toString(),
                                  nickname = "Anonymous User")
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
                          networkHelper = NetworkHelper(context, viewModel.getTripsRepository())

                          SessionManager.setUserSession(
                              userId = uid,
                              name = result.user?.email?.substringBefore("@") ?: "",
                              email = result.user?.email ?: "",
                              profilePhoto = result.user?.photoUrl.toString(),
                              nickname = viewModel.getUserName(result.user?.email ?: ""))
                          viewModel.setUserName(email)
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
                  NotificationPermission(context = context)
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
                  Log.d("CREATE_TRIP", "Create Trip")

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
                              key = "AdminPage"),
                      storageReference = storageRef)
                }
              }
        }
      }
    }
  }
}

/** Checks if the map manager is initialized. */
fun isMapManagerInitialized(): Boolean {
  return ::mapManager.isInitialized
}
