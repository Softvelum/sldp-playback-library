import com.android.build.gradle.BaseExtension
import org.gradle.kotlin.dsl.the

plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.softvelum.sldp"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.androidx.annotations)
}

tasks.register<Jar>("sourcesJar") {
    from(project.the<BaseExtension>().sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.softvelum"
            artifactId = "sldp-playback-library"
            version = "0.0.1"

            artifact(tasks["sourcesJar"])
            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", it.group)
                    dependencyNode.appendNode("artifactId", it.name)
                    dependencyNode.appendNode("version", it.version)
                }
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
