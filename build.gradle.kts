import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22"
    id("io.quarkus")
    id("org.openapi.generator") version "7.2.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val jaxrsFunctionalTestBuilderVersion: String by project
val okhttpVersion: String by project
val wiremockVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-hibernate-reactive-panache")
    implementation("io.quarkus:quarkus-liquibase")
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.quarkus:quarkus-reactive-mysql-client")
    implementation("io.quarkus:quarkus-undertow")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-rest-client-reactive-jackson")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-smallrye-health")

    implementation("io.vertx:vertx-core")
    implementation("io.vertx:vertx-lang-kotlin")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    configurations.all {
        exclude(group = "commons-logging", module = "commons-logging")
    }
    implementation("org.jboss.logging:commons-logging-jboss-logging")

    testImplementation("org.wiremock:wiremock:$wiremockVersion")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    testImplementation("fi.metatavu.jaxrs.testbuilder:jaxrs-functional-test-builder:$jaxrsFunctionalTestBuilderVersion")
}

group = "fi.metatavu.vp"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets["main"].java {
    srcDir("build/generated/api-spec/src/main/kotlin")
    srcDir("build/generated/vehicle-management-api-spec/src/main/kotlin")
    srcDir("build/generated/user-management-api-spec/src/main/kotlin")
}
sourceSets["test"].java {
    srcDir("build/generated/api-client/src/main/kotlin")
    srcDir("quarkus-invalid-param-test/src/main/kotlin")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.enterprise.context.RequestScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}


val generateApiSpec = tasks.register("generateApiSpec",GenerateTask::class){
    setProperty("generatorName", "kotlin-server")
    setProperty("inputSpec",  "$rootDir/vp-kuljetus-transport-management-specs/services/work-planning-services.yaml")
    setProperty("outputDir", "$buildDir/generated/api-spec")
    setProperty("apiPackage", "${project.group}.api.spec")
    setProperty("invokerPackage", "${project.group}.api.invoker")
    setProperty("modelPackage", "${project.group}.api.model")
    setProperty("templateDir", "$rootDir/openapi/api-spec")
    setProperty("validateSpec", false)

    this.configOptions.put("library", "jaxrs-spec")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("useMutiny", "true")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateApiClient = tasks.register("generateApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp3")
    setProperty("inputSpec",  "$rootDir/vp-kuljetus-transport-management-specs/services/work-planning-services.yaml")
    setProperty("outputDir", "$buildDir/generated/api-client")
    setProperty("packageName", "${project.group}.test.client")
    setProperty("validateSpec", false)

    this.configOptions.put("dateLibrary", "string")
    this.configOptions.put("collectionType", "array")
    this.configOptions.put("serializationLibrary", "jackson")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
}

val generateVehicleManagementApiClient = tasks.register("generateVehicleManagementApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin-server")
    setProperty("inputSpec",  "$rootDir/vp-kuljetus-transport-management-specs/services/vehicle-management-services.yaml")
    setProperty("outputDir", "$buildDir/generated/vehicle-management-api-spec")
    setProperty("apiPackage", "${project.group}.vehiclemanagement.spec")
    setProperty("invokerPackage", "${project.group}.vehiclemanagement.invoker")
    setProperty("modelPackage", "${project.group}.vehiclemanagement.model")
    setProperty("templateDir", "$rootDir/openapi/rest-client")
    setProperty("validateSpec", false)

    this.configOptions.put("library", "jaxrs-spec")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("useMutiny", "true")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateUserManagementApiClient = tasks.register("generateUserManagementApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin-server")
    setProperty("inputSpec",  "$rootDir/vp-kuljetus-transport-management-specs/services/user-management-services.yaml")
    setProperty("outputDir", "$buildDir/generated/user-management-api-spec")
    setProperty("apiPackage", "${project.group}.usermanagement.spec")
    setProperty("invokerPackage", "${project.group}.usermanagement.invoker")
    setProperty("modelPackage", "${project.group}.usermanagement.model")
    setProperty("templateDir", "$rootDir/openapi/rest-client")
    setProperty("validateSpec", false)

    this.configOptions.put("library", "jaxrs-spec")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("useMutiny", "true")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

tasks.named("compileKotlin") {
    dependsOn(generateApiSpec)
    dependsOn(generateVehicleManagementApiClient)
    dependsOn(generateUserManagementApiClient)
}

tasks.named("compileTestKotlin") {
    dependsOn(generateApiClient)
}

tasks.named<Test>("test") {
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
}

tasks.named<Test>("testNative") {
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
}

