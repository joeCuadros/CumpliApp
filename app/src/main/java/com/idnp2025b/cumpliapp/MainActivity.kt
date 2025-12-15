package com.idnp2025b.cumpliapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.idnp2025b.cumpliapp.ui.navigation.Navigation
import com.idnp2025b.cumpliapp.ui.theme.CumpliAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CumpliAppTheme {
                Surface {
                    Navigation()
                }
            }
        }
    }
}