# ==============================================================================
# PowerShell Script to Push CUST Full-Stack System to GitHub Repository
# Target: https://github.com/Karl-Lin-sys/School-Homepage-3.git
# ==============================================================================

$RepoUrl = "https://github.com/Karl-Lin-sys/School-Homepage-3.git"

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " Preparing to push CUST Full-Stack Project to GitHub" -ForegroundColor Cyan
Write-Host " Repository: $RepoUrl" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

# 1. Initialize Git if needed
if (-not (Test-Path ".git")) {
    Write-Host "[+] Initializing Git repository..." -ForegroundColor Green
    git init
    git branch -M main
}

# 2. Add Remote Origin
$existingRemote = git remote get-url origin 2>$null
if ($null -eq $existingRemote) {
    Write-Host "[+] Adding remote origin: $RepoUrl" -ForegroundColor Green
    git remote add origin $RepoUrl
} else {
    Write-Host "[+] Updating remote origin to: $RepoUrl" -ForegroundColor Green
    git remote set-url origin $RepoUrl
}

# 3. Add and Commit
Write-Host "[+] Staging all source files..." -ForegroundColor Green
git add -A

Write-Host "[+] Committing files..." -ForegroundColor Green
git commit -m "feat: complete enterprise full-stack CUST portal with news CMS, secure webmail and NIST CSF 2.0 compliance"

# 4. Push to Remote
Write-Host "[+] Pushing to GitHub main branch..." -ForegroundColor Green
Write-Host "Note: If prompted for credentials, please enter your GitHub Personal Access Token." -ForegroundColor Yellow

git push -u origin main --force

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " Upload to GitHub Complete!" -ForegroundColor Cyan
Write-Host " View online: https://github.com/Karl-Lin-sys/School-Homepage-3" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
