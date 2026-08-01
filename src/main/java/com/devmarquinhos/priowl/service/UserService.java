package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.dto.UpdatePasswordRequest;
import com.devmarquinhos.priowl.dto.UpdateProfileRequest;
import com.devmarquinhos.priowl.dto.UserProfileResponse;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Este e-mail já foi cadastrado. Tente outro.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        return tokenService.generateToken(user);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public UserProfileResponse getMyProfile() {
        User user = getAuthenticatedUser();
        return new UserProfileResponse(user.getId(), user.getRealUsername(), user.getEmail(), user.getIsAdmin().toString());
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();

        if (!user.getEmail().equals(request.email()) && userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso por outra conta.");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        userRepository.save(user);

        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getIsAdmin().toString());
    }

    @Transactional
    public void updateMyPassword(UpdatePasswordRequest request) {
        User user = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("A senha atual informada está incorreta.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
