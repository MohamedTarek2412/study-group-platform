package com.studygroup.group;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Entry point for the Group Service.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>CRUD operations for study groups</li>
 *   <li>Admin approval of study groups</li>
 *   <li>Join requests management</li>
 *   <li>Member management</li>
 *   <li>Real-time updates via WebSocket</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class GroupServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApplication.class, args);
    }
}