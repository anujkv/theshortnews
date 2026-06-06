package com.a3solution.theshortnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.a3solution.theshortnews.ui.AppNavigation
import com.a3solution.theshortnews.ui.theme.TheShortNewsTheme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.a3solution.theshortnews.ui.AppNavigation
import com.a3solution.theshortnews.ui.theme.TheShortNewsTheme
import com.a3solution.theshortnews.utils.NetworkUtils
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val useTwoPane = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

            TheShortNewsTheme {
                val context = LocalContext.current
                val networkUtils = remember { NetworkUtils(context) }
                val isConnected by networkUtils.observeConnectivity().collectAsState(initial = true)
                val snackbarHostState = remember { SnackbarHostState() }
                var isFirstLaunch by remember { mutableStateOf(true) }

                LaunchedEffect(isConnected) {
                    if (isFirstLaunch) {
                        isFirstLaunch = false
                        if (isConnected) return@LaunchedEffect
                    }

                    val message = if (isConnected) "Back online" else "No internet connection"
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Indefinite
                    )
                }

                LaunchedEffect(isConnected) {
                    delay(2000)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) { data ->
                            ConnectivitySnackbar(
                                message = data.visuals.message,
                                isConnected = isConnected
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        useTwoPane = useTwoPane
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectivitySnackbar(
    message: String,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isConnected) {
        Color(0xFF2E7D32) // Success Green
    } else {
        Color(0xFFD32F2F) // Danger Red
    }

    val icon = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Error

    Snackbar(
        modifier = modifier.padding(12.dp),
        containerColor = backgroundColor,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = message)
        }
    }
}



