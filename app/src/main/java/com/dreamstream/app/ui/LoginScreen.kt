package com.dreamstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedServer by viewModel.savedServer.collectAsState()
    val savedUsername by viewModel.savedUsername.collectAsState()
    val savedPassword by viewModel.savedPassword.collectAsState()

    var server by remember { mutableStateOf(savedServer) }
    var username by remember { mutableStateOf(savedUsername) }
    var password by remember { mutableStateOf(savedPassword) }

    LaunchedEffect(uiState) {
        if (uiState is UiState.LoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050508)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DreamStream Ultra",
                color = Color(0xFF00E5FF),
                fontSize = 28.sp
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = server,
                onValueChange = { server = it },
                label = { Text("Server Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedColors()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedColors()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedColors()
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(server, username, password) },
                enabled = uiState !is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("LOGIN", color = Color.Black)
                }
            }

            if (uiState is UiState.Error) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = (uiState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00E5FF),
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = Color(0xFF00E5FF),
    unfocusedLabelColor = Color.Gray,
    cursorColor = Color(0xFF00E5FF),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
