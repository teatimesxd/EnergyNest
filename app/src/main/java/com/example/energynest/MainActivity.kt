package com.example.energynest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.SupabaseClient
import com.example.energynest.ui.theme.EnergyNestTheme
import io.github.jan.supabase.postgrest.from

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EnergyNestTheme {
                SupabaseConnectionScreen()
            }
        }
    }
}

@Composable
fun SupabaseConnectionScreen() {

    var result by remember {
        mutableStateOf("Connecting...")
    }

    LaunchedEffect(Unit) {
        try {
            val response = SupabaseClient.client
                .from("test")
                .select()

            result = """
                SUCCESS!
                
                Test Table Result:
                ${response.data}
            """.trimIndent()

        } catch (e: Throwable) {

            e.printStackTrace()

            result = """
                FAILED!
                
                Error: ${e.message}
                
                Type: ${e::class.simpleName}
            """.trimIndent()
        }
    }

    Text(text = result)
}