import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

val javaVersion = JavaVersion.VERSION_1_8
val javaVersionNumber = javaVersion.name.substringAfter('_').replace('_', '.')
val javaVersionMajor = javaVersion.name.substringAfterLast('_')

val main = "Main"

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://storage.googleapis.com/r8-releases/raw")
    }
}

val r8: Configuration by configurations.creating

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
    r8("com.android.tools:r8:3.3.72")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersionNumber
}

tasks.withType<AbstractCompile> {
    sourceCompatibility = javaVersionNumber
    targetCompatibility = javaVersionNumber
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Main-Class" to main))
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("shadow")
    mergeServiceFiles()
}

val shadowJarMinified = tasks.register<JavaExec>("shadowJarMinified") {
    dependsOn(configurations.runtimeClasspath)
    
    val proguardRules = file("src/main/proguard-rules.pro")
    inputs.files(tasks.shadowJar.get().outputs.files, proguardRules)
    
    val r8File = File("$buildDir/libs/${base.archivesName.get()}-shadow-minified.jar")
    outputs.file(r8File)
    
    classpath(r8)
    
    mainClass.set("com.android.tools.r8.R8")
    val javaHome = File(ProcessHandle.current().info().command().get()).parentFile.parentFile.canonicalPath
    val javaHomeVersion = Runtime.getRuntime().exec("$javaHome/bin/java -version").run {
        (inputStream.bufferedReader().readText() + errorStream.bufferedReader().readText()).split('"')[1]
    }
    check(JavaVersion.toVersion(javaHomeVersion).isCompatibleWith(javaVersion)) {
        "Incompatible Java Versions: compile-target $javaVersionNumber, r8 runtime $javaHomeVersion (needs to be as new or newer)"
    }
    
    val args = mutableListOf(
        "--debug",
        "--classfile",
        "--output",
        r8File.toString(),
        "--pg-conf",
        proguardRules.toString(),
        "--lib",
        javaHome,
    )
    args.add(tasks.shadowJar.get().outputs.files.joinToString(" "))
    
    this.args = args
    
    doFirst {
        check(proguardRules.exists()) { "$proguardRules doesn't exist" }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
