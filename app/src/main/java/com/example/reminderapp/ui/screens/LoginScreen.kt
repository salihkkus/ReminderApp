package com.example.reminderapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var vergiNumarasi by remember { mutableStateOf("0123456010") }
    var kullaniciAdi by remember { mutableStateOf("sskarakussalih77@gmail.com") }
    var kullaniciSifre by remember { mutableStateOf("04a7b4c1") }
    var rememberMe by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsState()
    
    // Kaydedilmiş bilgileri yükle
    LaunchedEffect(Unit) {
        val savedCredentials = viewModel.getSavedCredentials()
        if (savedCredentials != null && viewModel.getRememberMe()) {
            vergiNumarasi = savedCredentials.vergiNumarasi
            kullaniciAdi = savedCredentials.kullaniciAdi
            kullaniciSifre = savedCredentials.kullaniciSifre
            rememberMe = true
        }
    }
    
    // Giriş başarılı olduğunda navigation
    LaunchedEffect(loginState) {
        if (loginState.isSuccess) {
            try {
                // Başarı mesajını göstermek için kısa bir gecikme
                delay(1500)
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } catch (e: Exception) {
                // Navigation hatası durumunda log
                android.util.Log.e("LoginScreen", "Navigation error", e)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logo ve başlık alanı
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            Image(
                painter = painterResource(id = com.example.reminderapp.R.mipmap.bilsoft),
                contentDescription = "Bilsoft Logo"
            )
            Text(
                text = "Bilsoft Ajanda Modülü",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Giriş yaparak hatırlatmalarınızı yönetin",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Giriş formu
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Giriş Bilgileri",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OutlinedTextField(
                    value = vergiNumarasi,
                    onValueChange = { vergiNumarasi = it },
                    label = { Text("Vergi Numarası") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = { Text("Örnek: 0123456010") }
                )
                
                OutlinedTextField(
                    value = kullaniciAdi,
                    onValueChange = { kullaniciAdi = it },
                    label = { Text("Kullanıcı Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    supportingText = { Text("E-posta adresiniz") }
                )
                
                OutlinedTextField(
                    value = kullaniciSifre,
                    onValueChange = { kullaniciSifre = it },
                    label = { Text("Şifre") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    supportingText = { Text("Şifrenizi girin") }
                )
                
                // Beni Hatırla checkbox'ı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(
                        text = "Beni Hatırla",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Button(
                    onClick = {
                        viewModel.login(
                            vergiNumarasi = vergiNumarasi,
                            kullaniciAdi = kullaniciAdi,
                            kullaniciSifre = kullaniciSifre,
                            rememberMe = rememberMe
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loginState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (loginState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Giriş Yap", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
        
        
        
        // Error display with more details
        loginState.error?.let { errorMsg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "❌ Giriş Hatası",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // API error code'u göster
                    loginState.apiCode?.let { code ->
                        Text(
                            text = "Hata Kodu: $code",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Success message
        if (loginState.isSuccess) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "✅ Giriş Başarılı!",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Ana sayfaya yönlendiriliyorsunuz...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Token bilgisi (debug için)
                    loginState.token?.let { token ->
                        Text(
                            text = "Token: ${token.take(20)}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ReminderappTheme {
        LoginScreen(navController = rememberNavController())
    }
}
