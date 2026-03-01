plugins {
    base
}

tasks.register<Exec>("dev") {
    workingDir = file("../ui/web")
    commandLine("npm", "run", "dev")
}

tasks.register<Exec>("npmInstall") {
    workingDir = file("../ui/web")
    commandLine("npm", "install")
}

tasks.register<Exec>("npmBuild") {
    workingDir = file("../ui/web")
    commandLine("npm", "run", "build")
}
