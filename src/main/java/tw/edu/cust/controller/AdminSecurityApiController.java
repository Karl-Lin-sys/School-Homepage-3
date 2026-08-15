package tw.edu.cust.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.edu.cust.model.AuditLog;
import tw.edu.cust.model.SecurityIncident;
import tw.edu.cust.model.User;
import tw.edu.cust.service.NistAuditService;
import tw.edu.cust.service.ThreatDetectionService;
import tw.edu.cust.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminSecurityApiController {

    private final NistAuditService auditService;
    private final ThreatDetectionService threatService;
    private final UserService userService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getSecurityMetrics() {
        Map<String, Object> metrics = auditService.getSecurityMetrics();
        metrics.put("activeIncidentsCount", threatService.getActiveIncidents().size());
        metrics.put("nistCsfVersion", "2.0 (Govern, Identify, Protect, Detect, Respond, Recover)");
        metrics.put("sp800177EmailSecurity", "ENFORCED (SPF/DKIM/DMARC/TLS1.3)");
        metrics.put("sp80063bAuthPolicy", "COMPLIANT (Argon2/BCrypt12)");
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditLogs(page, size));
    }

    @GetMapping("/audit-logs/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogs() {
        return ResponseEntity.ok(auditService.getRecentLogs());
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<SecurityIncident>> getAllIncidents() {
        return ResponseEntity.ok(threatService.getAllIncidents());
    }

    @PostMapping("/incidents/{id}/resolve")
    public ResponseEntity<SecurityIncident> resolveIncident(@PathVariable Long id, @RequestBody ResolveIncidentRequest req) {
        return ResponseEntity.ok(threatService.resolveIncident(id, req.getMitigationNote()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Data
    public static class ResolveIncidentRequest {
        private String mitigationNote;
    }
}
