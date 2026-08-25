package com.example.energynest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.energynest.ui.theme.EnergyNestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyNestTheme {
                //SmartSell() //call SmartSell function
                //LoginPage() //call LoginPage function
                RegisterPage()
            }
        }
    }
}

