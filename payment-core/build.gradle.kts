plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.fastcampus"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
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
	// spring
	implementation("org.springframework.boot:spring-boot-starter-aop")
//	implementation("androidx.compose.foundation:foundation-android:1.8.1")
//	implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.3")
	implementation ("org.springframework.boot:spring-boot-starter-web")
	implementation(project(":payment-infra"))
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// jwt
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// modules
	implementation(project(":payment-infra"))
	implementation(project(":common"))

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("it.ozimov:embedded-redis:0.7.2")
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}