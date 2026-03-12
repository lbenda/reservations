plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.3.0"
    application
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

application {
    mainClass = "cz.lbenda.reservation.ApplicationKt"
}

val javaSourceSets = extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
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
    implementation(libs.ktorServerCore)
    implementation(libs.ktorServerNetty)
    implementation(libs.ktorServerContentNegotiation)
    implementation(libs.ktorServerStatusPages)
    implementation(libs.ktorSerializationKotlinxJson)
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.flywaydb:flyway-core:10.22.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.22.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jooq:jooq:3.19.8")

    testImplementation(libs.ktorServerTestHost)
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
