dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation(project(":common-service:snowflake"))
    implementation(project(":common-service:outbox-relay"))
    implementation(project(":common-service:event"))
}