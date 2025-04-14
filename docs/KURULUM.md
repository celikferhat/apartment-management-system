# Apartman Yönetim Sistemi - Kurulum ve Kullanım Kılavuzu

## Sistem Gereksinimleri

- Docker ve Docker Compose
- JDK 11 veya üzeri (geliştirme için)
- Maven (geliştirme için)
- Minimum 2GB RAM
- 10GB disk alanı

## Kurulum Adımları

### 1. Projeyi İndirme

```bash
git clone https://github.com/your-username/apartment-management-system.git
cd apartment-management-system
```

### 2. Projeyi Derleme (İsteğe Bağlı)

Eğer kaynak kodundan derlemek isterseniz:

```bash
mvn clean package -DskipTests
```

### 3. Docker ile Çalıştırma

```bash
docker-compose up -d
```

Bu komut, hem uygulamayı hem de MySQL veritabanını içeren konteynerları başlatacaktır.

### 4. Sisteme Erişim

Tarayıcınızdan http://localhost:8080 adresine giderek sisteme erişebilirsiniz.

### 5. İlk Kullanıcı Oluşturma

1. Tarayıcıdan http://localhost:8080/register adresine gidin
2. Kayıt formunu doldurun ve "Kayıt Ol" butonuna tıklayın
3. İlk kullanıcı varsayılan olarak "ROLE_RESIDENT" (daire sakini) rolüne sahip olacaktır
4. Yönetici rolü vermek için veritabanında aşağıdaki SQL sorgusunu çalıştırın:

```sql
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
```

## Kullanım Kılavuzu

### Yönetici Kullanımı

#### Daire Yönetimi

1. Ana menüden "Daireler" seçeneğine tıklayın
2. Daire listesini görüntüleyin
3. "Yeni Daire Ekle" butonuna tıklayarak yeni daire ekleyin
4. Mevcut daireleri düzenlemek için "Düzenle" butonunu kullanın
5. Daire sakinlerini atamak veya çıkarmak için ilgili butonları kullanın

#### Aidat Yönetimi

1. Ana menüden "Aidatlar" seçeneğine tıklayın
2. Aidat listesini görüntüleyin
3. "Yeni Aidat Ekle" butonuna tıklayarak tek bir aidat ekleyin
4. "Aylık Aidat Oluştur" butonuna tıklayarak tüm daireler için toplu aidat oluşturun
5. Ödeme almak için "Ödendi İşaretle" butonunu kullanın
6. WhatsApp üzerinden bildirim göndermek için WhatsApp ikonuna tıklayın

#### Gelir/Gider Yönetimi

1. Ana menüden "Gelir/Gider" seçeneğine tıklayın
2. Tüm finansal işlemleri görüntüleyin
3. "Yeni Gelir Ekle" veya "Yeni Gider Ekle" butonlarını kullanarak işlem ekleyin
4. Mevcut işlemleri düzenlemek veya silmek için ilgili butonları kullanın

#### Raporlar

1. Ana menüden "Raporlar" seçeneğine tıklayın
2. Finansal raporları ve istatistikleri görüntüleyin
3. Tarih aralığı seçerek özel raporlar oluşturun

### Daire Sakini Kullanımı

#### Aidat Görüntüleme

1. Ana menüden "Aidatlar" seçeneğine tıklayın
2. Kendi dairenize ait aidat listesini görüntüleyin
3. Ödeme durumlarını kontrol edin

#### Apartman Finansal Durumu

1. Ana menüden "Gelir/Gider" seçeneğine tıklayın
2. Apartmanın tüm finansal işlemlerini görüntüleyin
3. Ana menüden "Raporlar" seçeneğine tıklayarak detaylı finansal raporları görüntüleyin

## Sorun Giderme

### Veritabanı Bağlantı Hatası

Eğer uygulama veritabanına bağlanamazsa:

```bash
docker-compose down
docker-compose up -d
```

### Konteyner Loglarını Görüntüleme

```bash
docker-compose logs -f app
docker-compose logs -f mysql
```

### Veritabanını Sıfırlama

```bash
docker-compose down -v
docker-compose up -d
```

## Güvenlik Notları

- Varsayılan kullanıcı adı ve şifreleri üretim ortamında değiştirin
- MySQL root şifresini docker-compose.yml dosyasında güncelleyin
- JWT secret anahtarını application.properties dosyasında güncelleyin

## Yedekleme

Veritabanı yedeklemesi için:

```bash
docker exec apartment-management-mysql mysqldump -u root -proot apartmentdb > backup.sql
```

## İletişim ve Destek

Sorunlar ve öneriler için: support@example.com
