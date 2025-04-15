package com.example.bug_it.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bug_it.presentation.viewmodel.BugSubmissionViewModel
import com.example.bug_it.presentation.viewmodel.BugSubmissionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugSubmissionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBugs: () -> Unit,
    viewModel: BugSubmissionViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedImageUri = it.toString() }
    }

    LaunchedEffect(uiState) {
        if (uiState is BugSubmissionUiState.Success) {
            title = ""
            description = ""
            selectedImageUri = null
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Bug") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToBugs) {
                        Icon(Icons.Default.List, contentDescription = "View Bugs")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is BugSubmissionUiState.Loading
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = uiState !is BugSubmissionUiState.Loading
                )

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is BugSubmissionUiState.Loading
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Image")
                }

                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Button(
                    onClick = {
                        viewModel.submitBug(title, description, selectedImageUri)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && 
                             description.isNotBlank() && 
                             uiState !is BugSubmissionUiState.Loading
                ) {
                    Text("Submit Bug")
                }

                if (uiState is BugSubmissionUiState.Error) {
                    Text(
                        text = (uiState as BugSubmissionUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (uiState is BugSubmissionUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}