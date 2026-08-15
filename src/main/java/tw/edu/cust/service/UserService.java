package tw.edu.cust.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.edu.cust.model.Role;
import tw.edu.cust.model.User;
import tw.edu.cust.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NistAuditService auditService;
    private final ThreatDetectionService threatService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User registerUser(String username, String fullName, String email, String plainPassword, Role role, String department, String clientIp) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // NIST SP 800-63B Password Validation
        validatePasswordPolicy(plainPassword);

        User user = User.builder()
                .username(username)
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(plainPassword))
                .role(role)
                .department(department)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        auditService.logEvent("USER_CREATED", "IDENTIFY", username, clientIp, null, 
                "User registered with role: " + role.name() + ", department: " + department, "INFO");
        return saved;
    }

    @Transactional
    public void recordSuccessfulLogin(String username, String clientIp, String userAgent) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(clientIp);
            userRepository.save(user);
            auditService.logEvent("LOGIN_SUCCESS", "PROTECT", username, clientIp, userAgent, "Successful authentication", "INFO");
        });
    }

    @Transactional
    public void recordFailedLogin(String username, String clientIp, String userAgent) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setAccountNonLocked(false);
                auditService.logEvent("ACCOUNT_LOCKED", "PROTECT", username, clientIp, userAgent, "Account locked due to 5 consecutive failed attempts", "CRITICAL");
                threatService.reportIncident("Brute Force Lockout: " + username, "User account temporarily locked due to multiple invalid credentials.", "BRUTE_FORCE", "HIGH", clientIp, username, "Auto-lock account & alert administrator");
            } else {
                auditService.logEvent("LOGIN_FAILURE", "PROTECT", username, clientIp, userAgent, "Failed password attempt #" + attempts, "WARN");
            }
            userRepository.save(user);
        }, () -> {
            auditService.logEvent("LOGIN_FAILURE_UNKNOWN_USER", "PROTECT", username, clientIp, userAgent, "Failed login attempt for non-existent username", "WARN");
        });
    }

    private void validatePasswordPolicy(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long per NIST SP 800-63B.");
        }
    }
}
