package com.studygroup.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Entry point for the Authentication Service.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Handle user registration and authentication</li>
 *   <li>Issue and validate JWT tokens</li>
 *   <li>Manage user credentials securely</li>
 *   <li>Publish UserRegisteredEvent to auth.user-registered topic</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}