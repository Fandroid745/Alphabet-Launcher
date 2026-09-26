package com.example.alphabetlauncher


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.alphabetlauncher.ui.HomeViewModel
import com.example.alphabetlauncher.ui.MainHomeScreen
import com.example.alphabetlauncher.ui.theme.AlphabetLauncherTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AlphabetLauncherTheme {
                val viewModel: HomeViewModel = koinViewModel()

                MainHomeScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}