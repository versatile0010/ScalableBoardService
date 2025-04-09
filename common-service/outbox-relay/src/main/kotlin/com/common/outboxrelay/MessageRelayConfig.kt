package com.common.outboxrelay

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@EnableAsync
@EnableScheduling
@Configuration
@ComponentScan("com.common.outboxrelay")
class MessageRelayConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
) {

    @Bean
    fun messageRelayKafkaTemplate(): KafkaTemplate<String, String> {
        val configMap = buildMap<String, Any> {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.ACKS_CONFIG, "all")
        }
        return KafkaTemplate(DefaultKafkaProducerFactory(configMap))
    }

    @Bean
    fun messageRelayPublishEventExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 20
            maxPoolSize = 50
            queueCapacity = 100
            setThreadNamePrefix("outbox-message-relay-publish-event-")
        }
    }

    @Bean
    fun messageRelayPublishPendingEventExecutor(): Executor {
        return Executors.newSingleThreadScheduledExecutor()
    }
}
