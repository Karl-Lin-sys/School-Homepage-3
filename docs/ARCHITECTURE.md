# システムアーキテクチャ設計書 (System Architecture)

## 1. 全体構造図

```
[ Clients: Web / Mobile / Tablet ]
           │ (HTTPS / WSS / TLS 1.3)
           ▼
[ Nginx Reverse Proxy / WAF ] 
   ├─ Rate Limiting (DDoS Defense)
   ├─ Security Headers (Strict CSP, HSTS, X-Frame-Options)
   └─ TLS Offloading
           │
           ▼
[ CUST Enterprise Application Layer ]
   ├─ Spring Security 6 / RBAC Filter
   ├─ REST API Controllers (/api/auth, /api/news, /api/mail, /api/security)
   ├─ Service Business Logic (News CMS, Secure Mail Engine, NIST Audit, Threat Scanner)
   └─ Spring Data JPA / Hibernate ORM
           │
           ▼
[ Database & Persistence Layer ]
   ├─ PostgreSQL 16 (Production) / H2 In-Memory (Dev/Testing)
   └─ Backup Archive (/opt/cust-backups)
```

## 2. データベースモデル設計 (Entity Relationships)

- **User**: ID, Username, FullName, Email, PasswordHash, Role, Department, FailedAttempts, Enabled, Locked, Timestamps
- **NewsCategory**: ID, Code, NameZh, NameEn, BadgeColor, DisplayOrder
- **News**: ID, Title, Content, Summary, CategoryID, AuthorDept, AuthorName, Pinned, Published, ViewCount, AttachmentName, AttachmentUrl, Timestamps
- **MailMessage**: ID, SenderEmail, SenderName, RecipientEmail, RecipientName, Subject, Body, Folder, IsRead, IsStarred, HasAttachment, SPFStatus, DKIMStatus, DMARCStatus, TLSEncryption, ThreatLevel, SecurityNotice, ReceivedAt
- **AuditLog**: ID, EventType, NISTCategory, Username, IPAddress, UserAgent, Details, Severity, Timestamp
- **SecurityIncident**: ID, Title, Description, IncidentType, Severity, Status, SourceIP, TargetAccount, MitigationAction, Timestamps

## 3. REST API 仕様一覧

| エンドポイント | メソッド | 概要 | 認証要件 |
| :--- | :--- | :--- | :--- |
| `/api/auth/login` | POST | ユーザー認証 & JWT発行 | Public |
| `/api/auth/me` | GET | 現在のログインユーザー情報取得 | Authenticated |
| `/api/categories` | GET | ニュースカテゴリ一覧取得 | Public |
| `/api/news` | GET | ニュース一覧・検索・カテゴリ絞り込み | Public |
| `/api/news/{id}` | GET | ニュース詳細取得 & 閲覧数インクリメント | Public |
| `/api/news` | POST | ニュース新規投稿 | Admin / Faculty |
| `/api/mail/folder/{folder}` | GET | 指定フォルダのメール一覧取得 | Authenticated |
| `/api/mail/send` | POST | 暗号化メール送信 (NIST SP 800-177) | Authenticated |
| `/api/security/metrics` | GET | NIST CSF 2.0 防護統計・指標取得 | Public / Admin |
| `/api/security/audit-logs` | GET | リアルタイム監査ログストリーム | Admin |
| `/api/security/incidents` | GET | セキュリティインシデント一覧 | Admin |
| `/api/security/incidents/{id}/resolve` | POST | インシデントの緩和および結案 | Admin |
