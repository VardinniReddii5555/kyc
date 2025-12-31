package com.kyc.kycapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringIntinalizrApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIntinalizrApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner demo(com.kyc.kycapp.repository.UserRepository repository) {
        return (args) -> {
            if (repository.findByUsernameAndPassword("test", "password").isEmpty()) {
                com.kyc.kycapp.entity.User user = new com.kyc.kycapp.entity.User();
                user.setUsername("test");
                user.setPassword("password");
                repository.save(user);
            }
        };
    }

}
