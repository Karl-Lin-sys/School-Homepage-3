package tw.edu.cust.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String incidentType; // BRUTE_FORCE, SUSPICIOUS_IP, PHISHING_ATTEMPT, PRIVILEGE_ESCALATION

    @Column(length = 20)
    @Builder.Default
    private String severity = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(length = 20)
    @Builder.Default
    private String status = "OPEN"; // OPEN, INVESTIGATING, MITIGATED, CLOSED

    @Column(length = 50)
    private String sourceIp;

    @Column(length = 50)
    private String targetAccount;

    private String mitigationAction;

    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;
}
