
plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    //id("xyz.jpenilla.run-paper") version "2.3.1"
    //id("com.gradleup.shadow") version "9.2.2"
}

group = "crux.project"
version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
tasks{
}

repositories {
    mavenCentral()
    maven("https://nexus.phoenixdevt.fr/repository/maven-public")
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    compileOnly("io.lumine","MythicLib-dist", "1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce", "MMOItems-API", "6.9.4-SNAPSHOT")



    compileOnly(files(
        "E:\\Plugins\\FIVERR\\newsloth\\newsloth_utility\\build\\libs\\Utility-1.0.jar",
        "E:\\Plugins\\FIVERR\\newsloth\\PlayerBlocks\\build\\libs\\PlayerBlocks-.jar",
    ))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))

    }
}













