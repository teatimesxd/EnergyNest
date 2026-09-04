package com.example.energynest.backend_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A simple global singleton to manage the logged-in user state 
 * since we are bypassing Supabase Auth.
 */
object UserSession {
    var user: User? by mutableStateOf(null)
    
    val icNumber: String
        get() = user?.icNumber ?: ""

    fun isLoggedIn(): Boolean = user != null

    fun logout() {
        user = null
    }
}