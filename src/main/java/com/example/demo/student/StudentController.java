package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@RequestBody Student student) {
        return service.create(student);
    }

    @GetMapping(path = "{id}")
    public Student getById(@PathVariable String id) {
        return service.getById(UUID.fromString(id));
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.deleteById(UUID.fromString(id));
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Student update(@PathVariable String id, @RequestBody Student newStudent) {
        return service.update(UUID.fromString(id), newStudent);
    }
}
