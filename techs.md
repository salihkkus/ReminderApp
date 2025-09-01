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

