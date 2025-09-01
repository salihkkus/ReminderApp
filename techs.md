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

