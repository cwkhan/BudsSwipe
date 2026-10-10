package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.example.BudSwipeApp
import com.example.MainActivity
import com.example.R
import com.example.engine.GestureDispatcher

class BudSwipeMediaSessionService : Service() {

    private var mediaSession: MediaSession? = null

    companion object {
        const val NOTIFICATION_ID = 2001
        const val ACTION_START = "com.budsweep.app.START_SERVICE"
        const val ACTION_STOP = "com.budsweep.app.STOP_SERVICE"

        var isRunning: Boolean = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, BudSwipeMediaSessionService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, BudSwipeMediaSessionService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setupMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForegroundServiceNotification()
        isRunning = true
        GestureDispatcher.updateMediaSessionStatus(true)
        return START_STICKY
    }

    private fun setupMediaSession() {
        mediaSession = MediaSession(this, "BudSwipeMediaSession").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val keyEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                    }

                    if (keyEvent != null) {
                        val handled = GestureDispatcher.onKeyEventReceived(
                            context = this@BudSwipeMediaSessionService,
                            event = keyEvent,
                            source = "MediaSession.Callback"
                        )
                        if (handled) return true
                    }
                    return super.onMediaButtonEvent(mediaButtonIntent)
                }

                override fun onSkipToNext() {
                    val dummyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)
                    GestureDispatcher.onKeyEventReceived(this@BudSwipeMediaSessionService, dummyEvent, "MediaSession.onSkipToNext")
                }

                override fun onSkipToPrevious() {
                    val dummyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                    GestureDispatcher.onKeyEventReceived(this@BudSwipeMediaSessionService, dummyEvent, "MediaSession.onSkipToPrevious")
                }

                override fun onPlay() {
                    val dummyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                    GestureDispatcher.onKeyEventReceived(this@BudSwipeMediaSessionService, dummyEvent, "MediaSession.onPlay")
                }

                override fun onPause() {
                    val dummyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                    GestureDispatcher.onKeyEventReceived(this@BudSwipeMediaSessionService, dummyEvent, "MediaSession.onPause")
                }
            })

            val stateBuilder = PlaybackState.Builder()
                .setActions(
                    PlaybackState.ACTION_PLAY or
                            PlaybackState.ACTION_PAUSE or
                            PlaybackState.ACTION_PLAY_PAUSE or
                            PlaybackState.ACTION_SKIP_TO_NEXT or
                            PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackState.ACTION_FAST_FORWARD or
                            PlaybackState.ACTION_REWIND
                )
                .setState(PlaybackState.STATE_PLAYING, 0L, 1.0f)

            setPlaybackState(stateBuilder.build())
            isActive = true
        }
    }

    private fun startForegroundServiceNotification() {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, BudSwipeApp.CHANNEL_ID)
            .setContentTitle("BudSwipe Active")
            .setContentText("Listening for earbud taps to navigate Reels")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        isRunning = false
        mediaSession?.apply {
            isActive = false
            release()
        }
        mediaSession = null
        GestureDispatcher.updateMediaSessionStatus(false)
        super.onDestroy()
    }
}
