package com.zhantu.autopartsmall.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhantu.autopartsmall.viewmodel.AuthState
import com.zhantu.autopartsmall.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onSwitchToAdminLogin: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthState.Success -> {
                val successData = (loginState as AuthState.Success).data
                val isAdmin = when (successData) {
                    is com.zhantu.autopartsmall.data.model.LoginResponse -> {
                        successData.user?.role == "ADMIN"
                    }
                    else -> false
                }
                onLoginSuccess(isAdmin)
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "战途汽配商城",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("手机号") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        when (loginState) {
            is AuthState.Error -> {
                Text(
                    text = (loginState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (phone.isBlank() || password.isBlank()) {
                    return@Button
                }
                viewModel.login(phone, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = loginState !is AuthState.Loading
        ) {
            if (loginState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("登录", fontSize = 16.sp)
            }
        }

        TextButton(
            onClick = onSwitchToAdminLogin,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("管理员登录")
        }

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("还没有账号？立即注册")
        }

        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("忘记密码？")
        }
    }
}
