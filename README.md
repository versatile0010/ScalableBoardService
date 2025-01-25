# Scalable Board Service

> A simple board application but focus on scalability. 🚀

Details will be updated soon. 👀

---

## Tools & Technologies

- **Language**: `Kotlin(21)`
- **Framework**: `Spring Boot`
- **Database**: `MySQL`, `Redis`
- **Messaging**: `Kafka`
- **ORM**: `JPA/Hibernate`

---

## Microservice Architecture

- Architecture can be restructured soon.

| **Service**       | **Description**                            | **Port** |
|-------------------|--------------------------------------------|----------|
| **Common**        |                                            |          |
| ㄴ `snowflake`     | generate unique IDs for distributed system | none     |
| **Board Service** |                                            |          |
| ㄴ `article`       |                                            | 8080     |
| ㄴ `article-read`  |                                            | 8081     |
| ㄴ `hot-article`   |                                            | 8083     |
| ㄴ `comment`       |                                            | 8082     |
| ㄴ `like`          |                                            | 8084     |
| ㄴ `view`          |                                            | 8085     |

---