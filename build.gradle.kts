plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

group = "net.azisaba"
version = "2.0.6+1.21.11"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "azisaba-repo"
        url = uri("https://repo.azisaba.net/repository/maven-public/")
    }
    maven {
        name = "Lumine Releases"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.12.0")
    compileOnly("xyz.acrylicstyle:StorageBox:1.5.6")
    compileOnly("net.azisaba:ItemStash:2.2.3")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}