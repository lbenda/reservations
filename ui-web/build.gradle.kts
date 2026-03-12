plugins {
    base
}

fun npmCommand() = if (org.gradle.internal.os.OperatingSystem.current().isWindows) "npm.cmd" else "npm"

tasks.register<Exec>("dev") {
    workingDir = file("../ui/web")
    commandLine(npmCommand(), "run", "dev")
}

tasks.register<Exec>("npmInstall") {
    workingDir = file("../ui/web")
    commandLine(npmCommand(), "install")
}

tasks.register<Exec>("npmBuild") {
    workingDir = file("../ui/web")
    commandLine(npmCommand(), "run", "build")
}
