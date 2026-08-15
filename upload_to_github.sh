#!/usr/bin/env bash
# ==============================================================================
# Bash Script to Push CUST Full-Stack System to GitHub Repository
# Target: https://github.com/Karl-Lin-sys/School-Homepage-3.git
# ==============================================================================

set -e

REPO_URL="https://github.com/Karl-Lin-sys/School-Homepage-3.git"

echo "================================================================"
echo " Preparing to push CUST Full-Stack Project to GitHub"
echo " Repository: ${REPO_URL}"
echo "================================================================"

if [ ! -d ".git" ]; then
    echo "[+] Initializing Git repository..."
    git init
    git branch -M main
fi

if git remote | grep -q origin; then
    echo "[+] Updating remote origin to: ${REPO_URL}"
    git remote set-url origin "${REPO_URL}"
else
    echo "[+] Adding remote origin: ${REPO_URL}"
    git remote add origin "${REPO_URL}"
fi

echo "[+] Staging all source files..."
git add -A

echo "[+] Committing files..."
git commit -m "feat: complete enterprise full-stack CUST portal with news CMS, secure webmail and NIST CSF 2.0 compliance" || true

echo "[+] Pushing to GitHub main branch..."
git push -u origin main --force

echo "================================================================"
echo " Upload to GitHub Complete!"
echo " View online: https://github.com/Karl-Lin-sys/School-Homepage-3"
echo "================================================================"
