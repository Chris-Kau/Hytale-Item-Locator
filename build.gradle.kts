plugins {
    id("java")
}

group = "MysticalAsian"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar{
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/resources")
}