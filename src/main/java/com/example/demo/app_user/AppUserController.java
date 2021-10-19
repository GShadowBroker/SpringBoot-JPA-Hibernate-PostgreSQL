package com.example.demo.app_user;

import com.example.demo.app_user.app_user_role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class AppUserController {

    @Autowired
    private AppUserService service;

    @GetMapping
    public ResponseEntity<AppUser> getUserByEmail(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok().body(service.getUserByEmail(email));
    }

    @GetMapping("all")
    public ResponseEntity<List<AppUser>> getAll() {
        return ResponseEntity.ok().body(service.getUsers());
    }

    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.saveUser(user));
    }

    @PostMapping("role")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.saveRole(role));
    }

    @PostMapping("grant_role")
    public ResponseEntity<?> addRole(@RequestBody GrantRoleForm form) {
        service.addRoleToUser(form.getEmail(), form.getRoleName());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("refresh_token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
