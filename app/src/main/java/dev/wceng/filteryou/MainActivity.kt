package dev.wceng.filteryou

import android.app.Activity
import android.app.role.RoleManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wceng.filteryou.data.local.FilterYouDatabase
import dev.wceng.filteryou.data.local.PreferencesDataStore
import dev.wceng.filteryou.data.repository.FilterRepository
import dev.wceng.filteryou.data.repository.SettingsRepository
import dev.wceng.filteryou.ui.navigation.FilterYouNavigation
import dev.wceng.filteryou.ui.theme.FilterYouTheme
import dev.wceng.filteryou.util.RoleManagerHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilterYouTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val context = LocalContext.current
    val roleHelper = remember { RoleManagerHelper(context) }
    
    var isSmsRoleHeld by remember { mutableStateOf(false) }
    var isCallRoleHeld by remember { mutableStateOf(false) }

    val smsRoleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isSmsRoleHeld = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                roleHelper.isRoleHeld(RoleManager.ROLE_SMS)
            } else true
            Toast.makeText(context, "SMS Role Granted", Toast.LENGTH_SHORT).show()
        }
    }

    val callRoleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isCallRoleHeld = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                roleHelper.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
            } else true
            Toast.makeText(context, "Call Screening Role Granted", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isSmsRoleHeld = roleHelper.isRoleHeld(RoleManager.ROLE_SMS)
            isCallRoleHeld = roleHelper.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        } else {
            isSmsRoleHeld = true
            isCallRoleHeld = true
        }
    }

    if (!isSmsRoleHeld || !isCallRoleHeld) {
        RoleSetupScreen(
            isSmsRoleHeld = isSmsRoleHeld,
            isCallRoleHeld = isCallRoleHeld,
            onRequestSmsRole = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    roleHelper.requestRole(context as ComponentActivity, RoleManager.ROLE_SMS, smsRoleLauncher)
                }
            },
            onRequestCallRole = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    roleHelper.requestRole(context as ComponentActivity, RoleManager.ROLE_CALL_SCREENING, callRoleLauncher)
                }
            }
        )
    } else {
        val database = remember { FilterYouDatabase.getDatabase(context) }
        val preferencesDataStore = remember { PreferencesDataStore(context) }
        val filterRepository = remember { FilterRepository(database.logDao(), database.ruleDao()) }
        val settingsRepository = remember { SettingsRepository(preferencesDataStore) }

        FilterYouNavigation(
            filterRepository = filterRepository,
            settingsRepository = settingsRepository
        )
    }
}

@Composable
fun RoleSetupScreen(
    isSmsRoleHeld: Boolean,
    isCallRoleHeld: Boolean,
    onRequestSmsRole: () -> Unit,
    onRequestCallRole: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Permissions Required",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FilterYou needs to be set as your default SMS app and call screener to protect you from unwanted communications.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (!isSmsRoleHeld && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Button(
                    onClick = onRequestSmsRole,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set as Default SMS App")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isCallRoleHeld && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Button(
                    onClick = onRequestCallRole,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable Call Screening")
                }
            }
        }
    }
}
