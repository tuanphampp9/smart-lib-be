plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.freefair.lombok") version "8.6"
}

group = "com.tuanpham"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("com.turkraft.springfilter:jpa:3.1.7")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("com.cloudinary:cloudinary-http44:1.33.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	// MapStruct
	implementation("org.mapstruct:mapstruct:1.6.2")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")

	// import/export excel
	implementation("org.apache.poi:poi-ooxml:5.2.4")

	//webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	//setup websocket
	implementation("org.springframework.boot:spring-boot-starter-websocket")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
