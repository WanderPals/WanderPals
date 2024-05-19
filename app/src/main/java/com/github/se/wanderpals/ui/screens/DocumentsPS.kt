package com.github.se.wanderpals.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.github.se.wanderpals.model.viewmodel.DocumentPSViewModel
import com.google.firebase.storage.StorageReference

/**
 * This composable function is used to display the documents of a trip. It displays the documents of
 * the trip and the documents of the current user.
 *
 * @param viewModel the view model of the documents
 * @param storageReference the reference to the storage
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsPS(viewModel: DocumentPSViewModel, storageReference: StorageReference?) {

  val context = LocalContext.current

  val documentslistURL by viewModel.documentslistURL.collectAsState()
  val documentslistUserURL by viewModel.documentslistUserURL.collectAsState()

  // make a topBar to switch between shared and personal documents
  var state by remember { mutableIntStateOf(0) }
  val titles = listOf("Private", "Shared")

  var isDisplayed by remember { mutableStateOf(false) }
  var selectedDocument by remember { mutableStateOf("") }
  var selectedImagesLocal by remember { mutableStateOf<Uri?>(Uri.EMPTY) }
  var displayedTheBoxSelector by remember { mutableStateOf(false) }
  var documentName by remember { mutableStateOf("") }

  // get all the documents from the trip

  LaunchedEffect(Unit) {
    viewModel.getAllDocumentsFromTrip()
    viewModel.getAllDocumentsFromCurrentUser()
  }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri -> selectedImagesLocal = uri })

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("documentsScreen"),

      // make a topBar to switch between shared and personal documents
      // make a list of documents
      topBar = {
        SecondaryTabRow(selectedTabIndex = state) {
          titles.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier.height(70.dp).testTag("tab$title"),
                text = { Text(title) },
                selected = state == index,
                onClick = { state = index })
          }
        }
      },

      // add an add button to add a document on the right bottom

      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("addDocumentButton"),
            onClick = { displayedTheBoxSelector = true }) {
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
                  documentslistURL[it].documentsName,
                  modifier =
                      Modifier.padding(20.dp)
                          .clickable(
                              onClick = {
                                isDisplayed = true
                                selectedDocument = documentslistURL[it].documentsURL
                              })
                          .testTag("document$it"))
              Log.d("Docs", "Document $it")
            }
          }
        } else {

          LazyColumn(modifier = Modifier.padding(it)) {
            items(documentslistUserURL.size) {
              Text(
                  documentslistUserURL[it].documentsName,
                  modifier =
                      Modifier.padding(20.dp)
                          .clickable(
                              onClick = {
                                isDisplayed = true
                                selectedDocument = documentslistUserURL[it].documentsURL
                              })
                          .testTag("documentUser$it"))
              Log.d("Docs", "Document $it")
            }
          }
        }
      }

  if (isDisplayed) {
    Box(
        modifier =
            Modifier.fillMaxSize()
                .clickable(onClick = { isDisplayed = false })
                .testTag("documentImageBox")) {
          AsyncImage(
              model = selectedDocument,
              contentDescription = "Document",
              modifier = Modifier.fillMaxSize().testTag("documentImage"))
        }
  }

  if (displayedTheBoxSelector) {
    Dialog(onDismissRequest = { displayedTheBoxSelector = false }) {
      Card(
          colors = CardDefaults.cardColors(contentColor = androidx.compose.ui.graphics.Color.Black),
          modifier = Modifier.size(370.dp, 300.dp).testTag("documentBox")) {

            // set the name of the documents
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  OutlinedTextField(
                      modifier = Modifier.size(250.dp, 60.dp).testTag("documentNameBox"),
                      value = documentName,
                      onValueChange = { documentName = it },
                      label = {
                        Text(
                            text = "Document Name",
                            style =
                                TextStyle(
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 0.5.sp,
                                ),
                        )
                      },
                      singleLine = true)

                  // Button to add the document from the media picker
                  FloatingActionButton(
                      onClick = { singlePhotoPickerLauncher.launch(PickVisualMediaRequest()) },
                      modifier =
                          Modifier.padding(top = 20.dp)
                              .size(width = 200.dp, height = 50.dp)
                              .testTag("addDocumentButton")) {
                        Text(
                            text = "Add Document",
                            style =
                                TextStyle(
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 0.5.sp,
                                ),
                        )
                      }

                  // button to accept the document
                  Row(
                      modifier = Modifier.padding(top = 10.dp),
                      horizontalArrangement = Arrangement.SpaceEvenly) {
                        FloatingActionButton(
                            onClick = {
                              if (selectedImagesLocal != Uri.EMPTY && documentName != "") {
                                viewModel.addDocument(
                                    documentName,
                                    selectedImagesLocal!!,
                                    titles[state],
                                    context,
                                    storageReference,
                                    state)
                              }
                              selectedImagesLocal = Uri.EMPTY
                              displayedTheBoxSelector = false
                            },
                            modifier =
                                Modifier.padding(top = 10.dp)
                                    .size(width = 100.dp, height = 50.dp)
                                    .testTag("acceptButton")) {
                              Text(
                                  text = "Accept",
                                  style =
                                      TextStyle(
                                          fontSize = 16.sp,
                                          textAlign = TextAlign.Center,
                                          letterSpacing = 0.5.sp,
                                      ),
                              )
                            }
                        Spacer(modifier = Modifier.width(50.dp))
                        // cancel button
                        FloatingActionButton(
                            onClick = {
                              selectedImagesLocal = Uri.EMPTY
                              displayedTheBoxSelector = false
                            },
                            modifier =
                                Modifier.padding(top = 10.dp)
                                    .size(width = 100.dp, height = 50.dp)
                                    .testTag("cancelButton")) {
                              Text(
                                  text = "Cancel",
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
  }
}
