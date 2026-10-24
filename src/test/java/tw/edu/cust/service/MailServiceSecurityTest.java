package tw.edu.cust.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import tw.edu.cust.model.MailMessage;
import tw.edu.cust.repository.MailRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailServiceSecurityTest {
    @Test void cannotReadOrModifyAnotherMailboxCopy() {
        MailRepository repo = mock(MailRepository.class);
        MailService service = new MailService(repo, mock(NistAuditService.class), mock(ThreatDetectionService.class));
        MailMessage mail = MailMessage.builder().id(1L).ownerEmail("owner@example.invalid")
                .senderEmail("sender@example.invalid").recipientEmail("owner@example.invalid").folder("INBOX").build();
        when(repo.findById(1L)).thenReturn(Optional.of(mail));
        assertTrue(service.getMailDetailsAndMarkRead(1L, "other@example.invalid").isEmpty());
        service.toggleStarred(1L, "sender@example.invalid");
        service.moveToTrash(1L, "other@example.invalid");
        assertFalse(mail.isStarred());
        assertEquals("INBOX", mail.getFolder());
        verify(repo, never()).save(any());
    }
    @Test void trashKeepsOriginalMailboxOwner() {
        MailRepository repo = mock(MailRepository.class);
        MailService service = new MailService(repo, mock(NistAuditService.class), mock(ThreatDetectionService.class));
        MailMessage mail = MailMessage.builder().id(1L).ownerEmail("sender@example.invalid")
                .senderEmail("sender@example.invalid").recipientEmail("recipient@example.invalid").folder("SENT").build();
        when(repo.findById(1L)).thenReturn(Optional.of(mail));
        service.moveToTrash(1L, "sender@example.invalid");
        assertEquals("TRASH", mail.getFolder());
        assertEquals("sender@example.invalid", mail.getOwnerEmail());
        assertTrue(service.getMailDetailsAndMarkRead(1L, "recipient@example.invalid").isEmpty());
        assertTrue(service.getMailDetailsAndMarkRead(1L, "sender@example.invalid").isPresent());
    }
}
