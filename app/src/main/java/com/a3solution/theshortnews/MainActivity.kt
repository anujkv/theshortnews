package com.a3solution.theshortnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.a3solution.theshortnews.ui.AppNavigation
import com.a3solution.theshortnews.ui.theme.TheShortNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheShortNewsTheme {
                AppNavigation()
            }
        }
    }
}
