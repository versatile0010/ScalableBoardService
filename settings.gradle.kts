rootProject.name = "board"

include(
    "board-service",
    "board-service:article",
    "board-service:comment",
    "board-service:article-read",
    "board-service:hot-article",
    "board-service:view",
    "board-service:like",
    "common-service",
    "common-service:snowflake",
)