package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import com.example.engine.GestureDispatcher

class BudSwipeMediaButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (Intent.ACTION_MEDIA_BUTTON == intent.action) {
            val keyEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
            }

            if (keyEvent != null) {
                GestureDispatcher.onKeyEventReceived(
                    context = context,
                    event = keyEvent,
                    source = "Bluetooth MediaButtonReceiver"
                )
            }
        }
    }
}
