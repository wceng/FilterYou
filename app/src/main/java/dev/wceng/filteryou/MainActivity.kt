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
import dagger.hilt.android.AndroidEntryPoint
import dev.wceng.filteryou.ui.navigation.FilterYouNavigation
import dev.wceng.filteryou.ui.theme.FilterYouTheme
import dev.wceng.filteryou.util.RoleManagerHelper

@AndroidEntryPoint
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

    var isCallRoleHeld by remember { mutableStateOf(false) }

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
            isCallRoleHeld = roleHelper.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        } else {
            isCallRoleHeld = true
        }
    }

    if (!isCallRoleHeld) {
        RoleSetupScreen(
            isCallRoleHeld = isCallRoleHeld,
            onRequestCallRole = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    roleHelper.requestRole(context as ComponentActivity, RoleManager.ROLE_CALL_SCREENING, callRoleLauncher)
                }
            }
        )
    } else {
        FilterYouNavigation()
    }
}

@Composable
fun RoleSetupScreen(
    isCallRoleHeld: Boolean,
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
                text = "Permission Required",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FilterYou needs to be set as your call screener to protect you from unwanted calls.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))

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
