package com.example.energynest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energynest.ui.theme.EnergyNestTheme

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyNestTheme {
                login_page()
            }
        }
    }
}

@Composable
fun login_page(modifier: Modifier = Modifier){
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var test by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
            .padding(20.dp)
            .background(Color.White,
                shape = RoundedCornerShape(20.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.energynest_icon),
            contentDescription = "App logo",
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = "EnergyNest",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Your Smart Portal to Clean & Affordable\n Energy in Malaysia",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )


        Text(
            text = "Email Address / TNB Account Number",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF505F76),
            modifier = modifier.align(Alignment.Start)
        )

        InputRow(
            label = "Enter your email or TNB #",
            value = account,
            onValueChange = {newAccount ->
                account = newAccount
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Text(
            text = "Password",
            color = Color(0xFF505F76),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        InputRow(
            label = "Enter your password",
            value = password,
            onValueChange = {newPassword ->
                password = newPassword
            },
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                test = "Login successful"
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(16, 185, 129),
                contentColor = Color.White
            ),

            modifier = Modifier.fillMaxWidth(0.9f).height(60.dp)
        ) {
            Text("Login →",
                fontSize = 30.sp)

        }
        if (test.isNotEmpty()){
            Text(
                text = test,
                color = Color.Green,
                fontSize = 26.sp
            )
        }
    }
}

@Composable
fun InputRow(
    label: String,
    value: String,
    onValueChange:(String) -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text(label)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // let user input string
        singleLine = true,
        shape = shape,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview(){
    EnergyNestTheme {
        login_page()
    }
}