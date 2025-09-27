# 📱 ReminderApp - Ajanda Modülü

Modern Android uygulaması ile hatırlatma ve bildirim yönetimi sistemi.

## 🎯 Proje Hakkında

Bu proje, kullanıcılara önemli görevleri hatırlatmak ve ajanda yönetimi sağlamak için geliştirilmiş bir Android uygulamasıdır. Modern Material Design 3 arayüzü ile kullanıcı dostu bir deneyim sunar.

## 🏗️ Teknoloji Stack

- **Dil**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Network**: Retrofit + OkHttp
- **Navigation**: Navigation Compose
- **Asynchronous Programming**: Coroutines + Flow
- **Date/Time**: ThreeTenABP

## 📱 Özellikler

- ✅ REST API entegrasyonu
- ✅ Kullanıcı girişi ve kimlik doğrulama
- ✅ Hatırlatma oluşturma, düzenleme, silme
- ✅ Öncelik seviyeleri (Düşük, Orta, Yüksek, Acil)
- ✅ Tarih ve saat yönetimi
- ✅ Takvim görünümü ile tarih bazlı gösterim
- ✅ Offline çalışma desteği
- ✅ Modern Material Design 3 arayüzü
- ✅ Bildirim sistemi

## 🚀 Kurulum

### Gereksinimler

- Android Studio Hedgehog veya üzeri
- Android SDK 24+
- JDK 11+

### Adımlar

1. Projeyi klonlayın:
```bash
git clone <repository-url>
cd ReminderApp
```

2. Android Studio'da projeyi açın

3. Gradle sync işlemini bekleyin

4. Uygulamayı çalıştırın

## 📁 Proje Yapısı

```
app/src/main/java/com/example/reminderapp/
├── data/
│   ├── api/           # API servisleri
│   ├── local/         # Room veritabanı
│   ├── model/         # Data modelleri
│   └── repository/    # Repository sınıfları
├── di/                # Hilt dependency injection
├── ui/
│   ├── components/    # UI bileşenleri
│   ├── screens/       # Ekranlar
│   ├── theme/         # Tema ve stiller
│   └── viewmodels/    # ViewModel sınıfları
├── MainActivity.kt    # Ana aktivite
└── ReminderApplication.kt # Application sınıfı
```

## 🎨 UI Bileşenleri

### Ekranlar

1. **LoginScreen**: Kullanıcı girişi
2. **HomeScreen**: Ana hatırlatma listesi ve bildirimler
3. **CalendarScreen**: Takvim görünümü ile tarih bazlı gösterim
4. **AddNotificationScreen**: Yeni bildirim ekleme
5. **UpdateNotificationScreen**: Bildirim düzenleme

### Bileşenler

- **ReminderItem**: Tekil hatırlatma kartı
- **NotificationItem**: Bildirim kartı
- **PriorityChip**: Öncelik göstergesi

## 🔐 Güvenlik

- API token'ları güvenli şekilde saklanır
- Şifre alanları maskelenir
- HTTPS kullanılarak veri transferi yapılır

## 📊 Veri Yönetimi

### Local Database (Room)

- Hatırlatmaların offline saklanması
- Otomatik senkronizasyon
- Veri bütünlüğü korunması

### Remote API

- Gerçek zamanlı veri senkronizasyonu
- Çoklu kullanıcı desteği
- Merkezi veri yönetimi

## 🧪 Test

```bash
# Unit testler
./gradlew test

# Instrumented testler
./gradlew connectedAndroidTest
```

## 📦 Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 🔄 Güncellemeler

### v1.0.0
- ✅ Temel hatırlatma yönetimi
- ✅ API entegrasyonu
- ✅ Modern UI tasarımı
- ✅ Takvim görünümü
- ✅ Offline çalışma desteği

---

**Not**: Bu uygulama modern Android geliştirme pratikleri kullanılarak geliştirilmiştir.