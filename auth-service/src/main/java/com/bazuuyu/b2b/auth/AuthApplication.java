package com.bazuuyu.b2b.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.bazuuyu.b2b.auth", "com.bazuuyu.b2b.core"})
@EntityScan(basePackages = "com.bazuuyu.b2b.core.entity")
@EnableJpaRepositories(basePackages = "com.bazuuyu.b2b.auth.repository")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
