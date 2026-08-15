# NIST サイバーセキュリティフレームワーク (CSF 2.0) 準拠仕様書

本ドキュメントは、**中華科技大學（China University of Science and Technology, CUST）** フルスタックウェブシステムにおける **NIST CSF 2.0 (Cybersecurity Framework 2.0)** および関連 NIST スペシャルパブリケーション（**SP 800-177 / SP 800-63B**）への技術的準拠状況を体系的に解説したものです。

---

## 1. NIST CSF 2.0 6大コア機能マッピング

| CSF 2.0 機能 | カテゴリ | CUST システムにおける技術的実装 |
| :--- | :--- | :--- |
| **1. 治理 (GOVERN - GV)** | GV.OC, GV.RM, GV.PO | • システム全体の資安方針を明文化（`NistAuditConfig.java`）<br>• 開発・デプロイにおけるロールベース権限規程（RBAC）<br>• 第三者ライブラリの脆弱性管理（GitHub Actions SAST / Dependency Check） |
| **2. 識別 (IDENTIFY - ID)** | ID.AM, ID.RA, ID.IM | • 資産とエンドポイントの明示的インベントリ管理（Spring Boot Actuator / OpenAPI）<br>• ユーザー身分の一元管理（`User.java`, `UserRepository.java`）<br>• 機密データ資産の分類（公告、個人メール、監査ログ） |
| **3. 防禦 (PROTECT - PR)** | PR.AC, PR.DS, PR.PS | • **身分認証 & アクセス制御 (PR.AC)**: Spring Security 6, JWT, Argon2/BCrypt12 パスワードハッシュ化, NIST SP 800-63B 準拠の複雑性・試行回数ロック<br>• **データセキュリティ (PR.DS)**: TLS 1.3 通信暗号化, Strict HSTS, CSP (Content Security Policy), X-Frame-Options: DENY, SQLi/XSS 完全サニタイズ<br>• **プラットフォーム保護 (PR.PS)**: Nginx WAF レートリミット（DDoS / ブルートフォース緩和） |
| **4. 偵測 (DETECT - DE)** | DE.CM, DE.AE | • **継続的監査ロギング (DE.CM)**: すべてのログイン、権限昇格、ニュース編集、メール送受信を記録（`AuditLog.java`, `NistAuditService.java`）<br>• **異常検知 (DE.AE)**: パスワード総当たり、不審な外部IPからの旧式PHPエンドポイント探測を自動検知（`ThreatDetectionService.java`） |
| **5. 應變 (RESPOND - RS)** | RS.MA, RS.AN, RS.MI | • **インシデント管理 (RS.MA)**: セキュリティダッシュボード（`security.html`）でのインシデント一元可視化<br>• **緩和アクション (RS.MI)**: 不審IPの自動ブラックリスト投入、アカウント一時ロック、緩和記録の監査証跡保持 |
| **6. 復原 (RECOVER - RC)** | RC.RP, RC.IM | • **バックアップ計画 (RC.RP)**: 自動データベースダンプおよびログアーカイブ（`linux/backup.sh`）<br>• **設定のコード化 (RC.IM)**: Docker Compose および systemd による迅速な障害復旧環境（RTO < 15分） |

---

## 2. NIST SP 800-177 (Trustworthy Email) 準拠

キャンパス電子メールシステム（`MailService.java`, `mail.html`）は、NIST SP 800-177 推奨基準を完全に満たしています：

1. **SPF (Sender Policy Framework)**: ドメイン偽装の防止
2. **DKIM (DomainKeys Identified Mail)**: 密碼学的署名による改ざん防止
3. **DMARC (Domain-based Message Authentication, Reporting, and Conformance)**: 送信ドメイン認証の一致検証
4. **TLS 1.3 暗号化**: メール転送経路の完全暗号化（`TLSv1.3_ECDHE_RSA_AES256_GCM_SHA384`）
5. **フィッシング・悪意のある添付ファイルスキャン**: 危険なキーワードや外部リンクを含むメッセージに対するリアルタイム警告シールド

---

## 3. NIST SP 800-63B (Digital Identity Guidelines) 準拠

1. **パスワード強度**: 最低8文字以上、事前定義禁止辞書チェック
2. **ブルートフォース保護**: 連続5回の失敗でアカウント自動一時ロック
3. **セッショントークン管理**: JWT（HMAC-SHA512）、HttpOnly、SameSite=Strict Cookie 運用
