# Security configuration

This is a local demonstration. Seeded names, addresses, mail, security metrics and compliance labels are simulated; they are not evidence of actual institutional security or delivered email.

The Node server binds to 127.0.0.1 by default. Configure unique PORTAL_ADMIN_PASSWORD, PORTAL_FACULTY_PASSWORD and PORTAL_STUDENT_PASSWORD values (at least 16 characters) to enable those demonstration accounts. No public default password is accepted. Sessions expire after one hour and are lost on restart. Run behind HTTPS and a production identity provider before internet deployment.

For Java, provide DB_PASSWORD and a randomly generated JWT_SECRET with at least 64 bytes. Seed accounts are off unless app.demo.seed=true and all three account passwords are configured. Existing databases may still contain old seed accounts: disable those accounts or reset their passwords and rotate any previously used JWT secret. Source changes do not update deployed accounts or infrastructure.

The code changes restrict API access and individual mail ownership, bound Node JSON bodies, and escape displayed text. They do not certify production readiness. Java changes require a Java 21/Maven build in CI; the local audit environment has no Java compiler.

Existing PostgreSQL installations must apply docs/migrations/20261024_mail_owner.sql before deployment. Back up the database first. The source audit does not run this migration. Old TRASH records remain inaccessible until their mailbox owner can be verified. A mail copy retains its owner when moved to trash; being its sender or recipient alone does not grant access to the other mailbox copy.
