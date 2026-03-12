plugins {
    kotlin("jvm") version "2.3.0" apply false
}

allprojects {
    group = "cz.lbenda.reservation"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
