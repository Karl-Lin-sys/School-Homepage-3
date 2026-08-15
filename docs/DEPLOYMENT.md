# Linux / Java 環境デプロイメントガイド (Deployment Guide)

本システムは、**Linux (Ubuntu 22.04/24.04 LTS, RHEL 9, Debian 12)** および **Java 21 LTS** 環境での本番運用を前提に最適化されています。

---

## 1. 前提条件 (Prerequisites)

- **OS**: Linux (Ubuntu 22.04 LTS 推奨) または Windows (開発用)
- **Java**: OpenJDK / Eclipse Temurin 21 LTS
- **Database**: PostgreSQL 16+ (または H2 開発用組み込みDB)
- **Container**: Docker 24+ & Docker Compose v2+
- **Reverse Proxy**: Nginx 1.24+

---

## 2. デプロイ方法 A: Docker Compose による一括起動 (推奨)

最も迅速かつ安全に全コンポーネント（Nginx + Spring Boot App + PostgreSQL + Mailhog）を立ち上げる方法です：

```bash
# 1. リポジトリのクローン
git clone https://github.com/Karl-Lin-sys/School-Homepage-3.git
cd School-Homepage-3

# 2. Docker Compose によるビルド & 起動
cd docker
docker compose up -d --build

# 3. ログ確認
docker compose logs -f app
```

ブラウザで `http://localhost` (または設定したドメイン) にアクセスすると、CUST ポータルが稼働します。

---

## 3. デプロイ方法 B: Linux ホストへのネイティブデプロイ (systemd)

```bash
# 1. ビルドスクリプトの実行権限付与
chmod +x ./mvnw ./linux/deploy.sh ./linux/backup.sh

# 2. 自動デプロイスクリプトの実行
sudo ./linux/deploy.sh

# 3. サービス状態確認
sudo systemctl status cust-portal.service
```

---

## 4. デプロイ方法 C: Node.js デュアルエンジンによる即時ローカル実行

Node.js 環境があれば、Java/Mavenのインストールなしに即座に全機能が動作します：

```bash
npm start
# http://localhost:8080 で即座にアクセス可能
```

---

## 5. 初期認証アカウント

- **システム管理者 (Super Admin)**:
  - ユーザー名: `admin`
  - 密碼: `CustAdmin2026!`
- **教職員アカウント (Faculty)**:
  - ユーザー名: `professor_chen`
  - 密碼: `ChenProf2026!`
- **学生アカウント (Student)**:
  - ユーザー名: `student_lin`
  - 密碼: `LinStudent2026!`
