package org.keycloak.quickstart.springsecurity.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(MyApplication.class, args);
    }

}
