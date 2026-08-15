package tw.edu.cust.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.edu.cust.model.AuditLog;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
    
    List<AuditLog> findTop20ByOrderByTimestampDesc();
    
    long countByEventType(String eventType);
    
    long countBySeverity(String severity);
}
