package com.github.se.wanderpals.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.se.wanderpals.model.viewmodel.DocumentPSViewModel
import com.google.firebase.storage.StorageReference

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsPS(
    tripId: String,
    viewModel: DocumentPSViewModel,
    storageReference: StorageReference?
) {

  val context = LocalContext.current

  val documentslistURL by viewModel.documentslistURL.collectAsState()
  val documentslistUserURL by viewModel.documentslistUserURL.collectAsState()

  // make a topBar to switch between shared and personal documents
  var state by remember { mutableIntStateOf(0) }
  val titles = listOf("Private", "Shared")

  var isDisplayed by remember { mutableStateOf(false) }
  var selectedDocument by remember { mutableStateOf("") }
  var selectedImagesLocal by remember { mutableStateOf<List<Uri?>>(emptyList()) }

  var expanded by remember { mutableStateOf(false) }

  // get all the documents from the trip

  LaunchedEffect(Unit) {
    viewModel.getAllDocumentsFromTrip()
    viewModel.getAllDocumentsFromCurrentUser()
  }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri -> selectedImagesLocal = listOf(uri) })

  Scaffold(
      modifier = Modifier.fillMaxSize(),

      // make a topBar to switch between shared and personal documents
      // make a list of documents
      topBar = {
        SecondaryTabRow(selectedTabIndex = state) {
          titles.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier.height(70.dp),
                text = { Text(title) },
                selected = state == index,
                onClick = { state = index })
          }
        }
      },

      // add an add button to add a document on the right bottom

      floatingActionButton = {
        FloatingActionButton(
            onClick = { singlePhotoPickerLauncher.launch(PickVisualMediaRequest()) }) {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Add document",
              )
            }
      }) { it ->
        if (state == 1) {
          LazyColumn(modifier = Modifier.padding(it)) {
            items(documentslistURL.size) {
              Text(
                  "Document $it",
                  modifier =
                      Modifier.padding(20.dp)
                          .clickable(
                              onClick = {
                                isDisplayed = true
                                selectedDocument = documentslistURL[it]
                              }))
              Log.d("Docs", "Document $it")
            }
          }
        } else {

          LazyColumn(modifier = Modifier.padding(it)) {
            items(documentslistUserURL.size) {
              Text(
                  "Document $it",
                  modifier =
                      Modifier.padding(20.dp)
                          .clickable(
                              onClick = {
                                isDisplayed = true
                                selectedDocument = documentslistUserURL[it]
                              }))
              Log.d("Docs", "Document $it")
            }
          }
        }
      }

  if (selectedImagesLocal.isNotEmpty() && selectedImagesLocal[0] != null) {
    Log.d("Admin", "Selected Image: ${selectedImagesLocal[0]}")
    // create a reference to the uri of the image
    val riversRef =
        storageReference?.child(
            "documents/${titles[state]}/${selectedImagesLocal[0]?.lastPathSegment}")
    // upload the image to the firebase storage
    val taskUp = riversRef?.putFile(selectedImagesLocal[0]!!)

    // Register observers to listen for state changes
    // and progress of the upload
    taskUp
        ?.addOnFailureListener {
          // Handle unsuccessful uploads
          Log.d("Admin", "Failed to upload image")
        }
        ?.addOnSuccessListener {
          // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
          Log.d("Document", "Image uploaded successfully")
          Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
        }
    // Continue with the task to get the download URL
    taskUp
        ?.continueWithTask { task ->
          if (!task.isSuccessful) {
            task.exception?.let { throw it }
          }
          riversRef.downloadUrl
        }
        ?.addOnCompleteListener { task ->
          if (task.isSuccessful && state == 0) {
            viewModel.updateDocumentsOfCurrentUser(task.result.toString())
            // empty the list
            selectedImagesLocal = emptyList()
            Log.d("Admin", "Image URL: ${task.result}")
          } else if (task.isSuccessful && state == 1) {
            viewModel.addDocumentToTrip(task.result.toString(), tripId)
            // empty the list
            selectedImagesLocal = emptyList()
            Log.d("Admin", "Image URL: ${task.result}")
          }
        }
  }

  if (isDisplayed) {
    Box(modifier = Modifier.fillMaxSize().clickable(onClick = { isDisplayed = false })) {
      AsyncImage(
          model = selectedDocument,
          contentDescription = "Document",
          modifier = Modifier.fillMaxSize())
    }
  }
}
