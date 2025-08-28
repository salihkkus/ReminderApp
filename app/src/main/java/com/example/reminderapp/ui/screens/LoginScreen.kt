package com.example.reminderapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var veritabaniAd by remember { mutableStateOf("0123456010") }
    var donemYil by remember { mutableStateOf("2025") }
    var subeAd by remember { mutableStateOf("Merkez") }
    var apiKullaniciAdi by remember { mutableStateOf("BLS-d475b5037621") }
    var apiKullaniciSifre by remember { mutableStateOf("e9d251eb-8d86-4e83-95d5-7163f141f8d3") }
    
    val loginState by viewModel.loginState.collectAsState()
    
    // Giriş başarılı olduğunda navigation
    LaunchedEffect(loginState) {
        if (loginState.isSuccess) {
            try {
                // Başarı mesajını göstermek için kısa bir gecikme oluşturduk
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
        Text(
            text = "Bilsoft Ajanda Modülü",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "Giriş yaparak hatırlatmalarınızı yönetin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        OutlinedTextField(
            value = vergiNumarasi,
            onValueChange = { vergiNumarasi = it },
            label = { Text("Vergi Numarası") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        
        OutlinedTextField(
            value = kullaniciAdi,
            onValueChange = { kullaniciAdi = it },
            label = { Text("Kullanıcı Adı") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        
        OutlinedTextField(
            value = kullaniciSifre,
            onValueChange = { kullaniciSifre = it },
            label = { Text("Kullanıcı Şifre") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        
        OutlinedTextField(
            value = veritabaniAd,
            onValueChange = { veritabaniAd = it },
            label = { Text("Veritabanı Adı") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = donemYil,
            onValueChange = { donemYil = it },
            label = { Text("Dönem Yılı") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        
        OutlinedTextField(
            value = subeAd,
            onValueChange = { subeAd = it },
            label = { Text("Şube Adı") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = apiKullaniciAdi,
            onValueChange = { apiKullaniciAdi = it },
            label = { Text("API Kullanıcı Adı") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = apiKullaniciSifre,
            onValueChange = { apiKullaniciSifre = it },
            label = { Text("API Kullanıcı Şifre") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        
        Button(
            onClick = {
                viewModel.login(
                    vergiNumarasi = vergiNumarasi,
                    kullaniciAdi = kullaniciAdi,
                    kullaniciSifre = kullaniciSifre,
                    veritabaniAd = veritabaniAd,
                    donemYil = donemYil,
                    subeAd = subeAd,
                    apiKullaniciAdi = apiKullaniciAdi,
                    apiKullaniciSifre = apiKullaniciSifre
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loginState.isLoading
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Giriş Yap")
            }
        }
        
        // Test login butonu (development için)
        OutlinedButton(
            onClick = { viewModel.testLogin() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loginState.isLoading
        ) {
            Text("🧪 Test Giriş (API olmadan)")
        }
        
        // Debug bilgileri (sadece development'ta göster)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🔧 Debug Bilgileri",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "API Endpoint: https://apiv3.bilsoft.com/api/Auth/GirisYap",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Vergi No: $vergiNumarasi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Kullanıcı: $kullaniciAdi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "API Kullanıcı: $apiKullaniciAdi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
