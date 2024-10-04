package com.misoux.abcall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.misoux.abcall.ui.navigation.Navigation
import com.misoux.abcall.ui.theme.ABCallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ABCallTheme {
                Navigation()
            }
        }
    }
}