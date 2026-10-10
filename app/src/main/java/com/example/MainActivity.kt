package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engine.GestureDispatcher
import com.example.service.BudSwipeMediaSessionService
import com.example.ui.BudSwipeMainApp
import com.example.ui.BudSwipeViewModel
import com.example.ui.theme.BudSwipeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BudSwipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val diagnosticState by viewModel.diagnosticState.collectAsStateWithLifecycle()
            val bluetoothState by viewModel.bluetoothState.collectAsStateWithLifecycle()

            // Runtime Permissions Request for modern Android versions
            val permissionsToRequest = buildList {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(Manifest.permission.BLUETOOTH_CONNECT)
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {
                viewModel.refreshStatus()
            }

            LaunchedEffect(Unit) {
                if (permissionsToRequest.isNotEmpty()) {
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
                // If service is enabled, ensure foreground media service is active
                if (settings.isServiceEnabled) {
                    BudSwipeMediaSessionService.start(this@MainActivity)
                }
            }

            BudSwipeTheme(darkTheme = settings.darkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    BudSwipeMainApp(
                        viewModel = viewModel,
                        settings = settings,
                        diagnosticState = diagnosticState,
                        bluetoothState = bluetoothState
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshStatus()
    }

    /**
     * Intercept hardware key events while MainActivity is focused (for in-app testing).
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode in listOf(
                KeyEvent.KEYCODE_MEDIA_NEXT,
                KeyEvent.KEYCODE_MEDIA_PREVIOUS,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_HEADSETHOOK,
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_VOLUME_DOWN
            )
        ) {
            val handled = GestureDispatcher.onKeyEventReceived(
                context = this,
                event = event,
                source = "MainActivity.dispatchKeyEvent"
            )
            if (handled) return true
        }
        return super.dispatchKeyEvent(event)
    }
}
