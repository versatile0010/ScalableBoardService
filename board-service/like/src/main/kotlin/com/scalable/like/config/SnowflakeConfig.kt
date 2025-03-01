package com.scalable.like.config

import com.common.snowflake.Snowflake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnowflakeConfig {
    @Bean
    fun snowflake(): Snowflake {
        return Snowflake()
    }
}