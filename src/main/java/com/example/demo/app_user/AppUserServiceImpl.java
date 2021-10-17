package com.example.demo.app_user;

import com.example.demo.app_user.app_user_role.Role;
import com.example.demo.app_user.app_user_role.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static final String EMAIL_NOT_FOUND_MESSAGE = "Email '%s' not found";
    private static final String ROLE_NOT_FOUND_MESSAGE = "Role '%s' not found";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("saving user to db...");
        return userRepository.save(user);
    }

    @Override
    public AppUser getUserByEmail(String email) {
        log.info(String.format("getting user by email '%s'...", email));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(
                            String.format(EMAIL_NOT_FOUND_MESSAGE, email)
                    );
                });
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("getting all users...");
        return userRepository.findAll();
    }

    @Override
    public Role saveRole(Role role) {
        log.info("saving role to db...");
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleByName(String name) {
        log.info(String.format("getting role by name '%s'...", name));
        return roleRepository.getRoleByName(name)
                .orElseThrow(() -> {
                    throw new RuntimeException( // TODO make custom exception
                            String.format(ROLE_NOT_FOUND_MESSAGE, name)
                    );
                });
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        log.info("adding role to user...");

        AppUser user = getUserByEmail(email);
        Role role = getRoleByName(roleName);

        user.getRoles().add(role);
    }

    @Override
    public void removeRoleFromUser(String email, String roleName) {
        log.info("removing role from user");

        AppUser user = getUserByEmail(email);
        Role role = getRoleByName(roleName);

        user.getRoles().remove(role);
    }
}
