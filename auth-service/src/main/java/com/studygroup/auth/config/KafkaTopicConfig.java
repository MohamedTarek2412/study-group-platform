package com.studygroup.auth.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares Kafka topics owned by the auth-service.
 *
 * <p>Spring Boot auto-creates topics via {@link NewTopic} beans
 * when {@code spring.kafka.admin.auto-create=true} (default in dev).
 *
 * <p>Replication factor 3 in production; 1 in local/dev.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.user-registered:auth.user-registered}")
    private String userRegisteredTopic;

    /**
     * Topic: auth.user-registered
     * Published when a user successfully registers.
     * Consumed by user-service to create user profiles.
     */
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
