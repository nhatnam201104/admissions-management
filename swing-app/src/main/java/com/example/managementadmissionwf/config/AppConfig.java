package com.example.managementadmissionwf.config;


import com.example.managementadmissionwf.dal.entity.RoleUser;
import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;

    // @Bean
    // CommandLineRunner seedAdminUser() {
    //     return args -> {

    //         if (userRepository.existsByUsername("admin")) {
    //             return;
    //         }

    //         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    //         Users admin = Users.builder()
    //                 .fullname("Administrator")
    //                 .email("admin@gmail.com")
    //                 .username("admin")
    //                 .password(encoder.encode("admin123"))
    //                 .role(RoleUser.ADMIN)
    //                 .build();

    //         userRepository.save(admin);

    //         System.out.println(" Admin user seeded successfully");
    //     };
    // }
}
