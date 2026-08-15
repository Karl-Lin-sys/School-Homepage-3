package tw.edu.cust.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String eventType; // LOGIN_SUCCESS, LOGIN_FAILURE, NEWS_CREATE, NEWS_DELETE, MAIL_SEND, ACCESS_DENIED, PRIVILEGE_CHANGE

    @Column(length = 50)
    private String nistCategory; // GOVERN, IDENTIFY, PROTECT, DETECT, RESPOND, RECOVER

    @Column(length = 50)
    private String username;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 255)
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(length = 20)
    @Builder.Default
    private String severity = "INFO"; // INFO, WARN, ERROR, CRITICAL

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
