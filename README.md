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
