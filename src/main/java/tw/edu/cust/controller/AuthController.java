package tw.edu.cust.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tw.edu.cust.model.Role;
import tw.edu.cust.model.User;
import tw.edu.cust.security.JwtTokenProvider;
import tw.edu.cust.service.UserService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUsername(loginRequest.getUsername())
                    .or(() -> userService.findByEmail(loginRequest.getUsername()))
                    .orElseThrow();

            String jwt = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
            userService.recordSuccessfulLogin(user.getUsername(), clientIp, userAgent);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("tokenType", "Bearer");
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("department", user.getDepartment());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            userService.recordFailedLogin(loginRequest.getUsername(), clientIp, userAgent);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return userService.findByUsername(authentication.getName())
                .map(user -> ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "fullName", user.getFullName(),
                        "email", user.getEmail(),
                        "role", user.getRole().name(),
                        "department", user.getDepartment() != null ? user.getDepartment() : ""
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
