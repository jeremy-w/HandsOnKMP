import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64 else ::iosX64

    // build SharedCode.framework
    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }

    // Build android.jar
    jvm("android")

    // Use the Kotlin/Native stdlib
    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
    }

    // Use the Kotlin/JVM stdlib
    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }
}

// Replace the destination directory with the source files.
// (It's Sync-like-rsync, not Sync-like-atomic.)
val packForXcode by tasks.creating(Sync::class) {
    /* Figure out where the framework got compiled too, and mark the build type as a build input */
    // Apparently we're expecting to be run by xcodebuild?
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    // Surface this external info to the build system as an input.
    // This way, changing mode will cause a rebuild.
    inputs.property("mode", mode)
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)
    dependsOn(framework.linkTask)

    /* Rig up the Sync from-to */
    // Declare the source paths for this Sync task.
    from({ framework.outputDirectory })
    // And the destination path.
    val targetDir = File(buildDir, "xcode-frameworks")
    into(targetDir)

    doLast {
        val gradleWrapper = File(targetDir, "gradlew")
        gradleWrapper.writeText(
            """
            #!/bin/bash
            export 'JAVA_HOME=${System.getProperty("java.home")}'
            cd '${rootProject.rootDir}'
            ./gradlew \$@
        """.trimIndent()
        )
        gradleWrapper.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)
