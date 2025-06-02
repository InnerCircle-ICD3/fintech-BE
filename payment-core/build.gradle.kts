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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("androidx.compose.foundation:foundation-android:1.8.1")
//	implementation("com.google.firebase:firebase-crashlytics-build tools:3.0.3")
	implementation ("org.springframework.boot:spring-boot-starter-web")
	implementation(project(":payment-infra"))

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// Swagger (SpringDoc OpenAPI 3)
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")


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

	//Redis
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")


	// 공용 모듈
	implementation(project(":common"))
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}