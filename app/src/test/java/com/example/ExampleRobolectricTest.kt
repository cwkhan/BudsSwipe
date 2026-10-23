package com.example

import android.content.Context
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import com.example.engine.GestureDispatcher
import com.example.model.BudSwipeSettings
import com.example.model.SwipeAction
import com.example.model.TargetApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `read string from context verifies BudSwipe app name`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("BudSwipe", appName)
    }

    @Test
    fun `default settings resolve next and previous keys correctly`() {
        val defaultSettings = BudSwipeSettings(
            isServiceEnabled = true,
            leftBudAction = SwipeAction.SWIPE_UP,
            rightBudAction = SwipeAction.SWIPE_DOWN,
            leftTriggerKeyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            rightTriggerKeyCode = KeyEvent.KEYCODE_MEDIA_NEXT
        )
        GestureDispatcher.updateSettings(defaultSettings)

        val leftResolved = GestureDispatcher.resolveActionForKeyCode(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        assertEquals(SwipeAction.SWIPE_UP, leftResolved)

        val rightResolved = GestureDispatcher.resolveActionForKeyCode(KeyEvent.KEYCODE_MEDIA_NEXT)
        assertEquals(SwipeAction.SWIPE_DOWN, rightResolved)
    }

    @Test
    fun `target app filtering identifies Instagram YouTube and Facebook packages`() {
        GestureDispatcher.updateSettings(BudSwipeSettings(targetApp = TargetApp.ALL_SHORTS, requireTargetAppForeground = true))

        GestureDispatcher.updateForegroundPackage("com.instagram.android")
        assertTrue(GestureDispatcher.diagnosticState.value.isInTargetApp)

        GestureDispatcher.updateForegroundPackage("com.google.android.youtube")
        assertTrue(GestureDispatcher.diagnosticState.value.isInTargetApp)

        GestureDispatcher.updateForegroundPackage("com.facebook.katana")
        assertTrue(GestureDispatcher.diagnosticState.value.isInTargetApp)

        GestureDispatcher.updateForegroundPackage("com.random.unrelated.app")
        assertFalse(GestureDispatcher.diagnosticState.value.isInTargetApp)
    }

    @Test
    fun `disabled master switch ignores gestures safely`() {
        GestureDispatcher.updateSettings(BudSwipeSettings(isServiceEnabled = false))

        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)
        val handled = GestureDispatcher.onKeyEventReceived(context, downEvent, "UnitTest")
        assertFalse(handled)
    }
}
