package com.sunrisedental.config;

import com.sunrisedental.model.Role;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User(
                        "admin",
                        passwordEncoder.encode("Admin@123"),
                        "System Administrator",
                        "admin@sunrisedental.com",
                        Role.ADMINISTRATOR
                );

                userRepository.save(admin);

                System.out.println(
                        "======================================"
                );
                System.out.println(
                        "INITIAL ADMIN CREATED"
                );
                System.out.println(
                        "Username: admin"
                );
                System.out.println(
                        "Password: Admin@123"
                );
                System.out.println(
                        "======================================"
                );
            }
        };
    }
}
