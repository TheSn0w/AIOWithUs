import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    id("java")
    `maven-publish`
    kotlin("jvm")
}

group = "net.botwithus.debug"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        setUrl("https://nexus.botwithus.net/repository/maven-snapshots/")
    }
}

configurations {
    create("includeInJar") {
        this.isTransitive = false
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

dependencies {
    implementation("net.botwithus.rs3:botwithus-api:1.0.0-SNAPSHOT")
    implementation("net.botwithus.xapi.public:api:1.0.0-SNAPSHOT")
    implementation("com.google.flogger:flogger:0.7.4")
    "includeInJar"("net.botwithus.xapi.public:api:1.0.0-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    implementation("com.google.code.gson:gson:2.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jsoup:jsoup:1.14.3")
}

val copyJar by tasks.register<Copy>("copyJar") {
    from("build/libs/")
    into("${System.getProperty("user.home")}\\BotWithUs\\scripts\\local\\")
    include("*.jar")
}

tasks.named<Jar>("jar") {
    from({
        configurations["includeInJar"].map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    finalizedBy(copyJar)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

/*
tasks.register("updateVersion") {
    doLast {
        val versionPropsFile = file("C:\\Users\\Sn0w\\OneDrive\\Desktop\\AIO\\version.properties")
        val versionProps = Properties()

        if (versionPropsFile.canRead()) {
            versionProps.load(FileInputStream(versionPropsFile))
        }

        val versionStr = versionProps.getProperty("version", "1.0.0")

        // Split the version string into major, minor, and patch parts
        val versionParts = versionStr.split(".")
        val major = versionParts[0].toInt()
        val minor = versionParts[1].toInt()
        val patch = versionParts[2].toInt()

        // Increment the patch version by 1
        val newPatch = patch + 1

        // Format the new version number
        val newVersionStr = "$major.$minor.$newPatch"

        // Update the version in the properties file
        versionProps.setProperty("version", newVersionStr)
        versionProps.store(FileOutputStream(versionPropsFile), null)

        println("Updated version to: $newVersionStr")
    }
}

tasks.named("build") {
    dependsOn("updateVersion")
}*/
