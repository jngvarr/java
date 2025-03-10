plugins {
    id("java")
}

group = "ru.gb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("com.google.guava:guava:32.1.3-jre")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}