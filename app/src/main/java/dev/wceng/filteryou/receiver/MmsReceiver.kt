package dev.wceng.filteryou.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // MMS interception is complex and often handled by system if not default.
        // For now, we provide the required component for ROLE_SMS.
    }
}
