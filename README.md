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

| **Service**       | **Description** | **Port** |
|-------------------|-----------------|----------|
| **Common**        |                 | 9001     |
| **Board Service** |                 |          |
| `article`         |                 | 8080     |
| `article-read`    |                 | 8081     |
| `hot-article`     |                 | 8083     |
| `comment`         |                 | 8082     |
| `like`            |                 | 8084     |
| `view`            |                 | 8085     |

---