# AI Code Explainer and Code Review Assistant

A full-stack web application that helps students and beginner developers understand source code through AI-powered explanations, bug detection, complexity analysis, and refactoring suggestions.

**B.Tech CSE Major Project** — demonstrates modern software engineering practices with React, Spring Boot, MySQL, and JWT authentication.

---

## Features

- **Code Explanation** — Line-by-line explanations, function/variable descriptions, algorithm detection
- **Code Review** — Bug detection, code smells, naming issues, refactoring suggestions
- **Complexity Analysis** — Time/space complexity with optimization tips
- **User Dashboard** — Statistics, charts, recent activity timeline
- **History** — Search, filter by language, reopen/delete past analyses
- **Admin Panel** — User management, system analytics
- **Dark Mode** — Responsive UI with sidebar navigation
- **Export** — Copy explanation, download PDF report

---

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Frontend | React 19, Vite, Tailwind CSS 4, Axios, React Router, Chart.js |
| Backend | Java 21, Spring Boot 3.4, Spring Security, JWT, Spring Data JPA |
| Database | MySQL 8 (H2 for quick dev) |
| AI | Pluggable `CodeAnalysisService` (Mock implementation included) |
| Docs | Swagger/OpenAPI at `/swagger-ui.html` |

---

## Project Structure

```
ai-code-explainer/
├── backend/                 # Spring Boot REST API
│   └── src/main/java/com/aicodeexplainer/
│       ├── controller/      # REST endpoints
│       ├── service/         # Business logic + MockCodeAnalysisService
│       ├── entity/          # JPA entities
│       ├── repository/      # Data access
│       ├── dto/             # Request/response objects
│       ├── config/          # Security, Swagger, CORS
│       ├── security/        # JWT filter & utilities
│       └── exception/       # Global exception handling
├── frontend/                # React SPA
│   └── src/
│       ├── pages/           # Landing, Dashboard, Analyzer, Admin...
│       ├── components/      # Reusable UI components
│       ├── context/         # Auth & Theme providers
│       └── api/             # Axios API client
└── docs/
    └── database-schema.sql  # MySQL schema
```

---

## Prerequisites

- **Java 21+** (Java 25 works with Lombok 1.18.38)
- **Maven 3.9+**
- **Node.js 18+** and npm
- **MySQL 8.0+** (or use H2 profile for quick start)

---

## Quick Start (H2 — No MySQL Required)

### Backend

```bash
cd backend
SPRING_PROFILES_ACTIVE=h2 mvn spring-boot:run
```

API runs at `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
H2 Console: `http://localhost:8080/h2-console`

### Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

App runs at `http://localhost:5173`

---

## MySQL Setup

1. Create the database:

```bash
mysql -u root -p < docs/database-schema.sql
```

2. Configure credentials (optional — defaults shown):

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export SPRING_PROFILES_ACTIVE=dev
```

3. Start backend:

```bash
cd backend
mvn spring-boot:run
```

---

## Default Admin Account

| Field | Value |
|-------|-------|
| Email | `admin@aicodeexplainer.com` |
| Password | `admin123` |

Created automatically on first startup via `DataInitializer`.

---

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, receive JWT |

### Analysis (requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/analysis/explain` | Line-by-line explanation |
| POST | `/api/analysis/review` | Bug detection & refactoring |
| POST | `/api/analysis/complexity` | Time/space complexity |
| GET | `/api/analysis/history` | Paginated history |
| GET | `/api/analysis/{id}` | Reopen analysis |
| DELETE | `/api/analysis/{id}` | Delete analysis |

### Dashboard & Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard` | User dashboard stats |
| GET | `/api/admin/users` | List all users (Admin) |
| GET | `/api/admin/statistics` | System stats (Admin) |
| DELETE | `/api/admin/user/{id}` | Delete user (Admin) |

---

## AI Integration (Pluggable Architecture)

The `CodeAnalysisService` interface allows swapping the mock provider with real LLM backends:

```java
public interface CodeAnalysisService {
    AnalysisResultDto explain(String sourceCode, String language);
    AnalysisResultDto review(String sourceCode, String language);
    AnalysisResultDto analyzeComplexity(String sourceCode, String language);
}
```

**Current:** `MockCodeAnalysisService` — rule-based analysis, no API keys needed.

**Future:** Implement providers for OpenAI, DeepSeek, Gemini, or local LLMs. Register the desired bean in Spring configuration.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` (MySQL) or `h2` |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | (built-in) | Change in production! |
| `JWT_EXPIRATION_MS` | `86400000` | Token expiry (24h) |
| `CORS_ORIGINS` | `http://localhost:5173` | Allowed frontend origins |
| `VITE_API_URL` | `http://localhost:8080` | Frontend API base URL |

---

## Build for Production

```bash
# Backend
cd backend && mvn clean package -DskipTests
java -jar target/ai-code-explainer-1.0.0.jar

# Frontend
cd frontend && npm run build
# Serve dist/ with any static server or nginx
```

---

## Security

- BCrypt password hashing
- JWT stateless authentication
- Role-Based Access Control (USER / ADMIN)
- Input validation on all endpoints
- CORS configuration
- Global exception handling with sanitized error messages

---

## License

Educational project for B.Tech CSE Major Project demonstration.
