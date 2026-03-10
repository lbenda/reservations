plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.10"
    id("io.ktor.plugin") version "3.1.1"
}

application {
    mainClass.set("cz.lbenda.reservation.ApplicationKt")
}

val javaSourceSets = extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer

kotlin {
    sourceSets {
        named("main") {
            kotlin.srcDir("build/generated-src/jooq")
        }
    }
}

val codegenSourceSet = javaSourceSets.maybeCreate("codegen").apply {
    java.srcDir("src/codegen/java")
    resources.srcDir("src/main/resources")
}

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.flywaydb:flyway-core:10.22.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.22.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jooq:jooq:3.19.8")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.jooq:jooq-meta:3.19.8")
    testImplementation("org.jooq:jooq-codegen:3.19.8")
    "codegenImplementation"("org.flywaydb:flyway-core:10.22.0")
    "codegenImplementation"("org.flywaydb:flyway-database-postgresql:10.22.0")
    "codegenImplementation"("org.postgresql:postgresql:42.7.4")
    "codegenImplementation"("org.jooq:jooq-meta:3.19.8")
    "codegenImplementation"("org.jooq:jooq-codegen:3.19.8")
    "codegenImplementation"("org.testcontainers:postgresql:1.20.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("generateJooq") {
    group = "codegen"
    description = "Generate jOOQ Kotlin classes from the Flyway-managed schema."
    classpath = codegenSourceSet.runtimeClasspath
    mainClass.set("cz.lbenda.reservation.db.JooqCodegenRunner")
}
