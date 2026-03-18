package com.example.dentalplus_frontend.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.ui.theme.DentalPlus_FrontendTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun LoginScreen(modifier: Modifier)
{
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(modifier = modifier.fillMaxSize(), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {

        Image(
            painter = painterResource(R.drawable.generic_header_wave),
            contentDescription = null,
        )
        Surface(modifier = modifier.padding(horizontal = 20.dp), shadowElevation = 2.dp, shape = MaterialTheme.shapes.medium) {
            Column(
                modifier.padding(20.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterHorizontally,
            ) {
                Text("Inicio de sesión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = username, onValueChange = { username = it }, label = {

                    Text("Usuario")
                }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("¿Olvidaste tu contraseña?")
                }
                Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                    Text("Iniciar sesión")
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.login_footer_wave_with_stucom_logo),
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview()
{
    DentalPlus_FrontendTheme {
        LoginScreen(modifier = Modifier)
    }
}