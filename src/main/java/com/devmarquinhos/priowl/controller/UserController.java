package com.devmarquinhos.priowl.controller;

import com.devmarquinhos.priowl.dto.AuthRequest;
import com.devmarquinhos.priowl.dto.UpdatePasswordRequest;
import com.devmarquinhos.priowl.dto.UpdateProfileRequest;
import com.devmarquinhos.priowl.dto.UserProfileResponse;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);

            newUser.setPassword(null);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = userService.authenticate(authRequest.email(), authRequest.password());

            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(@RequestBody UpdateProfileRequest request) {
        try {
            return ResponseEntity.ok(userService.updateMyProfile(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // Você pode melhorar retornando o e.getMessage()
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> updateMyPassword(@RequestBody UpdatePasswordRequest request) {
        try {
            userService.updateMyPassword(request);
            return ResponseEntity.ok("Senha atualizada com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
