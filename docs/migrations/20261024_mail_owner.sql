-- Apply through your normal deployment process before using ddl-auto=validate.
ALTER TABLE mail_messages ADD COLUMN owner_email VARCHAR(100);
UPDATE mail_messages SET owner_email = sender_email WHERE folder IN ('SENT', 'DRAFTS');
UPDATE mail_messages SET owner_email = recipient_email WHERE folder = 'INBOX';
-- Old TRASH rows have ambiguous ownership. Keep inaccessible until ownership is verified.
