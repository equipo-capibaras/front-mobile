package io.capibaras.abcall.ui.util

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun isAccessibilityEnabled(context: Context): Boolean {
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return accessibilityManager.isEnabled
}

fun navigateWithAccessibilityCheck(
    navController: NavController,
    destination: String,
    context: Context,
    dispatcher: CoroutineDispatcher,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    delayMillis: Long = 1500
) {
    CoroutineScope(dispatcher).launch {
        if (isAccessibilityEnabled(context)) {
            delay(delayMillis)
        }
        withContext(mainDispatcher) {
            navController.navigate(destination)
        }
    }
}