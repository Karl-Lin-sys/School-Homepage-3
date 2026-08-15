package tw.edu.cust.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.edu.cust.model.SecurityIncident;
import tw.edu.cust.repository.SecurityIncidentRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreatDetectionService {

    private final SecurityIncidentRepository incidentRepository;
    private final NistAuditService auditService;

    @Transactional
    public SecurityIncident reportIncident(String title, String description, String incidentType, String severity, String sourceIp, String targetAccount, String mitigationAction) {
        SecurityIncident incident = SecurityIncident.builder()
                .title(title)
                .description(description)
                .incidentType(incidentType)
                .severity(severity)
                .status("OPEN")
                .sourceIp(sourceIp)
                .targetAccount(targetAccount)
                .mitigationAction(mitigationAction)
                .detectedAt(LocalDateTime.now())
                .build();

        SecurityIncident saved = incidentRepository.save(incident);
        auditService.logEvent("THREAT_DETECTED", "DETECT", targetAccount, sourceIp, "SecurityEngine", 
                "Incident ID: " + saved.getId() + " - " + title + " (" + severity + ")", severity.equals("CRITICAL") ? "CRITICAL" : "WARN");
        return saved;
    }

    public List<SecurityIncident> getActiveIncidents() {
        return incidentRepository.findByStatusOrderByDetectedAtDesc("OPEN");
    }

    public List<SecurityIncident> getAllIncidents() {
        return incidentRepository.findAllByOrderByDetectedAtDesc();
    }

    @Transactional
    public SecurityIncident resolveIncident(Long id, String mitigationNote) {
        SecurityIncident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
        incident.setStatus("CLOSED");
        incident.setMitigationAction(mitigationNote);
        incident.setResolvedAt(LocalDateTime.now());
        return incidentRepository.save(incident);
    }
}
