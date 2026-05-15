package com.zhantu.autopartsmall.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhantu.autopartsmall.data.model.RegisterRequest
import com.zhantu.autopartsmall.data.network.RetrofitClient
import kotlinx.coroutines.launch
import android.graphics.BitmapFactory
import android.util.Base64

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var captchaInput by remember { mutableStateOf("") }
    var captchaImage by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var captchaKey by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadCaptcha() {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getCaptcha()
                if (response.code == 200 && response.data != null) {
                    val imageData = response.data.image
                    val base64Data = imageData.substringAfter("base64,")
                    val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    captchaImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    captchaKey = response.data.captchaKey
                }
            } catch (e: Exception) {
                errorMessage = "加载验证码失败: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCaptcha()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "注册账号",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("手机号") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("确认密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("昵称（选填）") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = captchaInput,
                onValueChange = { captchaInput = it },
                label = { Text("验证码") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (captchaImage != null) {
                    Image(
                        bitmap = captchaImage!!.asImageBitmap(),
                        contentDescription = "验证码",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }

        TextButton(
            onClick = { loadCaptcha() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("换一张")
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (phone.isBlank() || password.isBlank()) {
                    errorMessage = "请输入手机号和密码"
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = "两次输入的密码不一致"
                    return@Button
                }
                if (captchaInput.isBlank()) {
                    errorMessage = "请输入验证码"
                    return@Button
                }
                isLoading = true
                errorMessage = null
                scope.launch {
                    try {
                        val response = RetrofitClient.apiService.register(
                            RegisterRequest(phone, password, nickname.takeIf { it.isNotBlank() }, captchaInput, captchaKey)
                        )
                        if (response.code == 200) {
                            onRegisterSuccess()
                        } else {
                            errorMessage = response.message
                        }
                    } catch (e: Exception) {
                        errorMessage = "注册失败: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("注册", fontSize = 16.sp)
            }
        }

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("已有账号？返回登录")
        }
    }
}
