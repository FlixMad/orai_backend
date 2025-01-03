package com.ovengers.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaAuditing
// MySQL repositories
@EnableJpaRepositories(basePackages = "com.ovengers.chatservice.mysql")
// MongoDB repositories
@EnableMongoRepositories(basePackages = "com.ovengers.chatservice.mongodb")
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }

}
