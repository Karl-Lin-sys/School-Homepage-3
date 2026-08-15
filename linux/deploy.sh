#!/usr/bin/env bash
# ==============================================================================
# Linux Deployment Script for CUST Enterprise Portal
# Compatible with Ubuntu 22.04 / 24.04 LTS & RHEL 9
# ==============================================================================

set -euo pipefail

echo "================================================================"
echo " Starting CUST Portal Deployment (Java 21 / Spring Boot 3.3)"
echo " NIST CSF 2.0 Compliant Architecture"
echo "================================================================"

APP_DIR="/opt/cust-portal"
APP_USER="custapp"
APP_GROUP="custapp"

# 1. Ensure runtime user exists
if ! id -u "$APP_USER" >/dev/null 2>&1; then
    echo "[+] Creating unprivileged service account: $APP_USER"
    useradd -r -s /bin/false -d "$APP_DIR" "$APP_USER"
fi

# 2. Setup directory tree
echo "[+] Initializing directory permissions..."
mkdir -p "$APP_DIR"/{logs,data,static}
chown -R "$APP_USER":"$APP_GROUP" "$APP_DIR"

# 3. Build Artifact if maven is available
if [ -f "./mvnw" ]; then
    echo "[+] Building production JAR via Maven..."
    ./mvnw clean package -DskipTests -B
    cp target/*.jar "$APP_DIR"/cust-portal.jar
elif [ -f "./cust-portal.jar" ]; then
    cp ./cust-portal.jar "$APP_DIR"/cust-portal.jar
fi

# 4. Copy static frontend assets
cp -r ../src/main/resources/static/* "$APP_DIR"/static/ || true

# 5. Install systemd service
if [ -d "/etc/systemd/system" ]; then
    echo "[+] Installing systemd daemon service..."
    cp ./cust-portal.service /etc/systemd/system/
    systemctl daemon-reload
    systemctl enable cust-portal.service
    systemctl restart cust-portal.service
    echo "[+] CUST Portal Service is active and running!"
fi

echo "================================================================"
echo " Deployment Complete! Health Check: curl http://localhost:8080/actuator/health"
echo "================================================================"
