package tw.edu.cust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.edu.cust.model.SecurityIncident;
import java.util.List;

@Repository
public interface SecurityIncidentRepository extends JpaRepository<SecurityIncident, Long> {
    List<SecurityIncident> findByStatusOrderByDetectedAtDesc(String status);
    List<SecurityIncident> findAllByOrderByDetectedAtDesc();
}
