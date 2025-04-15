package com.example.bug_it.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bug_it.domain.model.Bug
import com.example.bug_it.presentation.viewmodel.BugsViewModel
import com.example.bug_it.presentation.viewmodel.BugsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugsScreen(
    onNavigateBack: () -> Unit,
    viewModel: BugsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getBugs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bugs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            when (uiState) {
                is BugsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BugsUiState.Success -> {
                    val bugs = (uiState as BugsUiState.Success).bugs
                    if (bugs.isEmpty()) {
                        Text(
                            text = "No bugs found",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(bugs) { bug ->
                                BugItem(bug = bug)
                            }
                        }
                    }
                }
                is BugsUiState.Error -> {
                    Text(
                        text = (uiState as BugsUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun BugItem(bug: Bug) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = bug.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = bug.description,
                style = MaterialTheme.typography.bodyMedium
            )
            if (bug.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = bug.imageUrl,
                    contentDescription = "Bug image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            Text(
                text = "Status: ${bug.status.name}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Reported: ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(bug.timestamp))}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 