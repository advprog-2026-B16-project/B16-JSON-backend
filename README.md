# JSON Backend

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
- `LOCAL_DB_URL`
- `LOCAL_DB_USER`
- `LOCAL_DB_PASSWORD`

For production, set:
- `SPRING_PROFILES_ACTIVE=production`
- `PROD_DB_URL`
- `PROD_DB_USER`
- `PROD_DB_PASSWORD`

