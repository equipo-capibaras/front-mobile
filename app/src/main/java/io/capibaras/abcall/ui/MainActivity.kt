package io.capibaras.abcall.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import io.capibaras.abcall.ui.navigation.Navigation
import io.capibaras.abcall.ui.theme.ABCallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ABCallTheme {
                Navigation()
            }
        }
    }
}