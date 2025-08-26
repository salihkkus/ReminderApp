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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.BuildConfig

@Composable
fun LoginScreen(
    navController: NavController
) {
    // Sadece kullanıcıdan istenen alanlar
    var vergiNumarasi by remember { mutableStateOf("") }
    var kullaniciAdi by remember { mutableStateOf("") }
    var kullaniciSifre by remember { mutableStateOf("") }
    
    // API için gerekli olan ama kullanıcıdan gizlenen alanlar
    val veritabaniAd = "0123456010"
    val donemYil = "2025"
    val subeAd = "Merkez"
    val apiKullaniciAdi = "BLS-d475b5037621"
    val apiKullaniciSifre = "e9d251eb-8d86-4e83-95d5-7163f141f8d3"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Logo ve başlık alanı
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Logo placeholder (gerçek logoyla değiştirilebilir)
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "B",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Text(
                text = "Bilsoft Ajanda Modülü",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Giriş yaparak hatırlatmalarınızı yönetin",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        // Giriş formu
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Giriş Bilgileri",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = vergiNumarasi,
                    onValueChange = { vergiNumarasi = it },
                    label = { Text("Vergi Numarası") },
                    placeholder = { Text("Örn: 0123456010") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = kullaniciAdi,
                    onValueChange = { kullaniciAdi = it },
                    label = { Text("Kullanıcı Adı") },
                    placeholder = { Text("E-posta adresiniz") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = kullaniciSifre,
                    onValueChange = { kullaniciSifre = it },
                    label = { Text("Şifre") },
                    placeholder = { Text("Şifrenizi girin") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                
                Button(
                    onClick = {
                        // Şu an için doğrudan ana sayfaya yönlendiriyoruz
                        // API entegrasyonu tamamlandığında bu kısmı tekrar aktif edeceğiz
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                        
                        // API çağrısı (şu an için devre dışı)
                        /*
                        if (vergiNumarasi.isNotBlank() && kullaniciAdi.isNotBlank() && kullaniciSifre.isNotBlank()) {
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
                        }
                        */
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true // Artık her zaman aktif
                ) {
                    Text("Giriş Yap")
                }
            }
        }
        
        // Yardım metni
        Text(
            text = "Giriş yapmak için Bilsoft hesap bilgilerinizi kullanın",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        // Development için örnek bilgiler (sadece test amaçlı)
        if (BuildConfig.DEBUG) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Test Bilgileri (Sadece Development)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Vergi: 0123456010",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "E-posta: sskarakussalih77@gmail.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Şifre: 04a7b4c1",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
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
