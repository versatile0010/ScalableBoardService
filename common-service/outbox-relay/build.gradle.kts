dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation(project(":common-service:snowflake"))
    implementation(project(":common-service:event"))
    implementation(project(":common-service:data-serializer"))
}