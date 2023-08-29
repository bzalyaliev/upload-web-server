package com.github.bzalyaliev.uploadwebserver.controller.security;

import com.github.bzalyaliev.uploadwebserver.model.Users;
import com.github.bzalyaliev.uploadwebserver.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    @Autowired
    private UsersRepository userRepository;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody Map<String, String> registrationInfo) {
        String username = registrationInfo.get("username");
        String role = "USER";

        if ("admin".equalsIgnoreCase(username)) {
            role = "ADMIN";
        }

        if (username != null && !username.isEmpty()) {
            Users user = new Users();
            user.setUsername(username);
            user.setRoles(role);

            userRepository.save(user);

            return ResponseEntity.ok("Registration successful: " + username);
        } else {
            return ResponseEntity.badRequest().body("Invalid username.");
        }
    }
}
