# Quester-TR

Türkçe arayüzlü, görselsiz ve hafif Java oyun kataloğu.

## Özellikler
- Oyun adlarıyla arama
- Kategori filtresi
- Oyunları yalnızca metin olarak listeleme
- HTTPS resmî mağaza/geliştirici sayfalarını varsayılan tarayıcıda açma
- Oyun dosyalarını indirmez veya çalıştırmaz; bu sürüm katalog/başlatıcı prototipidir.

## Gereksinimler
- Java 21
- Maven 3.9+

## Geliştirme
```bash
mvn clean test
mvn package
java -jar target/quester-tr.jar
```

## Windows EXE
Windows üzerinde JDK 21 ve WiX Toolset kurulu olmalıdır:
```powershell
mvn clean verify
jpackage --type exe --name Quester-TR --app-version 0.1.0 --input target --main-jar quester-tr.jar --main-class tr.quester.App --dest dist --win-menu --win-shortcut --win-per-user-install
```

GitHub Actions, `main` dalına gönderilen her değişiklikte testleri çalıştırır ve Windows EXE üretir. Çıktı, Actions sekmesindeki `Quester-TR-Windows-EXE` artifact'inde bulunur.

## Oyun kataloğunu genişletme
`src/main/resources/games.csv` dosyasına her satırda şu biçimde oyun eklenir:
`Oyun adı;Kategori;Platform;https://resmî-adres`

Katalogda şu an başlangıç kayıtları bulunur; 13.802 oyun hedefi henüz tamamlanmış değildir. Oyun dosyaları telif ve dağıtım koşulları nedeniyle uygulamaya gömülmez.
