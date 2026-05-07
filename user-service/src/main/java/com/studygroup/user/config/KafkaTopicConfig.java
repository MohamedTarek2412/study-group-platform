package com.studygroup.user.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares Kafka topics the user-service owns.
 *
 * <p>Spring Boot auto-creates topics via {@link NewTopic} beans
 * when {@code spring.kafka.admin.auto-create=true} (default in dev).
 *
 * <p>Replication factor 3 in production; 1 in local/dev.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.user-profile-updated}")
    private String profileUpdatedTopic;

    @Value("${kafka.topics.creator-approved}")
    private String creatorApprovedTopic;

    @Value("${kafka.topics.creator-rejected}")
    private String creatorRejectedTopic;

    @Bean
    public NewTopic userProfileUpdatedTopic() {
        return TopicBuilder.name(profileUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic creatorApprovedTopic() {
        return TopicBuilder.name(creatorApprovedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic creatorRejectedTopic() {
        return TopicBuilder.name(creatorRejectedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
