package com.bazuuyu.b2b.wholesale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.bazuuyu.b2b.wholesale", "com.bazuuyu.b2b.core"})
@EntityScan(basePackages = {"com.bazuuyu.b2b.wholesale.entity"})
@EnableJpaRepositories(basePackages = "com.bazuuyu.b2b.wholesale.repository")
public class WholesaleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WholesaleServiceApplication.class, args);
    }
}
