package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    private static final String EMAIL_TAKEN = "Email '%s' already taken";
    private static final String STUDENT_NOT_FOUND = "Student '%s' not found";

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Student create(Student student) {

        Optional<Student> studentOptional = getByEmail(student.getEmail());

        if (studentOptional.isPresent()) {
            throw new StudentNotFoundException(String.format(EMAIL_TAKEN, student.getEmail()));
        }

        return repository.save(student);
    }

    public Student getById(UUID id) {

        return repository.findById(id).orElseThrow(() -> {
            throw new StudentNotFoundException(String.format(STUDENT_NOT_FOUND, id));
        });
    }

    public void deleteById(UUID id) {

        if (!repository.existsById(id)) {
            throw new StudentNotFoundException(String.format(STUDENT_NOT_FOUND, id));
        }

        repository.deleteById(id);
    }

    @Transactional
    public Student update(UUID id, Student newStudent) {

        Student updatedStudent = getStudentById(id);

        if (!updatedStudent.getName().equals(newStudent.getName())) {
            updatedStudent.setName(newStudent.getName());
        }

        if (!updatedStudent.getDob().equals(newStudent.getDob())) {
            updatedStudent.setDob(newStudent.getDob());
        }

        if (!updatedStudent.getEmail().equals(newStudent.getEmail())) {
            updatedStudent.setEmail(newStudent.getEmail());
        }

        return repository.save(updatedStudent);
    }

    private Student getStudentById(UUID id) {
        Optional<Student> optionalStudent = repository.findById(id);

        if (optionalStudent.isEmpty()) {
            throw new StudentNotFoundException(String.format(STUDENT_NOT_FOUND, id));
        }

        return optionalStudent.get();
    }

    private Optional<Student> getByEmail(String email) {
        return repository.findStudentByEmail(email);
    }

}
