dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka:3.3.4")
    implementation(project(":common-service:event"))
    implementation(project(":common-service:data-serializer"))
}