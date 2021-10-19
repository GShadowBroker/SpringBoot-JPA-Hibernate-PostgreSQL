package com.example.demo.app_user;

import com.example.demo.app_user.app_user_role.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Configuration
public class SeedConfig {

    private final Logger log = LoggerFactory.getLogger(SeedConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner run(AppUserService service) {
        log.info("Seeding roles and users into db...");

        return args -> {
            // create roles
            service.saveRole(new Role("ROLE_USER"));
            service.saveRole(new Role("ROLE_ADMIN"));
            service.saveRole(new Role("ROLE_SUPER_ADMIN"));

            // create users
            service.saveUser(new AppUser(
                    "Gledyson",
                    "Ferreira",
                    "gledysonferreira@gmail.com",
                    passwordEncoder.encode("1234"),
                    new ArrayList<>()
            ));
            service.saveUser(new AppUser(
                    "Vitória",
                    "Ferreira",
                    "vitoriaferreira@gmail.com",
                    passwordEncoder.encode("1234"),
                    new ArrayList<>()
            ));
            service.saveUser(new AppUser(
                    "Rosimeire",
                    "Nogueira",
                    "rosimeire@gmail.com",
                    passwordEncoder.encode("1234"),
                    new ArrayList<>()
            ));

            // add roles to users
            service.addRoleToUser("gledysonferreira@gmail.com", "ROLE_USER");
            service.addRoleToUser("gledysonferreira@gmail.com", "ROLE_SUPER_ADMIN");
            service.addRoleToUser("vitoriaferreira@gmail.com", "ROLE_USER");
            service.addRoleToUser("vitoriaferreira@gmail.com", "ROLE_ADMIN");
            service.addRoleToUser("rosimeire@gmail.com", "ROLE_USER");
        };
    }

}
