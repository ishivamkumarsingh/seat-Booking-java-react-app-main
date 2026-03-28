package com.seatbooking.config;

import com.seatbooking.model.User;
import com.seatbooking.model.enums.UserRole;
import com.seatbooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Create default admin
                userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@office.com")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("System Admin")
                        .role(UserRole.ADMIN)
                        .build());

                // Create default manager
                userRepository.save(User.builder()
                        .username("manager")
                        .email("manager@office.com")
                        .password(passwordEncoder.encode("manager123"))
                        .fullName("Office Manager")
                        .role(UserRole.MANAGER)
                        .build());

                // Create default user
                userRepository.save(User.builder()
                        .username("user")
                        .email("user@office.com")
                        .password(passwordEncoder.encode("user123"))
                        .fullName("John Doe")
                        .role(UserRole.USER)
                        .build());

                System.out.println("Default users created: admin/admin123, manager/manager123, user/user123");
            }
        };
    }
}