package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class StudentConfig {

    @Autowired
    StudentRepository repository;

    @Bean
    CommandLineRunner commandLine() {
        return args -> {

            Student gledyson = new Student(
                    "Gledyson",
                    LocalDate.of(1990, 4, 21),
                    "gledysonferreira@gmail.com"
            );

            Student vitoria = new Student(
                    "Vitória",
                    LocalDate.of(2007, 2, 3),
                    "vitoriaferreira@gmail.com"
            );

            repository.saveAll(
                    List.of(gledyson, vitoria)
            );

        };
    }

}
