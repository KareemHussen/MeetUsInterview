# MeetUsInterviewTask

A **Spring Boot** backend application built for technical assessment purposes. It demonstrates secure authentication, task management, filtering, pagination, database migrations, and seed data generation.

## 🚀 Features

### 🔐 Authentication & Security

- Spring Security with JWT authentication
- Password hashing using **BCrypt**
- Secure login & protected routes
- Custom authentication & authorization handling

### ✅ Task Management Module

- Task status enum: `OPEN`, `DONE`

- Each user manages **their own tasks only**
- CRUD operations (Create, Read, Update, Delete)
- DTO‑based request/response handling
- Request **validation** using Jakarta Validation

### 🔍 Filtering & Query System

Implemented using **Spring Specification API** with filters for:

- `status` (OPEN, DONE)
- `fromDate` / `toDate`
- `search` (title / description)
- `user_id` (internally controlled — user only sees own tasks)

### 📄 Pagination & Sorting

- Dynamic pagination (`page`, `size`)
- Sorting by **any field** (`sortBy`, `direction` ASC/DESC)

### 🗄 Database & Migrations

- **Liquibase** for schema migrations
- Indexing on frequently‑queried columns for better performance

### 🧪 Fake Data Generation

- Seeder class using **Java Faker** to generate test users and tasks

---

## 📦 Tech Stack

| Category     | Technology                                                          |
| ------------ | ------------------------------------------------------------------- |
| Language     | Java 17                                                             |
| Framework    | Spring Boot                                                         |
| Security     | Spring Security + JWT + BCrypt                                      |
| DB Migration | Liquibase                                                           |
| ORM          | Hibernate / JPA                                                     |
| DB           | H2 In‑Memory Database                                               |
| Fake Data    | Java Faker                                                          |

---

## 📁 Project Structure

```
src/main/java/com/meetus/MeetUSInterview
├── config
│   ├── CorsConfig.java
│   ├── JWTSecurityConfiguration.java
│   └── OpenApiConfig.java
├── controller
│   ├── AuthController.java
│   └── TaskController.java
├── dto
│   ├── request
│   │   ├── auth
│   │   │   ├── LoginRequest.java
│   │   │   └── RegisterRequest.java
│   │   └── task
│   │       ├── TaskCreateRequest.java
│   │       └── TaskSearchRequest.java
│   └── response
│       ├── auth
│       │   ├── AuthResponse.java
│       │   └── UserResponse.java
│       └── task
│           ├── TaskPageResponse.java
│           ├── TaskResponse.java
│           └── APIResponse.java
├── entity
│   ├── Task.java
│   └── User.java
├── enums
│   └── TaskStatus.java
├── exception
│   └── GlobalExceptionHandler.java
├── mapper
│   ├── TaskMapper.java
│   └── UserMapper.java
├── repository
│   ├── TaskRepository.java
│   ├── TaskSpecifications.java
│   └── UserRepository.java
├── security
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtil.java
├── seeder
│   └── TaskSeeder.java
├── service
│   ├── TaskService.java
│   ├── UserService.java
│   └── MeetusInverviewApplication.java
└── resources
    ├── db/changelog
    │   ├── 001-create-users-table.yaml
    │   ├── 002-create-tasks-table.yaml
    │   └── changelog-master.yaml
    └── application.properties
```

---

## 🧠 Key Concepts Demonstrated

- Clean DTO-based architecture
- Validation handling
- Specification pattern for dynamic queries
- JWT-based auth flow
- **H2 In‑Memory database**
- Database indexing & migrations (via Liquibase)
- Seeder & Java Faker for demo data (runs only in dev profile — disabled in prod profile)
- **38 Automated Test Cases** covering controllers, services, repositories & auth flows

---

## ▶️ Run Locally

```bash
./mvnw spring-boot:run
```

---

## 📬 API Endpoints

### Auth

```
POST /api/v1/auth/register
POST /api/v1/auth/login
```

### Tasks

```
GET    /api/v1/tasks
POST   /api/v1/tasks
PUT    /api/v1/tasks/{id} --To update status from open to done
DELETE /api/v1/tasks/{id}
```

Supports query params:

```
?page=0&size=10&sortBy=createdAt&direction=DESC&status=PENDING&search=test
```

## 📚 API Documentation (Swagger)

http://127.0.0.1:8080/api/v1/swagger-ui/index.html#/

---

## 📎 Postman Collection

Download API collection:
👉 [MeetUS Interview - Task Management API.postman_collection.json](./MeetUS Interview - Task Management API.postman_collection.json)


## 🌐 Profiles
- dev → Seeder + Faker enabled
- prod → Seeder disabled
