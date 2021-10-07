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

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Student create(Student student) {

        Optional<Student> studentOptional = getByEmail(student.getEmail());

        if (studentOptional.isPresent()) {
            throw new StudentNotFoundException("Email already taken");
        }

        return repository.save(student);
    }

    public Student getById(UUID id) {

        return repository.findById(id).orElseThrow(() -> {
            throw new StudentNotFoundException("Student not found");
        });
    }

    public void deleteById(UUID id) {

        if (!repository.existsById(id)) {
            throw new StudentNotFoundException("Student not found");
        }

        repository.deleteById(id);
    }

    @Transactional
    public Student update(UUID id, Student newStudent) {

        return repository.findById(id).map(student -> {

            student.setName(newStudent.getName());
            student.setDob(newStudent.getDob());
            student.setEmail(newStudent.getEmail());

            return repository.save(student);

        }).orElseThrow(() -> {
            throw new StudentNotFoundException("Student not found");
        });

    }

    private Optional<Student> getByEmail(String email) {
        return repository.findStudentByEmail(email);
    }

}