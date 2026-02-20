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
CREATE DATABASE json_db;

### 3. Copy env and setup your env
cp .env.example .env

### 4. Run Application
.\gradlew bootRun

