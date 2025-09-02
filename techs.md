# ReminderApp
Proje boyunca öğrendiğim ve kullanmaya çalıştığım teknolojiler, mimariler, kütüphaneler vs.

___________________________________________________________________________________________________________________________________________________________________________________________

# REST API

## Nedir?
REST (Representational State Transfer) API, istemci (client) ile sunucu (server) arasındaki iletişimi sağlayan bir web servisidir. HTTP protokolünü kullanır. Modern yazılım geliştirmede en yaygın kullanılan servis iletişim yöntemlerinden biridir.  

## Ne İşe Yarar?
- Farklı uygulamalar arasında veri alışverişini sağlar.  
- Örneğin: Mobil uygulama → Sunucudan kullanıcı bilgilerini almak için REST API kullanır.  
- Platform bağımsızdır: Android, iOS, web uygulamaları aynı API üzerinden iletişim kurabilir.  

## Temel Özellikleri
- **HTTP metodları** ile çalışır:  
  - `GET` → Veri getirir  (Read)
  - `POST` → Yeni veri ekler  (Create)
  - `PUT` → Var olan veriyi günceller  (Update)
  - `DELETE` → Veriyi siler  (Delete)

- **JSON** en yaygın kullanılan veri formatıdır (XML, plain text de olabilir).  

- **Stateless (durumsuz)**: Her istek bağımsızdır, sunucu istemcinin önceki isteklerini hatırlamaz.  

- **Endpoint mantığı**:  
  - `/users` → Tüm kullanıcıları getirir  
  - `/users/5` → ID’si 5 olan kullanıcıyı getirir  

## HTTP Status Kodları
REST API, her isteğe bir **HTTP status code** ile yanıt verir. Bu kodlar isteğin sonucunu belirtir:  

- **200 OK** → İstek başarılı  
- **201 Created** → Yeni kaynak oluşturuldu  
- **204 No Content** → İşlem başarılı, fakat içerik yok  
- **400 Bad Request** → Hatalı istek  
- **401 Unauthorized** → Yetkisiz erişim  
- **403 Forbidden** → Yetki yok  
- **404 Not Found** → Kaynak bulunamadı  
- **500 Internal Server Error** → Sunucu hatası  

## Avantajları
- Basit ve anlaşılırdır.  
- Hafiftir ve hızlıdır.  
- Yaygın desteklenir, öğrenmesi ve kullanması kolaydır.  
- Platformdan bağımsızdır, her dil ve ortamda kullanılabilir.  

________________________________________________________________________________________________________________________________________________________________________________________


# MVVM (Model-View-ViewModel)

## Nedir?
MVVM, yazılım geliştirmede kullanılan bir **mimari tasarım deseni**dir.  
Amaç, uygulamanın farklı katmanlarını birbirinden ayırarak **daha düzenli, okunabilir ve sürdürülebilir** bir kod yapısı oluşturmaktır.  
Özellikle Android uygulama geliştirmede yaygın olarak kullanılır.  

## Katmanlar

- **Model**  
  - Uygulamanın veri katmanıdır.  
  - Veri sınıfları (örneğin: `User`, `Product`) ve veri kaynaklarını temsil eder.  
  - API, veritabanı veya cache’den gelen bilgileri içerir.  
  - İş mantığı burada değil, sadece veriyi temsil eder.  

- **View**  
  - Kullanıcıya gösterilen arayüzdür (Activity, Fragment, XML layout dosyaları).  
  - Kullanıcıdan input alır ve çıktıları ekranda gösterir.  
  - Kendi içinde iş mantığı barındırmaz, sadece görüntüleme işini yapar.  

- **ViewModel**  
  - Model ve View arasındaki köprü görevi görür.  
  - Veriyi View’e hazırlar, iş mantığını burada uygular.  
  - Lifecycle-aware (yaşam döngüsünden haberdar) olduğu için ekran döndüğünde (rotate) veriler kaybolmaz.  
  - View’den bağımsızdır → test edilebilirliği kolaydır.  

- **Repository (Ekstra Katman)**  
  - Genellikle MVVM projelerinde kullanılan ek bir katmandır.  
  - Veri kaynağıyla ilgili tüm işleri üstlenir (API, veritabanı, cache).  
  - ViewModel, Repository’den veri ister ama Repository’nin nasıl çalıştığını bilmez.  
  - Böylece bağımlılıklar azalır.  

## Özellikleri
- **Katmanlar arası bağımsızlık** → Model, View, ViewModel birbirinden ayrı çalışır.  
- **Data Binding** desteği vardır → ViewModel’deki değişiklikler otomatik olarak UI’ya yansır.  
- **Lifecycle yönetimi** → ViewModel, ekran döndürme gibi durumlarda verileri korur.  
- **Test edilebilirlik** → İş mantığı View’den ayrıldığı için test yazmak kolaydır.  
- **Temiz kod** → Kodlar daha düzenli, okunabilir ve sürdürülebilir olur.  

## Örnek Akış
1. Kullanıcı uygulamada "Kullanıcıları Listele" butonuna tıklar (**View**).  
2. ViewModel, bu isteği Repository’ye iletir (**ViewModel**).  
3. Repository, API’den kullanıcı listesini alır (**Model/Repository**).  
4. Gelen liste ViewModel’e döner, ViewModel bunu işler.  
5. ViewModel veriyi View’e gönderir ve arayüz otomatik güncellenir (**Data Binding**).  

## Avantajları
- **Temiz mimari** sağlar, kodun okunabilirliği artar.  
- **Veri kaybolmaz** → Ekran rotate edilse bile ViewModel veriyi korur.  
- **Kolay test edilebilir** → ViewModel ayrı olduğu için iş mantığı bağımsız test edilebilir.  
- **Geliştirici verimliliği** → UI ile iş mantığı ayrıldığı için takım çalışması kolaylaşır.  
- **Memory leak riski azalır** → Lifecycle-aware yapısı sayesinde gereksiz nesneler bellekte tutulmaz.  

## MVVM vs MVC  
- **MVC**: Controller, hem iş mantığı hem de UI ile ilgilenir → zamanla karmaşık hale gelir.  
- **MVVM**: View sadece arayüzle ilgilenir, ViewModel iş mantığını üstlenir → daha düzenli ve sürdürülebilir yapı sağlar.  

________________________________________________________________________________________________________________________________________________________________________________________


# Hilt

## Nedir?
Hilt, Android’de **Dependency Injection (Bağımlılık Enjeksiyonu)** işlemini kolaylaştıran bir kütüphanedir.  
Google tarafından geliştirilmiş olup Dagger üzerine inşa edilmiştir.  

## Ne İşe Yarar?
- Nesnelerin (örneğin `Repository`, `Retrofit`, `Room`) yönetimini otomatik yapar.  
- Kod tekrarını azaltır, modülerlik sağlar.  
- Test yazmayı kolaylaştırır.  
- Büyük projelerde bağımlılıkların yönetimini basitleştirir.  

## Temel Özellikleri
- **@Inject** → Bir sınıfın bağımlılıklarını Hilt’in sağlamasını sağlar.  
- **@Module & @Provides** → Hilt’e hangi nesneyi nasıl oluşturacağını söyler.  
- **@Singleton** → Nesnenin tek bir örneğini (instance) oluşturur.  
- **@HiltViewModel** → ViewModel bağımlılıklarını otomatik olarak enjekte eder.  
- **@AndroidEntryPoint** → Hilt’in dependency injection yapacağı sınıfları işaretler (Activity, Fragment).  

## MVVM ve REST API ile İlişkisi
- **MVVM**: ViewModel içinde Repository veya UseCase bağımlılıklarını Hilt otomatik olarak sağlar.  
- **REST API**: Retrofit, OkHttp gibi ağ nesneleri Hilt modüllerinde tanımlanır ve kolayca enjekte edilir.  

## Avantajları
- Kod tekrarını azaltır.  
- Daha temiz ve okunabilir mimari sağlar.  
- Test edilebilirliği artırır (Mock nesneler kolay eklenir).  
- MVVM ve REST API yapısıyla uyumlu çalışır.  

_________________________________________________________________________________________________________________________________________________________________________________________

# Retrofit

## Nedir?
Retrofit, **Android** için geliştirilmiş bir HTTP istemci kütüphanesidir.  
REST API’lerle haberleşmeyi kolaylaştırır.  
Normalde ağ (network) işlemleri karmaşıktır, Retrofit bu süreci basitleştirir.

## Ne İşe Yarar?
- API isteklerini kolayca yapmanı sağlar.  
- JSON verilerini otomatik olarak Java/Kotlin objelerine dönüştürür.  
- Ağ işlemlerinde hata yönetimi ve asenkron (eşzamansız) çalışma desteği sunar.  
- Okunabilir ve sürdürülebilir kod yazmayı sağlar.

## Özellikleri
- **Kolay kullanım** → Basit arayüzler üzerinden API istekleri yapılır.  
- **JSON Parsing** → `Gson`, `Moshi` gibi kütüphanelerle JSON verileri otomatik parse edilir.  
- **Asenkron Çalışma** → Callback veya Coroutines ile çalışır.  
- **Dinamik URL desteği** → URL parametreleri, header bilgileri kolayca eklenebilir.  
- **RxJava & Coroutines desteği** → Modern asenkron programlamayla uyumludur.

## Avantajları
- Kod tekrarını azaltır.  
- Ağ işlemlerini düzenli ve okunabilir hale getirir.  
- Test edilebilirliği artırır.  
- Hata yönetimini kolaylaştırır.
 
__________________________________________________________________________________________________________________________________________________________________________________________

# Jetpack Compose

## Nedir?
- Jetpack Compose, Android’in modern **UI toolkit**’idir.  
- XML yazmadan, tamamen **Kotlin kodu** ile arayüz oluşturmayı sağlar.  
- Deklaratif bir yaklaşım kullanır: *"Ne görmek istiyorum?"* diye tanımlarsın, Compose gerisini halleder.  
- Daha az kod → Daha okunabilir ve bakımı kolay.

---

## Temel Özellikler
- **Deklaratif Yapı** → UI güncellemelerini otomatik yapar. State değişirse ekran kendini yeniler.  
- **Daha Az Kod** → XML ve Adapter karmaşasına gerek kalmaz.  
- **Kotlin ile Güçlü Entegrasyon** → Coroutine, Flow gibi modern Kotlin özellikleriyle uyumlu.  
- **Material Design Desteği** → Buton, Card, TextField gibi hazır UI bileşenleri içerir.  
- **Hızlı Önizleme (Preview)** → Android Studio’da anında tasarımı görebilirsin.  

---

## Avantajlar
- XML + Adapter yerine tek noktadan, sade bir şekilde UI geliştirme.  
- State yönetimi kolaydır (örneğin `mutableStateOf` ile).  
- Daha az boilerplate kod → daha hızlı geliştirme.  
- Test edilebilirliği artırır.  
- Google, gelecekte Android UI geliştirme için Compose’u merkez yapıyor → uzun vadeli yatırım.  

---

## Kullanım Senaryosu
- Yeni Android projelerinde **varsayılan UI çözümü** olarak kullanılır.  
- MVVM + Compose çok iyi çalışır → ViewModel’deki veriyi direkt UI’ye yansıtabilirsin.  
- Hızlı prototipleme için uygundur.  

________________________________________________________________________________________________________________________________________________________________________________________

# Material Design 3 (MD3)

## Nedir?
- Google’ın en güncel tasarım dili.  
- **Material You** konsepti ile birlikte geldi.  
- Kullanıcı cihazından tema renkleri alabilir → **kişiselleştirilmiş tasarım**.  

## Avantajları
- Modern ve uyumlu görünüm.  
- Daha yuvarlak köşeler, dinamik renkler.  
- Android 12+ cihazlarda sistem renklerini otomatik alabilir.  
- Compose ve XML ile uyumlu.  

## Kullanım Alanı
- Butonlar, kartlar, input alanları gibi UI bileşenlerini güncel tasarımla oluşturmak.  
- Kullanıcıya cihazı ile bütünleşmiş bir deneyim sunmak.  

__________________________________________________________________________________________________________________________________________________________________________________________

# StateFlow

## Nedir?
- StateFlow, Kotlin Coroutines kütüphanesinde bulunan, **her zaman bir değer tutan hot Flow** türüdür.  
- ViewModel → UI arasında güncel durumu (state) paylaşmak için kullanılır.  
- Yeni kolektörler (observer) her zaman en son değeri hemen alır.  

---

## Temel Özellikler
- **Hot Flow** → Kolektör olmasa bile değer saklar.  
- **Her zaman bir başlangıç değeri gerekir.**  
- **Thread-safe** → `value` veya `update {}` ile güvenli güncelleme yapılır.  
- **UI ile uyumlu** → Compose’da `collectAsState()`, Activity/Fragment’ta `lifecycleScope` ile kolayca kullanılabilir.  

---

## Avantajlar
- ViewModel’deki veriyi tek kaynak (**single source of truth**) olarak yönetir.  
- State değişimlerini otomatik olarak UI’ye yansıtır.  
- Test edilebilir ve coroutine tabanlı çalışır.  

---

## Kullanım Senaryosu
- **UI state yönetimi** (örneğin: yükleniyor, hata, veri listesi).  
- MVVM + Compose projelerinde ViewModel’den gelen veriyi UI’ye aktarmak.  
- Cold Flow’ları `stateIn` ile StateFlow’a çevirerek en son değeri saklamak.  

__________________________________________________________________________________________________________________________________________________________________________________________

# Coroutines

## Nedir?
- **Coroutines**, Kotlin'de **asenkron** (aynı anda birden fazla iş) ve **eşzamanlı** programlamayı kolaylaştıran yapıdır.  
- Normalde asenkron işlemler (ör. ağ istekleri, veritabanı işlemleri) çok karmaşık olabilir, ancak Coroutines bunları **basit fonksiyonlar gibi** yazmamızı sağlar.  
- Hafiftir: Binlerce coroutine aynı anda çalışabilir, sistemin performansını zorlamaz.

---

## Neden Kullanılır?
- **Arka planda işlem yapma**: Ağ çağrıları, veritabanı sorguları gibi zaman alan işler ana thread’i (UI) bloke etmeden çalışır.  
- **Kolay okunur kod**: Callback cehennemi yerine düz, sıralı kod gibi görünür.  
- **Hafiflik**: Thread’lerden çok daha az kaynak tüketir.  

---

## Temel Kavramlar
- **Suspend Fonksiyon**: Bekleme yapabilen fonksiyon. Örn: `suspend fun getData()`.  
- **Scope (CoroutineScope)**: Coroutine’lerin hangi yaşam döngüsünde çalışacağını belirler.  
  - `GlobalScope` → Uygulama süresince yaşar.  
  - `viewModelScope` → ViewModel ile yaşar.  
  - `lifecycleScope` → Activity/Fragment ile yaşar.  
- **Dispatcher**: Coroutine’in hangi thread’de çalışacağını belirler.  
  - `Dispatchers.Main` → UI işlemleri  
  - `Dispatchers.IO` → Ağ / Veritabanı işlemleri  
  - `Dispatchers.Default` → Yoğun CPU işlemleri  

---

## Özet
- Coroutines, Kotlin’in **modern asenkron çözümüdür**.  
- **Okunabilir**, **hafif** ve **verimli** kod yazmayı sağlar.  
- UI’yi dondurmadan arka planda işlem yapmaya imkan tanır.  

___________________________________________________________________________________________________________________________________________________________________________________________

