# JSON Backend
Monitoring pada sistem diimplementasikan menggunakan Spring Boot Actuator, Micrometer, Prometheus, dan Grafana. Backend Spring Boot mengekspos metrics aplikasi melalui endpoint /actuator/prometheus, kemudian Prometheus melakukan scraping metrics secara periodik dan Grafana digunakan untuk visualisasi dashboard monitoring. Monitoring difokuskan pada metric platform yang essential dan tidak bergantung pada module bisnis: application up, uptime, CPU, JVM memory, disk usage, HTTP traffic, HTTP latency, database connection pool, JVM threads, GC, dan class loading.
Link commit implementasi monitoring: https://github.com/advprog-2026-B16-project/B16-JSON-backend/tree/chore/monitoring

Profiling aplikasi dilakukan menggunakan Spring Boot profiling dan metrics dari Actuator untuk menganalisis performa backend secara umum. Proses profiling difokuskan pada pengukuran HTTP response time, penggunaan CPU, dan penggunaan memory JVM. Berdasarkan hasil profiling tersebut, improvement yang dapat dilakukan antara lain optimasi query, penambahan pagination pada endpoint dengan data besar, dan penggunaan asynchronous processing untuk proses yang berat.

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

Dashboard sudah otomatis ter-provision di Grafana pada folder `JSON Backend` dengan nama `JSON Backend Essential Monitoring`.

Dashboard berisi metrik essential non-module:

- Application up: `up{job="json-backend"}`.
- Uptime: `process_uptime_seconds{job="json-backend"}`.
- CPU usage: `system_cpu_usage{job="json-backend"}`.
- JVM heap usage dan JVM memory used/committed.
- Disk usage dan disk free/total.
- Request rate by HTTP status.
- HTTP latency average dan p95.
- Database connection pool: active, idle, pending, dan max connection dari HikariCP.
- JVM threads: live, daemon, dan peak.
- JVM GC rate dan average pause.
- JVM class loading.

Health detail juga tersedia di:

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/health/readiness
http://localhost:8080/actuator/health/liveness
```

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
