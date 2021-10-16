package com.example.demo.app_user;

import com.example.demo.app_user.app_user_role.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class SeedConfig {

    private final Logger log = LoggerFactory.getLogger(SeedConfig.class);

    @Bean
    CommandLineRunner run(AppUserService service) {
        log.info("Seeding roles and users into db...");

        return args -> {
            // create roles
            service.saveRole(new Role("ROLE_ADMIN"));
            service.saveRole(new Role("ROLE_MANAGER"));
            service.saveRole(new Role("ROLE_USER"));
            service.saveRole(new Role("ROLE_SUPER_ADMIN"));

            // create users
            service.saveUser(new AppUser(
                    "Gledyson",
                    "Ferreira",
                    "gledysonferreira@gmail.com",
                    "1234",
                    new ArrayList<>()
            ));
            service.saveUser(new AppUser(
                    "Vitória",
                    "Ferreira",
                    "vitoriaferreira@gmail.com",
                    "1234",
                    new ArrayList<>()
            ));
            service.saveUser(new AppUser(
                    "Rosimeire",
                    "Nogueira",
                    "rosimeire@gmail.com",
                    "1234",
                    new ArrayList<>()
            ));

            // add roles to users
            service.addRoleToUser("gledysonferreira@gmail.com", "ROLE_SUPER_ADMIN");
            service.addRoleToUser("vitoriaferreira@gmail.com", "ROLE_MANAGER");
            service.addRoleToUser("rosimeire@gmail.com", "ROLE_USER");
        };
    }

}
