package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void testFindStudent() {
        // given
        String email = "jaguatirica_da_serra@homeil.com";

        Student testStudent = new Student(
                "Jaguatirica",
                LocalDate.of(2012, 1, 1),
                email
        );
        underTest.save(testStudent);

        // when
        Student expected = underTest.findStudentByEmail(email).get();

        // then
        assertThat(expected).isNotNull();
        assertThat(expected).isInstanceOf(Student.class);
        assertThat(expected).hasNoNullFieldsOrProperties();
    }

    @Test
    void testNotFindStudent() {
        // given
        String email = "julinho@homeil.com";

        // when
        boolean expected = underTest.findStudentByEmail(email).isPresent();

        // then
        assertThat(expected).isFalse();
    }

    @Test
    void testEmailNotExist() {
        // given
        String email = "americano@homeil.com";

        // when
        boolean expected = underTest.emailExists(email);

        // then
        assertThat(expected).isFalse();
    }

    @Test
    void testEmailExists() {
        // given
        String email = "jaodalua@gmail.com";

        Student testStudent = new Student(
                "Jão",
                LocalDate.of(2000, 1, 1),
                email
        );
        underTest.save(testStudent);

        // when
        boolean expected = underTest.emailExists(email);

        // then
        assertThat(expected).isTrue();
    }
}