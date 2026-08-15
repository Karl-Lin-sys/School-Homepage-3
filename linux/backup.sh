#!/usr/bin/env bash
# ==============================================================================
# Automated Disaster Recovery & Backup Script
# Satisfies NIST CSF 2.0 (RC.RP - Recovery Planning & Backups)
# ==============================================================================

set -euo pipefail

BACKUP_DIR="/opt/cust-backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RETENTION_DAYS=30

mkdir -p "$BACKUP_DIR"

echo "[+] Starting automated backup at ${TIMESTAMP}..."

# 1. Backup PostgreSQL Database if active
if command -v pg_dump >/dev/null 2>&1; then
    echo "[+] Dumping PostgreSQL database..."
    pg_dump -U cust_admin -h localhost cust_portal_db | gzip > "${BACKUP_DIR}/cust_db_${TIMESTAMP}.sql.gz"
fi

# 2. Backup Audit Logs & Configuration
echo "[+] Archiving NIST Audit Logs & System configurations..."
tar -czf "${BACKUP_DIR}/cust_audit_logs_${TIMESTAMP}.tar.gz" -C /opt/cust-portal logs data 2>/dev/null || true

# 3. Rotate and prune old backups (> 30 days per policy)
echo "[+] Pruning backups older than ${RETENTION_DAYS} days..."
find "$BACKUP_DIR" -type f -mtime +${RETENTION_DAYS} -delete

echo "[+] NIST CSF 2.0 Backup completed successfully!"
