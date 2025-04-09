package com.common.outboxrelay

import com.common.snowflake.Snowflake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnowflakeConfig {
    @Bean
    fun outboxIdGenerator(): Snowflake {
        return Snowflake()
    }

    @Bean
    fun eventIdGenerator(): Snowflake {
        return Snowflake()
    }
}