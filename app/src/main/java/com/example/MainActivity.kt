package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screen.EmulatorScreen
import com.example.ui.screen.MainDashboardScreen
import com.example.ui.screen.WorkspaceScreen
import com.example.ui.screen.WorkstationScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AgentForgeViewModel

enum class NavigationScreen {
    DASHBOARD, WORKSTATION, WORKSPACE, EMULATOR
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: AgentForgeViewModel = viewModel()
                var currentScreen by remember { mutableStateOf(NavigationScreen.DASHBOARD) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            NavigationScreen.DASHBOARD -> {
                                MainDashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToWorkstation = {
                                        currentScreen = NavigationScreen.WORKSTATION
                                    }
                                )
                            }
                            NavigationScreen.WORKSTATION -> {
                                WorkstationScreen(
                                    viewModel = viewModel,
                                    onNavigateToWorkspace = {
                                        currentScreen = NavigationScreen.WORKSPACE
                                    },
                                    onNavigateToEmulator = {
                                        currentScreen = NavigationScreen.EMULATOR
                                    },
                                    onBack = {
                                        currentScreen = NavigationScreen.DASHBOARD
                                    }
                                )
                            }
                            NavigationScreen.WORKSPACE -> {
                                WorkspaceScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        currentScreen = NavigationScreen.WORKSTATION
                                    }
                                )
                            }
                            NavigationScreen.EMULATOR -> {
                                EmulatorScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        currentScreen = NavigationScreen.WORKSTATION
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
