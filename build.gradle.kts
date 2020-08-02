import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springBootVersion = "2.3.2.RELEASE"

plugins {
    id("org.springframework.boot") version "2.3.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("kapt") version "1.3.72"
}

group = "com.wink"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate:hibernate-validator:6.0.16.Final")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation("net.dv8tion:JDA:4.1.1_165")
    implementation("com.jagrosh:jda-utilities:3.0.4")
    runtimeOnly(group = "org.mariadb.jdbc", name = "mariadb-java-client", version = "2.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
