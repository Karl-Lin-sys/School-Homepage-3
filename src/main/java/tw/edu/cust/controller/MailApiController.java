package tw.edu.cust.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.edu.cust.model.MailMessage;
import tw.edu.cust.model.User;
import tw.edu.cust.service.MailService;
import tw.edu.cust.service.UserService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MailApiController {

    private final MailService mailService;
    private final UserService userService;

    private String resolveUserEmail(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return "admin@cust.edu.tw"; // default for simulation/demo
        }
        return userService.findByUsername(auth.getName())
                .map(User::getEmail)
                .orElse(auth.getName().contains("@") ? auth.getName() : auth.getName() + "@cust.edu.tw");
    }

    @GetMapping("/folder/{folder}")
    public ResponseEntity<Page<MailMessage>> getFolderMails(
            @PathVariable String folder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Authentication auth) {
        String email = resolveUserEmail(auth);
        String upperFolder = folder.toUpperCase();

        Page<MailMessage> result;
        switch (upperFolder) {
            case "SENT":
                result = mailService.getSent(email, page, size);
                break;
            case "STARRED":
                result = mailService.getStarred(email, page, size);
                break;
            case "DRAFTS":
                result = mailService.getDrafts(email, page, size);
                break;
            case "TRASH":
                result = mailService.getTrash(email, page, size);
                break;
            case "INBOX":
            default:
                result = mailService.getInbox(email, page, size);
                break;
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
        String email = resolveUserEmail(auth);
        long count = mailService.getUnreadCount(email);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<MailMessage> getMailDetail(@PathVariable Long id, Authentication auth) {
        String email = resolveUserEmail(auth);
        return mailService.getMailDetailsAndMarkRead(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/send")
    public ResponseEntity<MailMessage> sendMail(@RequestBody SendMailRequest req, HttpServletRequest request, Authentication auth) {
        String clientIp = request.getRemoteAddr();
        String senderEmail = resolveUserEmail(auth);
        String senderName = auth != null ? auth.getName() : "CUST User";

        MailMessage sent = mailService.sendMail(
                senderEmail,
                senderName,
                req.getRecipientEmail(),
                req.getRecipientName(),
                req.getSubject(),
                req.getBody(),
                req.isHasAttachment(),
                req.getAttachmentName(),
                req.getAttachmentSize(),
                clientIp
        );
        return ResponseEntity.ok(sent);
    }

    @PostMapping("/message/{id}/star")
    public ResponseEntity<?> toggleStarred(@PathVariable Long id) {
        mailService.toggleStarred(id);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @DeleteMapping("/message/{id}")
    public ResponseEntity<?> moveToTrash(@PathVariable Long id) {
        mailService.moveToTrash(id);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @Data
    public static class SendMailRequest {
        private String recipientEmail;
        private String recipientName;
        private String subject;
        private String body;
        private boolean hasAttachment;
        private String attachmentName;
        private String attachmentSize;
    }
}
