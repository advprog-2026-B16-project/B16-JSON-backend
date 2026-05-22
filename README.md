# JSON Backend
Monitoring pada sistem diimplementasikan menggunakan Spring Boot Actuator, Micrometer, Prometheus, dan Grafana. Backend Spring Boot mengekspos metrics aplikasi melalui endpoint /actuator/prometheus, kemudian Prometheus melakukan scraping metrics secara periodik dan Grafana digunakan untuk visualisasi dashboard monitoring. Monitoring difokuskan pada metric yang relevan terhadap kestabilan backend aplikasi seperti CPU usage, JVM memory usage, HTTP request rate, HTTP response latency, dan database connection pool. Pemilihan metric tersebut dilakukan karena backend memiliki business flow penting seperti payment, wallet transaction, refund, dan order processing yang membutuhkan observability terhadap performa aplikasi dan penggunaan resource. Dashboard Grafana digunakan untuk memantau kondisi aplikasi secara real-time, misalnya untuk melihat peningkatan latency endpoint payment, penggunaan memory saat banyak request berjalan bersamaan, serta memastikan koneksi database tetap tersedia ketika terjadi transaksi paralel. Dengan pendekatan ini, proses debugging, observability, dan monitoring deployment menjadi lebih mudah dilakukan.
Link commit implementasi monitoring: https://github.com/advprog-2026-B16-project/B16-JSON-backend/tree/chore/monitoring

Profiling aplikasi dilakukan menggunakan Spring Boot profiling dan metrics dari Actuator untuk menganalisis performa backend pada business flow utama seperti payment processing, order transition, refund approval, wallet transaction, dan scheduler payment expiration. Proses profiling difokuskan pada pengukuran HTTP response time, penggunaan CPU, penggunaan memory JVM, serta penggunaan database connection. Hasil profiling menunjukkan bahwa endpoint seperti payment dan refund memiliki latency lebih tinggi dibanding endpoint biasa karena melibatkan transaction management, pessimistic locking, dan update beberapa entity sekaligus dalam satu proses transaksi. Selain itu, penggunaan koneksi database meningkat ketika beberapa transaction berjalan secara paralel. Berdasarkan hasil profiling tersebut, terdapat beberapa improvement yang dapat dilakukan seperti penambahan caching pada endpoint catalog, optimasi query dan indexing database, penggunaan message broker seperti RabbitMQ atau Kafka untuk asynchronous processing yang lebih scalable, serta penambahan pagination pada endpoint dengan data besar untuk mengurangi penggunaan memory. Dengan profiling ini, sistem menjadi lebih mudah dianalisis dan siap dikembangkan untuk deployment yang lebih besar.

## Tech Stack

- Java 17
- Spring Boot 3.x
- Gradle
- Spring Data JPA
- PostgreSQL
- Docker (not yet)

---

## Project Structure

src/main/java → Application source code  
src/main/resources → Configuration files  
build.gradle → Dependency & build config

---

## Prerequisites

- Java 17+
- Gradle 8.x (wrapper included)
- PostgreSQL
- Docker (not yet)

---

## Run Locally
### 1. Clone repository
git clone https://github.com/your-username/json-backend.git
cd json-backend

### 2. Create PostgreSQL Database
Login to PostgreSQL and create database:
CREATE DATABASE json_backend;

### 3. Copy env and setup your env
cp .env.example .env

### 4. Run Application
.\gradlew bootRun

Default profile is `local`, so the app will use:
- `JWT_SECRET_KEY`
- `LOCAL_DB_URL`
- `LOCAL_DB_USER`
- `LOCAL_DB_PASSWORD`

For production, set:
- `SPRING_PROFILES_ACTIVE=production`
- `JWT_SECRET_KEY`
- `PROD_DB_URL`
- `PROD_DB_USER`
- `PROD_DB_PASSWORD`

---

## Monitoring

Monitoring menggunakan Spring Boot Actuator, Prometheus, dan Grafana.

### 1. Jalankan backend

Pastikan `.env` sudah berisi konfigurasi database dan JWT, lalu jalankan:

```powershell
.\gradlew bootRun
```

Endpoint metrics tersedia di:

```text
http://localhost:8080/actuator/prometheus
```

### 2. Jalankan Prometheus dan Grafana

Di terminal lain, jalankan:

```powershell
docker compose -f docker-compose.monitoring.yml up -d
```

Prometheus akan scrape backend di `host.docker.internal:8080`.

### 3. Buka dashboard

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001`
- Login Grafana: `admin` / `admin`

Dashboard sudah otomatis ter-provision di Grafana pada folder `JSON Backend` dengan nama `JSON Backend Monitoring`.

Dashboard berisi metrik:

- Health aplikasi: `up`, uptime proses, CPU, JVM memory.
- HTTP API: request rate, error rate, dan average latency per URI/method/status.
- Database: HikariCP connection pool.
- User dan catalog: total user, total product, total unit stok, dan product low stock.
- Order: total order per status.
- Payment: total payment per status.
- Wallet: total wallet, total saldo wallet, total transaksi per type/status, dan total nominal transaksi per type/status.
- Refund: total refund request per status.

### 4. Cek target Prometheus

Buka:

```text
http://localhost:9090/targets
```

Target `json-backend` harus berstatus `UP`. Kalau masih `DOWN`, pastikan backend berjalan di port `8080` dan endpoint `/actuator/prometheus` bisa dibuka dari browser.

### 5. Matikan monitoring

```powershell
docker compose -f docker-compose.monitoring.yml down
```
