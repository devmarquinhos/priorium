package com.devmarquinhos.priowl.user;

import com.devmarquinhos.priowl.security.TokenService;
import com.devmarquinhos.priowl.subscription.Plan;
import com.devmarquinhos.priowl.subscription.PlanRepository;
import com.devmarquinhos.priowl.subscription.Subscription;
import com.devmarquinhos.priowl.subscription.SubscriptionRepository;
import com.devmarquinhos.priowl.user.dto.UpdatePasswordRequest;
import com.devmarquinhos.priowl.user.dto.UpdateProfileRequest;
import com.devmarquinhos.priowl.user.dto.UserProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       PlanRepository planRepository,
                       SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Este e-mail já foi cadastrado. Tente outro.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        Plan freePlan = planRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Plano Free padrão não encontrado no banco."));

        Subscription subscription = new Subscription();
        subscription.setUser(savedUser);
        subscription.setPlan(freePlan);
        subscription.setStatus("ACTIVE");
        subscriptionRepository.save(subscription);

        return savedUser;
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
        return new UserProfileResponse(user.getId(), user.getRealUsername(), user.getEmail(), user.getIsAdmin());
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

        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getIsAdmin());
    }

    @Transactional
    public void updateMyPassword(UpdatePasswordRequest request) {
        User user = getAuthenticatedUser();

        System.out.println("Senha digitada: " + request.currentPassword());
        System.out.println("Senha do banco: " + user.getPassword());

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("A senha atual informada está incorreta.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteMyAccount() {
        User user = getAuthenticatedUser();
        userRepository.delete(user);
    }

    private void verifyAdmin() {
        User user = getAuthenticatedUser();
        if (!user.getIsAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores.");
        }
    }

    public List<UserProfileResponse> getAllUsersForAdmin() {
        verifyAdmin();
        return userRepository.findAll().stream()
                .map(u -> new UserProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getIsAdmin()))
                .toList();
    }

    public UserProfileResponse getUserByIdForAdmin(Long id) {
        verifyAdmin();
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return new UserProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getIsAdmin());
    }
}
