import com.example.SupabaseClient
import io.github.jan.supabase.postgrest.from
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
