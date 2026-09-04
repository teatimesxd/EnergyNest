package com.example.energynest.backend_models

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://skanmdzsnfoquwljukfk.supabase.co",
        supabaseKey = "sb_publishable_LTLKeWepLBaIi8RW3Fd23w_OVLDbLqZ"
    ) {
        install(Auth) {
            host = "reset-password"
            scheme = "energynest"
        }
        install(Postgrest)
    }
}