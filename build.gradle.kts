import org.gradle.api.plugins.quality.Pmd

plugins {
    java
    jacoco
    id("pmd")
    id("org.sonarqube") version "6.0.1.5171"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.7"
}

sonar {
    properties {
        property("sonar.projectKey", "B-Naufal-Zafran-Fadil-2406402542_Modul-2-CI-CD-DevOps")
        property("sonar.organization", "b-naufal-zafran-fadil-2406402542")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "json-backend"

val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
    implementation("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:20240325.1")
}

tasks.register<Test>("unitTest"){
    description = "Runs unit tests."
    group = "verification"

    filter {
        excludeTestsMatching("*FunctionalTest")
    }

}

tasks.register<Test>("functionalTest"){
    description = "Runs functional tests."
    group = "verification"

    filter {
        includeTestsMatching("*FunctionalTest")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

pmd {
    toolVersion = "6.55.0"
    ruleSets = listOf("category/java/bestpractices")
}
