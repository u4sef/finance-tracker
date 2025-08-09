plugins {
    application
    checkstyle
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories { mavenCentral() }

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx {
    version = "21.0.5"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

application { mainClass.set("com.ft.app.MainApp") }

tasks.test { useJUnitPlatform() }

checkstyle { toolVersion = "10.17.0" }
