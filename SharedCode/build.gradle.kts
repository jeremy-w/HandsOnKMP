import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    // Build SharedCode.framework
    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }

    // Use the Kotlin/Native stdlib
    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
    }


    // Build JavaScript module.
    js("js")


    // Build android.jar
    jvm("android")

    // Use the Kotlin/JVM stdlib
    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }
}

// Replace the destination directory with the source files.
// (It's Sync-like-rsync, not Sync-like-atomic.)
val packForXcode by tasks.creating(Sync::class) {
    // Let xcodebuild CONFIGURATION pick the kind of framework
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    // Tell Gradle "mode" factors into what we're building.
    // This way, changing CONFIGURATION will cause a rebuild.
    inputs.property("mode", mode)
    // Tell Gradle to link the framework for that configuration before running packForXcode.
    val framework =
        kotlin.targets
            .getByName<KotlinNativeTarget>("ios")
            .binaries.getFramework(mode)
    dependsOn(framework.linkTask)

    // Configure the paths to keep in sync
    from({ framework.outputDirectory })
    val targetDir = File(buildDir, "xcode-frameworks")
    into(targetDir)

    // After syncing, add a script to ensure xcodebuild uses the correct Java when calling gradlew.
    doLast {
        val gradleWrapper = File(targetDir, "gradlew")
        gradleWrapper.writeText(
            """
            #!/bin/bash
            export 'JAVA_HOME=${System.getProperty("java.home")}'
            cd '${rootProject.rootDir}' || exit 1
            ./gradlew "$@"

        """.trimIndent()
        )
        gradleWrapper.setExecutable(true)

        /*
        // Dump the list of files this depends on.
        // This could be used as an input to the Run Script build phase to shortcut even having to launch Gradle.
        // Or you can just not have an inputs list, and Gradle will always be run, but can then conclude it doesn't have any work to do and exit.
        // Since the CONFIGURATION isn't tracked in this, that's probably needed for correctness. :\
        val xcfilelist = File(targetDir, "inputs.xcfilelist")
        val paths =
            framework.compilation
                .compileKotlinTask.inputs
                .sourceFiles.map { it.path }
        xcfilelist.writeText(paths.joinToString(separator="\n", postfix="\n"))
         */
    }
}

tasks.getByName("build").dependsOn(packForXcode)
