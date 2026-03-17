import com.android.build.api.dsl.CommonExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
    jacoco
}

jacoco {
    toolVersion = "0.8.11"
}

subprojects {
    apply(plugin = "jacoco")
    afterEvaluate {
        val androidExt = extensions.findByType<CommonExtension>() ?: return@afterEvaluate
        androidExt.buildTypes.findByName("debug")?.enableUnitTestCoverage = true

        val testTask = tasks.findByName("testDebugUnitTest") ?: return@afterEvaluate
        val reportTask =
            tasks.findByName("jacocoTestReport") as? org.gradle.testing.jacoco.tasks.JacocoReport
                ?: tasks.register<org.gradle.testing.jacoco.tasks.JacocoReport>("jacocoTestReport") {}.get()
        reportTask.apply {
            dependsOn(testTask)
            group = "verification"
            description = "JaCoCo coverage report for ${project.name}"

            val buildDir = layout.buildDirectory.get().asFile
            doFirst {
                executionData.setFrom(
                    files(
                        java.io.File(buildDir, "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"),
                        java.io.File(buildDir, "jacoco/testDebugUnitTest.exec")
                    ).filter { it.exists() }
                )
            }
            sourceDirectories.from(
                files(
                    androidExt.sourceSets.getByName("main").java.directories,
                    androidExt.sourceSets.getByName("main").kotlin.directories
                )
            )
            classDirectories.from(
                fileTree(buildDir) {
                    include(
                        "intermediates/javac/debug/classes/**",
                        "tmp/kotlin-classes/debug/**"
                    )
                    exclude(
                        "**/R.class",
                        "**/R$*.class",
                        "**/BuildConfig.class",
                        "**/Manifest*.class"
                    )
                }
            )
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Aggregated JaCoCo coverage report for all modules"

    val testTasks = subprojects.mapNotNull { sub ->
        sub.tasks.findByName("testDebugUnitTest")
    }
    dependsOn(testTasks)

    sourceDirectories.from(
        subprojects.flatMap { sub ->
            listOf(
                sub.file("src/main/java"),
                sub.file("src/main/kotlin")
            ).filter { it.exists() }
        }
    )
    classDirectories.from(
        subprojects.map { sub ->
            sub.fileTree(sub.layout.buildDirectory.get().asFile) {
                include(
                    "intermediates/javac/debug/classes/**",
                    "tmp/kotlin-classes/debug/**"
                )
                exclude(
                    "**/R.class",
                    "**/R$*.class",
                    "**/BuildConfig.class",
                    "**/Manifest*.class"
                )
            }
        }
    )
    executionData.setFrom(
        files(
            subprojects.flatMap { sub ->
                val buildDir = sub.layout.buildDirectory.get().asFile
                listOf(
                    File(buildDir, "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"),
                    File(buildDir, "jacoco/testDebugUnitTest.exec")
                )
            }.filter { it.exists() }
        )
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}