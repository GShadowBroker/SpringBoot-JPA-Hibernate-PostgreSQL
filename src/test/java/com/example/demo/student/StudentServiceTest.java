package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService underTest = new StudentService();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetAll() {
        // when
        underTest.getAll();

        // then
        verify(repository).findAll();
    }

    @Test
    void testCreateStudent() {
        // given
        Student testStudent = new Student(
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        // when
        underTest.create(testStudent);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);

        // then
        verify(repository).save(argumentCaptor.capture());

        Student captured = argumentCaptor.getValue();

        assertThat(captured).isEqualTo(testStudent);
    }

    @Test
    void testEmailTaken() {
        // given
        Student testStudent = new Student(
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        given(repository.findStudentByEmail(testStudent.getEmail())).willReturn(Optional.of(testStudent));

        // when
        // then
        assertThatThrownBy(() -> underTest.create(testStudent))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(String.format("Email '%s' already taken", testStudent.getEmail()));
    }

    @Test
    void testGetStudentById() {
        // given
        Student testStudent = new Student(
                UUID.randomUUID(),
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        // when
        given(repository.findById(testStudent.getId())).willReturn(Optional.of(testStudent));

        Student expected = repository.findById(testStudent.getId()).get();

        // then
        verify(repository).findById(testStudent.getId());

        assertThat(expected).isInstanceOf(Student.class);
        assertThat(expected).isNotNull();
        assertThat(expected).isEqualTo(testStudent);
    }

    @Test
    void testGetStudentByIdNotFound() {
        // given
        Student testStudent = new Student(
                UUID.randomUUID(),
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        given(repository.findById(testStudent.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getById(testStudent.getId()))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(String.format("Student '%s' not found", testStudent.getId()));
    }

    @Test
    void testDeleteById() {
        // given
        Student testStudent = new Student(
                UUID.randomUUID(),
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        given(repository.existsById(testStudent.getId())).willReturn(true);

        // when
        underTest.deleteById(testStudent.getId());

        // then
        verify(repository).existsById(testStudent.getId());
        verify(repository).deleteById(testStudent.getId());
    }

    @Test
    void testDeleteByIdNotFound() {
        // given
        Student testStudent = new Student(
                UUID.randomUUID(),
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        given(repository.existsById(testStudent.getId())).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteById(testStudent.getId()))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(String.format("Student '%s' not found", testStudent.getId()));
    }

    @Test
    void testUpdate() {
        // given
        Student testStudent = new Student(
                UUID.randomUUID(),
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        Student toBeUpdated = new Student(
                testStudent.getId(),
                "Marian",
                LocalDate.of(2002, 9, 1),
                "marian@example.com"
        );

        given(repository.findById(testStudent.getId())).willReturn(Optional.of(testStudent));
        given(repository.save(any(Student.class))).willReturn(toBeUpdated);

        // when
        Student expected = underTest.update(testStudent.getId(), toBeUpdated);

        // then
        assertThat(expected).isNotNull();
        assertThat(expected).isInstanceOf(Student.class);
        assertThat(expected).isEqualTo(toBeUpdated);
    }

    @Test
    void testUpdateStudentNotFound() {
        // given
        Student testStudent = new Student(
                UUID.randomUUID(),
                "Maria",
                LocalDate.of(2005, 9, 22),
                "maria@example.com"
        );

        Student toBeUpdated = new Student(
                testStudent.getId(),
                "Marian",
                LocalDate.of(2002, 9, 1),
                "marian@example.com"
        );

        given(repository.findById(testStudent.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.update(testStudent.getId(), toBeUpdated))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(String.format("Student '%s' not found", testStudent.getId()));

        verify(repository, never()).save(any());
    }
}