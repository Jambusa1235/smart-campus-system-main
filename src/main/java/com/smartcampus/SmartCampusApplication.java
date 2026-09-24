package com.smartcampus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  Smart Campus Management & Academic Information System Started!  ");
        System.out.println("  Access URL: http://localhost:8080                               ");
        System.out.println("  H2 Console: http://localhost:8080/h2-console                    ");
        System.out.println("==================================================================");
    }
}
