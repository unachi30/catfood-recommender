# Cat Food Recommender

貓罐頭比較網站（Vue 3 + Spring Boot + MySQL）

## 結構

```
catfood-recommender/
├── backend/             # Spring Boot 後端
├── frontend/            # Vue 3 前端
├── docker-compose.yml   # MySQL + 後端 + 前端（一鍵啟動）
├── init.sql             # 選用手動建庫說明（非必要）
└── scripts/             # 本機開發腳本
```

## 一鍵啟動（Docker，推薦給面試官）

需安裝 [Docker Desktop](https://www.docker.com/products/docker-desktop/)。

```sh
docker compose up --build
```

首次建置會下載映像並編譯，約需數分鐘。完成後：

| 服務 | 網址 |
|------|------|
| 網站 | http://localhost:5173 |
| Swagger | http://localhost:8080/swagger-ui.html |
| MySQL（選用，DBeaver） | `localhost:3307`（root / admin123，庫名 `catfood`） |

> MySQL 對外使用 **3307**，避免與本機已安裝的 MySQL（3306）衝突。容器內後端仍透過內部網路連 `mysql:3306`，不受影響。

停止服務：

```sh
docker compose down
```

清除資料庫並重來（會刪除所有商品與帳號外的持久化資料）：

```sh
docker compose down -v
docker compose up --build
```

## 本機開發（熱重載）

### 1. MySQL

```sh
docker compose up -d mysql
```

### 2. 後端

```sh
scripts\dev-backend.bat
```

或 `cd backend && ./mvnw spring-boot:run`

### 3. 前端

```sh
cd frontend
npm install
npm run dev
```

### Windows 一鍵開前後端

```sh
deploy-all.bat
```

## 帳號（首次啟動自動建立）

| 帳號 | 密碼 | 角色 |
|------|------|------|
| admin | admin123 | ADMIN |
| member01 | member123 | USER |

註冊新帳號固定為 `USER`。

## API

- Swagger: http://localhost:8080/swagger-ui.html
- 登入: `POST /api/auth/login`
- 註冊: `POST /api/users/register`
- 罐頭清單: `GET /api/cans`（訪客可瀏覽）

## 公開上線（讓大家直接點網址）

GitHub 開源 ≠ 公開網站。要讓使用者不用自己跑 Docker，需要把服務部署到 **24 小時開著的雲端主機**。

### 推薦方案：Oracle Cloud 免費 VM + 現有 Docker（副業最划算）

| 項目 | 費用 | 說明 |
|------|------|------|
| Oracle Cloud Always Free VM | 免費 | 可長期跑 `docker compose` |
| 網域（如 `.com`） | 約每年數百元 | 副業建議一定要有，才有 HTTPS 網址 |
| 本專案 Docker | 免費 | 已支援 |

**不建議**只靠 Render 免費方案當副業：會休眠、冷啟動慢，MySQL 也常需付費。

### 上線步驟概要

1. **申請 Oracle Cloud** 免費 ARM VM（Ubuntu）
2. **安裝 Docker** + 開放防火牆 **80 / 443**
3. **購買網域**（Cloudflare / Namecheap 等），A 記錄指向 VM 公網 IP
4. **在 VM 上 clone 專案**：

```sh
git clone https://github.com/unachi30/catfood-recommender.git
cd catfood-recommender
cp .env.example .env          # 填入網域、強密碼
cp Caddyfile.example Caddyfile  # 改成你的網域
```

5. **編輯 `.env`**（必改）：

```env
APP_CORS_ALLOWED_ORIGINS=https://你的網域
MYSQL_ROOT_PASSWORD=隨機強密碼
APP_JWT_SECRET=隨機至少32字元
```

6. **啟動正式環境**（含 HTTPS 自動憑證）：

```sh
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

7. 瀏覽器開啟 `https://你的網域`

### 正式環境與本機的差異

| | 本機 `docker compose up` | 正式 `docker-compose.prod.yml` |
|--|--------------------------|--------------------------------|
| 網址 | http://localhost:5173 | https://你的網域 |
| MySQL 對外 port | 3307（本機 DBeaver） | **不對外開放**（較安全） |
| HTTPS | 無 | Caddy 自動申請 |
| 密碼 | 預設 admin123 | `.env` 自訂強密碼 |

### 上線後必做

- 用 `admin` / `admin123` 登入後 **立刻改密碼**（或之後關閉預設帳號種子）
- `.env` 不要 commit 到 GitHub
- 首次商品清單為空，需管理員自行新增

### 備用方案（較省事但可能月費）

前端 [Cloudflare Pages](https://pages.cloudflare.com/) + 後端 [Render](https://render.com/) + 付費 MySQL。需拆開部署，並在 build 時設定 `VITE_API_BASE_URL=https://api.你的網域/api`。
