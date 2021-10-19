package com.example.demo.app_user;

import com.example.demo.app_user.app_user_role.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface AppUserService {

    AppUser saveUser(AppUser user);

    AppUser getUserByEmail(String email);

    List<AppUser> getUsers();

    Role saveRole(Role role);

    Role getRoleByName(String name);

    void addRoleToUser(String email, String roleName);

    void removeRoleFromUser(String email, String roleName);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

