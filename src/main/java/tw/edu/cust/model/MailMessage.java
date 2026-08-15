package tw.edu.cust.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mail_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String senderEmail;

    @Column(length = 100)
    private String senderName;

    @Column(nullable = false, length = 100)
    private String recipientEmail;

    @Column(length = 100)
    private String recipientName;

    @Column(length = 255)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(length = 20)
    @Builder.Default
    private String folder = "INBOX"; // INBOX, SENT, DRAFTS, STARRED, TRASH

    private boolean isRead = false;
    private boolean isStarred = false;
    private boolean hasAttachment = false;

    @Column(length = 255)
    private String attachmentName;

    @Column(length = 50)
    private String attachmentSize;

    // NIST SP 800-177 Email Security & Authentication Attributes
    @Column(length = 20)
    @Builder.Default
    private String spfStatus = "PASS"; // PASS, SOFTFAIL, FAIL, NONE

    @Column(length = 20)
    @Builder.Default
    private String dkimStatus = "PASS"; // PASS, FAIL, NONE

    @Column(length = 20)
    @Builder.Default
    private String dmarcStatus = "PASS"; // PASS, FAIL, NONE

    @Column(length = 50)
    @Builder.Default
    private String tlsEncryption = "TLSv1.3_ECDHE_RSA_AES256_GCM_SHA384";

    @Column(length = 20)
    @Builder.Default
    private String threatLevel = "CLEAN"; // CLEAN, LOW_RISK, SUSPICIOUS, BLOCKED_PHISHING

    @Column(length = 255)
    private String securityNotice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();
}
