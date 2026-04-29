package com.studygroup.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Entry point for the User Service.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Manage user profile data (display name, bio, avatar, subjects)</li>
 *   <li>Handle creator-approval workflow</li>
 *   <li>Consume UserRegisteredEvent from auth-service to bootstrap profiles</li>
 *   <li>Publish profile-update & creator-approval events downstream</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}