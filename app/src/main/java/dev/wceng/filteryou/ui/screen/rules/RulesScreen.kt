package dev.wceng.filteryou.ui.screen.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.launch
import dev.wceng.filteryou.data.model.FilterRule
import dev.wceng.filteryou.data.model.RuleStrategy
import dev.wceng.filteryou.ui.theme.FilterYouTheme

@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Blacklist, 1: Whitelist

    RulesScreenContent(
        uiState = uiState,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onNavigateBack = onNavigateBack,
        onToggleActive = { rule, isActive -> viewModel.updateRule(rule.copy(isActive = isActive)) },
        onDelete = { viewModel.deleteRule(it) },
        onRestore = { viewModel.restoreRule(it) },
        onAddRule = { name, pattern, strategy, type -> viewModel.addRule(name, pattern, strategy, type) },
        getRuleDescription = { viewModel.getRuleDescription(it) },
        getNextDefaultName = { viewModel.getNextDefaultName() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreenContent(
    uiState: RulesUiState,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onToggleActive: (FilterRule, Boolean) -> Unit,
    onDelete: (FilterRule) -> Unit,
    onRestore: (FilterRule) -> Unit,
    onAddRule: (String, String, RuleStrategy, String) -> Unit,
    getRuleDescription: (FilterRule) -> String,
    getNextDefaultName: () -> String
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Filtering Rules", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { onTabSelected(0) },
                        text = { Text("Blacklist", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { onTabSelected(1) },
                        text = { Text("Whitelist", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                contentColor = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Rule")
            }
        }
    ) { padding ->
        val filteredRules = uiState.rules.filter { 
            if (selectedTab == 0) it.ruleType == "BLOCK" else it.ruleType == "ALLOW"
        }

        if (filteredRules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Rounded.Block else Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (selectedTab == 0) "No blacklist rules" else "No whitelist rules",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredRules, key = { it.id }) { rule ->
                    SwipeToDismissRuleItem(
                        onDismiss = {
                            onDelete(rule)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Removed: ${rule.name}",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    onRestore(rule)
                                }
                            }
                        },
                        content = {
                            RuleItem(
                                rule = rule,
                                description = getRuleDescription(rule),
                                onToggleActive = { onToggleActive(rule, it) }
                            )
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddRuleBottomSheet(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, pattern, strategy, ruleType ->
                onAddRule(name, pattern, strategy, ruleType)
                showAddDialog = false
            },
            defaultName = getNextDefaultName(),
            initialRuleType = if (selectedTab == 0) "BLOCK" else "ALLOW"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissRuleItem(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                false // Important: return false to prevent the state from staying "dismissed"
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
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        content()
    }
}

@Composable
fun RuleItem(
    rule: FilterRule,
    description: String,
    onToggleActive: (Boolean) -> Unit
) {
    val isAllow = rule.ruleType == "ALLOW"
    val themeColor = if (isAllow) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = if (rule.isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (rule.isActive) themeColor.copy(alpha = 0.1f) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (rule.isActive) {
                            if (isAllow) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (rule.strategy) {
                        RuleStrategy.LOCATION -> Icons.Rounded.Public
                        RuleStrategy.REGEX -> Icons.Rounded.Code
                        else -> if (isAllow) Icons.Rounded.VerifiedUser else Icons.Rounded.FilterList
                    },
                    contentDescription = null,
                    tint = if (rule.isActive) themeColor else MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rule.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = rule.isActive,
                onCheckedChange = onToggleActive,
                colors = if (isAllow) SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) else SwitchDefaults.colors()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (String, String, RuleStrategy, String) -> Unit,
    defaultName: String,
    initialRuleType: String = "BLOCK"
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant) }
    ) {
        AddRuleBottomSheetContent(
            onConfirm = onConfirm,
            defaultName = defaultName,
            initialRuleType = initialRuleType
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleBottomSheetContent(
    onConfirm: (String, String, RuleStrategy, String) -> Unit,
    defaultName: String,
    initialRuleType: String = "BLOCK"
) {
    var name by remember { mutableStateOf(defaultName) }
    var pattern by remember { mutableStateOf("") }
    var strategy by remember { mutableStateOf(RuleStrategy.CONTAINS) }
    var ruleType by remember { mutableStateOf(initialRuleType) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "New Interception Rule",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = ruleType == "BLOCK",
                onClick = { ruleType = "BLOCK" },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("Blacklist")
            }
            SegmentedButton(
                selected = ruleType == "ALLOW",
                onClick = { ruleType = "ALLOW" },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("Whitelist")
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Rule Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = when (strategy) {
                    RuleStrategy.CONTAINS -> "Contains"
                    RuleStrategy.STARTS_WITH -> "Starts with"
                    RuleStrategy.ENDS_WITH -> "Ends with"
                    RuleStrategy.REGEX -> "Regex"
                    RuleStrategy.LOCATION -> "Location (ISO)"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Strategy") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                shape = MaterialTheme.shapes.large
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                RuleStrategy.entries.forEach { entry ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (entry) {
                                    RuleStrategy.CONTAINS -> "Contains"
                                    RuleStrategy.STARTS_WITH -> "Starts with"
                                    RuleStrategy.ENDS_WITH -> "Ends with"
                                    RuleStrategy.REGEX -> "Regex"
                                    RuleStrategy.LOCATION -> "Location (ISO)"
                                }
                            )
                        },
                        onClick = {
                            strategy = entry
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = pattern,
            onValueChange = { pattern = it },
            label = {
                Text(
                    if (strategy == RuleStrategy.LOCATION) "Region Code (e.g., HK, CN)"
                    else "Number or Keyword"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            singleLine = true
        )

        Button(
            onClick = { onConfirm(name, pattern, strategy, ruleType) },
            enabled = pattern.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            val actionText = if (ruleType == "BLOCK") "Create Block Rule" else "Create Allow Rule"
            Text(actionText, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "Add Rule Sheet")
@Composable
fun AddRuleBottomSheetPreview() {
    FilterYouTheme {
        Surface {
            AddRuleBottomSheetContent(
                onConfirm = { _, _, _, _ -> },
                defaultName = "Rule 5"
            )
        }
    }
}

@Preview(showBackground = true, name = "Blacklist - Light")
@Preview(showBackground = true, name = "Blacklist - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RulesScreenPreview() {
    val mockRules = listOf(
        FilterRule(id = 1, name = "Rule 1", pattern = "400", strategy = RuleStrategy.STARTS_WITH, ruleType = "BLOCK"),
        FilterRule(id = 2, name = "Rule 2", pattern = "^95.*", strategy = RuleStrategy.REGEX, ruleType = "BLOCK"),
        FilterRule(id = 4, name = "Rule 4", pattern = "123", strategy = RuleStrategy.CONTAINS, ruleType = "BLOCK", isActive = false)
    )

    FilterYouTheme {
        RulesScreenContent(
            uiState = RulesUiState(rules = mockRules),
            selectedTab = 0,
            onTabSelected = {},
            onNavigateBack = {},
            onToggleActive = { _, _ -> },
            onDelete = {},
            onRestore = {},
            onAddRule = { _, _, _, _ -> },
            getRuleDescription = { rule -> "Block ${rule.strategy} \"${rule.pattern}\"" },
            getNextDefaultName = { "Rule 5" }
        )
    }
}

@Preview(showBackground = true, name = "Whitelist - Light")
@Preview(showBackground = true, name = "Whitelist - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RulesScreenWhitelistPreview() {
    val mockRules = listOf(
        FilterRule(id = 3, name = "Home Office", pattern = "HK", strategy = RuleStrategy.LOCATION, ruleType = "ALLOW"),
        FilterRule(id = 5, name = "Family", pattern = "+852", strategy = RuleStrategy.STARTS_WITH, ruleType = "ALLOW")
    )

    FilterYouTheme {
        RulesScreenContent(
            uiState = RulesUiState(rules = mockRules),
            selectedTab = 1,
            onTabSelected = {},
            onNavigateBack = {},
            onToggleActive = { _, _ -> },
            onDelete = {},
            onRestore = {},
            onAddRule = { _, _, _, _ -> },
            getRuleDescription = { rule -> "Allow ${rule.strategy} \"${rule.pattern}\"" },
            getNextDefaultName = { "Rule 6" }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RulesScreenEmptyPreview() {
    FilterYouTheme {
        RulesScreenContent(
            uiState = RulesUiState(rules = emptyList()),
            selectedTab = 0,
            onTabSelected = {},
            onNavigateBack = {},
            onToggleActive = { _, _ -> },
            onDelete = {},
            onRestore = {},
            onAddRule = { _, _, _, _ -> },
            getRuleDescription = { "" },
            getNextDefaultName = { "Rule 1" }
        )
    }
}
