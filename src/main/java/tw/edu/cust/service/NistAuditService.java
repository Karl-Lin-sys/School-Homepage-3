package tw.edu.cust.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.edu.cust.model.AuditLog;
import tw.edu.cust.repository.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NistAuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog logEvent(String eventType, String nistCategory, String username, String ipAddress, String userAgent, String details, String severity) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .nistCategory(nistCategory)
                .username(username != null ? username : "ANONYMOUS")
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .userAgent(userAgent != null ? userAgent : "Unknown-Client")
                .details(details)
                .severity(severity != null ? severity : "INFO")
                .timestamp(LocalDateTime.now())
                .build();

        log.info("[NIST-CSF-AUDIT] [{}] [{}] user={} ip={} severity={}", nistCategory, eventType, username, ipAddress, severity);
        return auditLogRepository.save(auditLog);
    }

    public Page<AuditLog> getAuditLogs(int page, int size) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop20ByOrderByTimestampDesc();
    }

    public Map<String, Object> getSecurityMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalLogs", auditLogRepository.count());
        metrics.put("loginSuccessCount", auditLogRepository.countByEventType("LOGIN_SUCCESS"));
        metrics.put("loginFailureCount", auditLogRepository.countByEventType("LOGIN_FAILURE"));
        metrics.put("criticalEventsCount", auditLogRepository.countBySeverity("CRITICAL"));
        metrics.put("warningEventsCount", auditLogRepository.countBySeverity("WARN"));
        return metrics;
    }
}
