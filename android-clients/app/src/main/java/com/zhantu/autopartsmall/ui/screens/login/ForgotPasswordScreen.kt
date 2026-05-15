package com.zhantu.autopartsmall.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhantu.autopartsmall.viewmodel.AuthState
import com.zhantu.autopartsmall.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(0) }
    var countdown by remember { mutableStateOf(0) }

    val sendCodeState by viewModel.sendCodeState.collectAsState()
    val resetState by viewModel.resetState.collectAsState()

    LaunchedEffect(sendCodeState) {
        if (sendCodeState is AuthState.Success) {
            countdown = 60
            repeat(60) {
                kotlinx.coroutines.delay(1000)
                countdown--
            }
        }
    }

    LaunchedEffect(resetState) {
        if (resetState is AuthState.Success) {
            step = 2
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "找回密码",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (step < 2) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("手机号") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (step == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("验证码") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (phone.isBlank()) return@Button
                            viewModel.sendResetCode(phone)
                        },
                        enabled = countdown == 0,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(if (countdown > 0) "${countdown}s" else "获取验证码")
                    }
                }

                when (sendCodeState) {
                    is AuthState.Error -> {
                        Text(
                            text = (sendCodeState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (phone.isBlank() || code.isBlank()) return@Button
                        step = 1
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("下一步", fontSize = 16.sp)
                }
            } else {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("新密码（至少6位）") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认新密码") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                when (resetState) {
                    is AuthState.Error -> {
                        Text(
                            text = (resetState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (newPassword.length < 6) return@Button
                        if (newPassword != confirmPassword) return@Button
                        viewModel.resetPassword(phone, code, newPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = resetState !is AuthState.Loading
                ) {
                    if (resetState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("确认重置", fontSize = 16.sp)
                    }
                }
            }
        } else {
            Text(
                text = "密码重置成功！",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("返回登录", fontSize = 16.sp)
            }
        }

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("返回登录")
        }
    }
}
