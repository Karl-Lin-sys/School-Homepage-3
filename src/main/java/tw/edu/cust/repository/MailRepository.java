package tw.edu.cust.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.edu.cust.model.MailMessage;
import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<MailMessage, Long> {
    Page<MailMessage> findByRecipientEmailAndFolderOrderByReceivedAtDesc(String recipientEmail, String folder, Pageable pageable);
    
    Page<MailMessage> findBySenderEmailAndFolderOrderByReceivedAtDesc(String senderEmail, String folder, Pageable pageable);
    
    Page<MailMessage> findByRecipientEmailAndIsStarredTrueOrderByReceivedAtDesc(String recipientEmail, Pageable pageable);
    
    long countByRecipientEmailAndFolderAndIsReadFalse(String recipientEmail, String folder);

    @Query("SELECT m FROM MailMessage m WHERE " +
           "(m.recipientEmail = :email OR m.senderEmail = :email) AND " +
           "(LOWER(m.subject) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.body) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.senderName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY m.receivedAt DESC")
    Page<MailMessage> searchMails(@Param("email") String email, @Param("query") String query, Pageable pageable);
}
