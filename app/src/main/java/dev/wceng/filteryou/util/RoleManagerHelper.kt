package dev.wceng.filteryou.util

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi

class RoleManagerHelper(private val context: Context) {

    private val roleManager: RoleManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getSystemService(RoleManager::class.java)
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun isRoleHeld(role: String): Boolean {
        return roleManager?.isRoleHeld(role) ?: false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestRole(activity: ComponentActivity, role: String, launcher: ActivityResultLauncher<Intent>) {
        if (!isRoleHeld(role)) {
            val intent = roleManager?.createRequestRoleIntent(role)
            if (intent != null) {
                launcher.launch(intent)
            }
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.Q)
        const val ROLE_SMS = RoleManager.ROLE_SMS
        @RequiresApi(Build.VERSION_CODES.Q)
        const val ROLE_CALL_SCREENING = RoleManager.ROLE_CALL_SCREENING
    }
}
