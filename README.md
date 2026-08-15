# 中華科技大學 (CUST) 新世代企業級全端門戶系統
### China University of Science and Technology - Enterprise Full-Stack Web Platform

[![NIST CSF 2.0](https://img.shields.io/badge/Security-NIST%20CSF%202.0%20Compliant-success?style=for-the-badge&logo=shield)](docs/NIST_COMPLIANCE.md)
[![Java 21 LTS](https://img.shields.io/badge/Java-21%20LTS-orange?style=for-the-badge&logo=java)](https://openjdk.org/)
[![Spring Boot 3.3](https://img.shields.io/badge/Framework-Spring%20Boot%203.3-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![WCAG 2.1 AA](https://img.shields.io/badge/Accessibility-WCAG%202.1%20AA-blue?style=for-the-badge)](https://www.w3.org/WAI/standards-guidelines/wcag/)
[![Dual Runtime](https://img.shields.io/badge/Runtime-Java%20%26%20Node.js-blueviolet?style=for-the-badge)](server.js)

---

## 📖 專案簡介 (Project Overview)

本專案針對 **中華科技大學（China University of Science and Technology, CUST - https://www.cust.edu.tw ）** 現行網站架構進行了全面性重構與現代化升級。

全面導入當前 IT 業界最佳實踐（Best Practices）、美國國家標準技術研究院 **NIST 網路安全框架 (CSF 2.0)**、**NIST SP 800-177 (可信電子郵件標準)** 與 **NIST SP 800-63B (數位身分認證指引)**，以 **Java 21 LTS / Spring Boot 3.3** 與 **Linux 原生容器化架構** 為核心，提供集 **大學入口門戶、新聞公告內容管理系統 (CMS)、高安全性校園 Webmail 與 NIST SOC 資安監控儀表板** 於一體之現代化全端解決方案。

---

## ✨ 核心特色與架構優勢 (Key Features)

### 1. 現代化校園入口門戶 (Campus Portal)
- **品牌識別與設計系統**: 採用 CUST 專屬群青深藍 (`#0A2540`) 與榮耀金 (`#C5A059`)，結合流暢微動畫與響應式格線。
- **無障礙輔助 (WCAG 2.1 AA)**: 支援高對比度切換、動態字體放大縮小 (A+/A-)、深淺主題模式與鍵盤導航。
- **三大校區與學術單位導覽**: 台北南港校本部、新竹航空學院基地（全台唯一歐盟 EASA 認證）、雲林生技產學園區之完整介紹與乘車指引。

### 2. 全功能新聞與公告 CMS (News & Notices Hub)
- **多維度分類**: 重要公告、學術活動、招生訊息、獎助學金、校園活動、研發產學。
- **即時搜尋與篩選**: 支援全文模糊檢索、置頂標記 (Pin to top)、點閱計數器與官方附件下載。
- **身分權限控管**: 教職員與管理員可在前台直接調用安全 REST API 發布及維護公告。

### 3. 可信校園電子郵件系統 (CUST Secure Webmail)
- **NIST SP 800-177 合規認證**: 內建雙向 **SPF / DKIM / DMARC** 數位簽名驗證與 **TLS 1.3** 傳輸加密標章。
- **信件全生命週期管理**: 收件匣、已加星號、寄件備份、草稿匣、垃圾桶、未讀計數與附件即時防毒檢測。
- **釣魚防護盾**: 自動標註與隔離可疑外部寄件者與高風險敏感詞彙。

### 4. NIST CSF 2.0 資安防護中心 (Security SOC Dashboard)
- **六大防護維度即時可視化**: 治理 (Govern)、識別 (Identify)、防禦 (Protect)、偵測 (Detect)、應變 (Respond)、復原 (Recover)。
- **實時稽核軌跡 (Audit Trails)**: 記錄所有身分認證、公告發布、郵件外發與權限變更，不可竄改。
- **威脅偵測與處置**: 自動偵測異常探測與暴力破解行為，提供一鍵處置與緩解紀錄存證。

---

## 🚀 快速啟動指南 (Quick Start)

### 方式一：Node.js 原生高速啟動 (無須配置 Java 環境即可即時預覽)
```bash
# 啟動雙核心伺服器
npm start
# 或
node server.js
```
啟動後於瀏覽器開啟：`http://localhost:8080`

### 方式二：Docker Compose 企業級一鍵容器化部署 (Nginx + App + PostgreSQL + Mailhog)
```bash
cd docker
docker compose up -d --build
```

### 方式三：Java 21 / Maven 原生編譯
```bash
./mvnw clean spring-boot:run
```

---

## 🔑 預設測試帳號 (Default Credentials)

| 角色 | 使用者名稱 (Username) | 預設密碼 (Password) | 權限範圍 |
| :--- | :--- | :--- | :--- |
| **超級管理員 (Super Admin)** | `admin` | `CustAdmin2026!` | 具備全部後台、資安 SOC 監控、用戶與公告管理權限 |
| **專任教師 (Faculty)** | `professor_chen` | `ChenProf2026!` | 具備公告發布、學院專區與個人安全信箱權限 |
| **在校學生 (Student)** | `student_lin` | `LinStudent2026!` | 具備新聞查閱、校園信箱收發與選課查詢權限 |

---

## 📂 專案目錄結構 (Directory Tree)

```
school-homepage-3/
├── pom.xml                               # Java 21 / Spring Boot 3.3 Maven 依賴配置
├── package.json                          # Node.js 雙核心環境配置
├── server.js                             # 高性能 Node.js 原生 API 伺服器 (即時運行引擎)
├── src/
│   ├── main/
│   │   ├── java/tw/edu/cust/
│   │   │   ├── CustPortalApplication.java
│   │   │   ├── config/ (SecurityConfig, WebConfig)
│   │   │   ├── controller/ (AuthController, NewsApiController, MailApiController, AdminSecurityApiController)
│   │   │   ├── model/ (User, Role, News, NewsCategory, MailMessage, AuditLog, SecurityIncident)
│   │   │   ├── repository/ (UserRepository, NewsRepository, MailRepository, AuditLogRepository, SecurityIncidentRepository)
│   │   │   ├── service/ (UserService, NewsService, MailService, NistAuditService, ThreatDetectionService)
│   │   │   └── security/ (JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService)
│   │   └── resources/
│   │       ├── application.yml / application-prod.yml
│   │       └── static/                   # 現代化前端資產 (WCAG 2.1 AA)
│   │           ├── index.html            # CUST 官方首頁
│   │           ├── news.html             # 新聞公告 CMS 中心
│   │           ├── mail.html             # 校園 Webmail (NIST SP 800-177)
│   │           ├── security.html         # NIST CSF 2.0 資安監控中心
│   │           ├── about.html            # 學校歷史與沿革
│   │           ├── academics.html        # 學院系所導覽
│   │           ├── campus.html           # 三大校區交通指南
│   │           ├── css/ (main.css, portal.css, news.css, mail.css, security.css)
│   │           └── js/ (app.js, news.js, mail.js, security.js)
├── docker/
│   ├── Dockerfile                        # Multi-Stage Java 21 LTS 構建檔
│   ├── docker-compose.yml                # PostgreSQL, Nginx, App, Mailhog 容器整合
│   └── nginx/nginx.conf                  # Nginx WAF / SSL / レートリミット / 嚴格安全標頭
├── linux/
│   ├── cust-portal.service               # Linux systemd 系統守護進程配置
│   ├── deploy.sh                         # Linux 自動化部署腳本
│   └── backup.sh                         # 自動化災害復原與備份腳本 (NIST RC.RP)
├── docs/
│   ├── NIST_COMPLIANCE.md                # NIST CSF 2.0 / SP 800-177 / SP 800-63B 完整規範對照表
│   ├── ARCHITECTURE.md                   # 系統架構與 REST API 規格書
│   └── DEPLOYMENT.md                     # Linux / 容器化環境部署手冊
├── .github/
│   └── workflows/ci-cd.yml               # GitHub Actions CI/CD 自動化測試與安全掃描流程
├── upload_to_github.ps1                  # GitHub 上傳自動化腳本 (Windows PowerShell)
└── upload_to_github.sh                   # GitHub 上傳自動化腳本 (Linux/macOS Bash)
```

---

## 🛡️ 資訊安全宣告 (Security & Compliance)

本系統全面導入 NIST CSF 2.0 框架，並實施以下防禦機制：
1. **HTTP 嚴格傳輸安全 (HSTS)**: 強制使用 TLS 1.3 通訊。
2. **內容安全策略 (CSP)**: 防止 XSS 跨站腳本與資料注入攻擊。
3. **框架選項 (X-Frame-Options: DENY)**: 阻絕點擊劫持 (Clickjacking)。
4. **率限制保護 (Rate Limiting)**: 防止 DDoS 與密碼暴力探測。
5. **完整稽核日誌 (Audit Trails)**: 保留不可否認性之系統活動記錄。

---

## 📄 開源授權 (License)

Copyright &copy; 2026 China University of Science and Technology (CUST). Released under the [MIT License](LICENSE).
