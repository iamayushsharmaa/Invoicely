package com.example.saas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class SaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasApplication.class, args);
    }


    @Bean
    CommandLineRunner test(DataSource dataSource) {
        return args -> {
            System.out.println(dataSource.getConnection().getMetaData().getURL());
        };
    }
}
