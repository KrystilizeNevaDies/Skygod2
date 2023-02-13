plugins {
    id("java")
}

group = "org.krystilize"
version = "1.0-INDEV"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:-SNAPSHOT")

    // JNoise
    implementation("com.github.Articdive:JNoise:3.0.2")

    // TOML
    implementation("com.moandjiezana.toml:toml4j:0.7.2")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
}

// Add the "generated" src folder to the classpath
java.sourceSets["main"].java {
    srcDir("src/generated/java")
}

tasks.withType<Test> {
    useJUnitPlatform()
}