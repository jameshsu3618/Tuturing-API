import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.gorylenko.gradle-git-properties") version "1.4.21"
    kotlin("jvm") version "1.3.60"
    kotlin("plugin.spring") version "1.3.60"
    kotlin("plugin.jpa") version "1.3.60"
    kotlin("plugin.allopen") version "1.3.60"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
    id("io.gitlab.arturbosch.detekt") version "1.2.2"
    id("org.liquibase.gradle") version "2.0.3"
    kotlin("kapt") version "1.3.61"
    idea
    jacoco
    java
    application
    id("com.github.ManifestClasspath") version "0.1.0-RELEASE"
    id("com.yelp.codegen.plugin") version "1.3.0"
    id("org.kordamp.gradle.stats") version "0.2.2"
    id("com.adarshr.test-logger") version "2.0.0"
}

ktlint {
    version.set("0.36.0")
    disabledRules.set(setOf("no-wildcard-imports"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
    }
}

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("$projectDir/detekt.yml")

    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
    }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

group = "com.tuturing"
version = file("VERSION").readText(Charsets.UTF_8).trim()

java.sourceCompatibility = JavaVersion.VERSION_11

springBoot {
    buildInfo()
    mainClassName = "com.tuturing.api.ApiApplicationKt"
}

application {
    mainClassName = "org.sprintframework.boot.loader.JarLauncher"
}

application.applicationDistribution.from("src/main/resources/application.yml") {
    into("config")
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "Hoxton.RC2"

val test by tasks.getting(Test::class) {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    useJUnitPlatform { }

    testlogger {
        theme = ThemeType.MOCHA_PARALLEL
        slowThreshold = 2000
    }
}

// jacoco test coverage
//tasks.jacocoTestCoverageVerification {
//    violationRules {
//        rule {
//            limit {
//                minimum = "0.0".toBigDecimal()
//            }
//        }
//    }
//}
//
//tasks.jacocoTestReport {
//    reports {
//        xml.isEnabled = true
//        csv.isEnabled = false
//        html.isEnabled = true
//        html.destination = file("$buildDir/reports/coverage")
//    }
//}

//tasks.withType<JacocoReport> {
//    classDirectories.setFrom(
//        sourceSets.main.get().output.asFileTree.matching {
//            exclude("com/sabre/**/*.class", "dev/sabre/**/*.class")
//        }
//    )
//}

//val testCoverage by tasks.registering {
//    group = "verification"
//    description = "Runs the unit tests with coverage."
//
//    dependsOn(":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
//    val jacocoTestReport = tasks.findByName("jacocoTestReport")
//    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
//    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter(jacocoTestReport)
//}

val springFrameworkVersion = "5.2.4.RELEASE"
val springFrameworkJmsVersion = springFrameworkVersion

val springBootVersion = "2.2.5.RELEASE"
val springBootActuatorVersion = springBootVersion
val springBootAopVersion = springBootVersion
val springBootWebVersion = springBootVersion
val springBootUndertowVersion = springBootVersion
val springBootJpaVersion = springBootVersion
val springBootConfigurationProcessorVersion = springBootVersion
val springBootRedisVersion = springBootVersion
val springBootSecurityVersion = springBootVersion
val springBootOAuth2Version = "2.2.4.RELEASE"
val springBootTestVersion = springBootVersion
val springBootDevToolsVersion = springBootVersion

val springfoxSwaggerVersion = "2.9.2"
val okHttpVersion = "3.14.9"
val retrofitVersion = "2.9.0"
val moshiVersion = "1.9.2"

dependencies {
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // documentation
    implementation("io.springfox:springfox-swagger2:$springfoxSwaggerVersion")
    implementation("io.springfox:springfox-swagger-ui:$springfoxSwaggerVersion")

    // visibility - healthchecks & info
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootActuatorVersion")
    implementation("pl.project13.maven:git-commit-id-plugin:3.0.1")
    // implementation("org.jolokia:jolokia.core")

    // aop
    implementation("org.springframework.boot:spring-boot-starter-aop:$springBootAopVersion")

    // metrics
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-influx")

    // logger
    // https://www.baeldung.com/logback
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")

    // spring boot web
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootWebVersion") {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow:$springBootUndertowVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootJpaVersion") {
        exclude("org.apache.tomcat", "tomcat-jdbc")
    }
    implementation("com.zaxxer:HikariCP")
    implementation("mysql:mysql-connector-java")

    // soap
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:3.3.5")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxrs:3.3.5")
    implementation("org.apache.cxf:cxf-rt-features-logging:3.3.5")
    implementation("com.sun.xml.ws:jaxws-rt:2.3.2")
    implementation("org.slf4j:jul-to-slf4j:1.7.30")
    implementation("javax.xml.ws:jaxws-api:2.3.1")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("jakarta.activation:jakarta.activation-api:1.2.2")
    implementation("com.sun.activation:jakarta.activation:1.2.2")

    // mapper
    implementation("org.mapstruct:mapstruct:1.3.1.Final")
    kapt("org.mapstruct:mapstruct-processor:1.3.1.Final")

    // configuration processor
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootConfigurationProcessorVersion")

//  implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:$springBootRedisVersion")
//  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security:$springBootSecurityVersion")
    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:$springBootOAuth2Version")
// 	implementation("org.springframework.cloud:spring-cloud-starter-aws-messaging")

    // Moshi + OkHttp + Retrofit
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")

    // Date Support
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    // jms
    implementation("org.springframework:spring-jms:$springFrameworkJmsVersion")

    // aws
    implementation("com.amazonaws:aws-java-sdk:1.11.721")
    implementation("com.amazonaws:aws-java-sdk-core:1.11.721")
    implementation("com.amazonaws:amazon-sqs-java-messaging-lib:1.0.0")
    implementation("com.amazonaws:aws-java-sdk-kms:1.11.721")

    // jwt
    implementation("com.auth0:java-jwt:3.9.0")

    // liquibase
    implementation("org.liquibase:liquibase-core:3.9.0")
    // liquibase command line
    liquibaseRuntime("mysql:mysql-connector-java")
    liquibaseRuntime("org.liquibase:liquibase-core:3.9.0")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:2.1.1")
    liquibaseRuntime("ch.qos.logback:logback-core:1.2.3")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")
    liquibaseRuntime("org.yaml:snakeyaml:1.26")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate5:3.8")

    // kotlin coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    implementation("nl.komponents.kovenant:kovenant:3.3.0")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.1")

    // mapbox
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:5.0.0")

    // stripe
    implementation("com.stripe:stripe-java:17.11.0")

    // handlebars
    implementation("com.github.jknack:handlebars:4.1.2")

    // sendgrid
    implementation("com.sendgrid:sendgrid-java:4.4.8")

    // flying saucer (pdf)
    implementation("org.xhtmlrenderer:flying-saucer-core:9.1.20")
    implementation("org.xhtmlrenderer:flying-saucer-pdf:9.1.20")

    // Apache commons
    implementation("commons-codec:commons-codec:1.14")

    developmentOnly("org.springframework.boot:spring-boot-devtools:$springBootDevToolsVersion")

    // testing
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("com.ninja-squad:springmockk:1.1.3")
    testImplementation("io.strikt:strikt-core:0.22.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootTestVersion") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xallow-result-return-type")
        jvmTarget = "11"
    }
}

val liquibaseUrl = "jdbc:mysql://localhost:3306/tuturing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
val liquibaseUsername = "root"
val liquibasePassword = ""
val liquibaseFormat = "json"
val liquibaseChangeLogFile = "$projectDir/src/main/resources/db/changelog/db.changelog-master." + liquibaseFormat
val liquibaseBeforeSnapshotFile = "liquibaseBefore." + liquibaseFormat
val liquibaseDiffFile = "liquibaseDiff." + liquibaseFormat
val liquibaseOfflineUrl = "offline:mysql?snapshot=" + liquibaseBeforeSnapshotFile

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "changeLogFile" to liquibaseChangeLogFile,
            "url" to liquibaseUrl,
            "username" to liquibaseUsername,
            "password" to liquibasePassword
        )
    }
    activities.register("snapshot") {
        this.arguments = mapOf(
            "changeLogFile" to liquibaseChangeLogFile,
            "url" to liquibaseUrl,
            "username" to liquibaseUsername,
            "password" to liquibasePassword,
            "outputFile" to liquibaseBeforeSnapshotFile,
            "snapshotFormat" to liquibaseFormat
        )
    }
    activities.register("diff") {
        this.arguments = mapOf(
            "url" to liquibaseOfflineUrl,
            "referenceUrl" to liquibaseUrl,
            "referenceUsername" to liquibaseUsername,
            "referencePassword" to liquibasePassword
        )
    }
    activities.register("diffChangeLog") {
        this.arguments = mapOf(
            "changeLogFile" to liquibaseDiffFile,
            "url" to liquibaseOfflineUrl,
            "referenceUrl" to liquibaseUrl,
            "referenceUsername" to liquibaseUsername,
            "referencePassword" to liquibasePassword
        )
    }
    runList = "main"
}

tasks.snapshot {
    doFirst {
        liquibase.runList = "snapshot"
    }
    doLast {
        liquibase.runList = "main"
    }
}

tasks.diff {
    doFirst {
        liquibase.runList = "diff"
    }
    doLast {
        liquibase.runList = "main"
    }
}

tasks.diffChangeLog {
    doFirst {
        liquibase.runList = "diffChangeLog"
    }
    doLast {
        liquibase.runList = "main"
    }
}

// swagger codegen
generateSwagger {
    platform = "kotlin-coroutines"
    packageName = "com.ean.api.v2_4"
//    inputFile = file("${projectDir}/api-specifications/sabre/airports-at-cities.yml")
//    inputFile = file("${projectDir}/api-specifications/sabre/geo-autocomplete.yml")
//    inputFile = file("${projectDir}/api-specifications/sabre/geo-search.yml")
//    inputFile = file("${projectDir}/api-specifications/sabre/bargain-finder-max.yml")
    inputFile = file("$projectDir/api-specifications/expedia-rapid/swagger_2.4.yaml")
    outputDir = file("$projectDir/src/main/kotlin")
}

// install precommit hook

tasks.register("installGitHooks", Copy::class) {
    description = "Copies git hooks ensuring everyone has the same hooks"
    group = "verification"

    println("Installing Git hooks")
    println("- from: ${rootProject.rootDir}/gradle/hooks")
    println("- into: ${rootProject.rootDir}/.git/hooks")

    from("${rootProject.rootDir}/gradle/hooks") {
        include("**/*")
    }
    into("${rootProject.rootDir}/.git/hooks")
    fileMode = Integer.parseUnsignedInt("755", 8)
}

afterEvaluate {
    tasks["build"].dependsOn(tasks.findByName("installGitHooks"))
    tasks["bootRun"].dependsOn(tasks.findByName("installGitHooks"))
}
