package dev.wceng.filteryou.ui.screen.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import dev.wceng.filteryou.data.model.InterceptedLog
import dev.wceng.filteryou.ui.screen.dashboard.LogItem
import dev.wceng.filteryou.ui.theme.FilterYouTheme

@Composable
fun LogsScreen(
    viewModel: LogsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LogsScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onDeleteLog = { viewModel.deleteLog(it) },
        onClearAllLogs = { viewModel.clearAllLogs() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreenContent(
    uiState: LogsUiState,
    onNavigateBack: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDeleteLog: (InterceptedLog) -> Unit,
    onClearAllLogs: () -> Unit
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interception Logs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.logs.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear All")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search number or reason") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            if (uiState.logs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.searchQuery.isEmpty()) "No history found" else "No matches found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.logs, key = { it.id }) { log ->
                        SwipeToDismissLogItem(
                            log = log,
                            onDismiss = { onDeleteLog(log) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Clear All History?") },
            text = { Text("This will permanently remove all intercepted logs. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllLogs()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.extraLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissLogItem(
    log: InterceptedLog,
    onDismiss: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                false
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        LogItem(log)
    }
}

@Preview(showBackground = true, name = "Logs List - Light")
@Preview(showBackground = true, name = "Logs List - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LogsScreenPreview() {
    val mockLogs = listOf(
        InterceptedLog(id = 1, sender = "10086", reason = "Matched rule: Ad", timestamp = System.currentTimeMillis()),
        InterceptedLog(id = 2, sender = "4001234567", reason = "Matched rule: Spam", timestamp = System.currentTimeMillis() - 3600000),
        InterceptedLog(id = 3, sender = "+852 9876 5432", reason = "Matched rule: Overseas", timestamp = System.currentTimeMillis() - 7200000)
    )

    FilterYouTheme {
        LogsScreenContent(
            uiState = LogsUiState(logs = mockLogs),
            onNavigateBack = {},
            onSearchQueryChange = {},
            onDeleteLog = {},
            onClearAllLogs = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty Logs")
@Composable
fun LogsScreenEmptyPreview() {
    FilterYouTheme {
        LogsScreenContent(
            uiState = LogsUiState(logs = emptyList()),
            onNavigateBack = {},
            onSearchQueryChange = {},
            onDeleteLog = {},
            onClearAllLogs = {}
        )
    }
}
