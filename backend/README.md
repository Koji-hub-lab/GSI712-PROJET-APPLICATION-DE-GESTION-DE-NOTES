# Student Management System

A full-stack, production-ready Student Management System for managing courses, users, enrollments, grades, and learning resources. Built with a modern Java Spring Boot backend and a React/MUI frontend, this project provides robust role-based access control, JWT authentication, and a clean, responsive UI for administrators, teachers, and students.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Scripts & Commands](#scripts--commands)
- [Testing](#testing)
- [Contribution Guidelines](#contribution-guidelines)
- [License](#license)

---

## Overview

**Student Management System** is designed to streamline the administration of educational institutions. It enables efficient management of users (admins, teachers, students), courses, subjects, enrollments, and grades, with secure authentication and role-based access.

---

## Features

- **User Authentication**: Secure JWT-based login for all roles.
- **Role-Based Access Control**: Super Admin, Teacher, and Student roles with protected routes and endpoints.
- **Course Management**: Create, update, and view courses, subjects, and enrollments.
- **Student & Teacher Management**: CRUD operations for students and teachers.
- **Grade Management**: Assign and view grades.
- **Learning Management**: LMS features for students and teachers.
- **Responsive UI**: Built with React and Material-UI.
- **API-First Design**: RESTful endpoints for all major operations.
- **Database Migrations**: Managed with Flyway.
- **Production-Ready**: Includes build scripts and deployment instructions.

---

## Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.x/4.x
- Spring Data JPA (MySQL)
- Spring Security (JWT)
- Lombok
- Flyway (DB migrations)
- Maven

**Frontend:**
- React 18
- Material-UI (MUI)
- React Router DOM
- Axios
- Bootstrap

**Database:**
- MySQL 8.0+

**Testing:**
- JUnit (backend)
- React Testing Library, Jest (frontend)

---

## Project Structure

```
Student Management System/
├── student-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/StudentManagementSystem/
│   │   │   │   ├── controller/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── security/
│   │   │   │   └── dto/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/
│   ├── pom.xml
│   └── ...
├── student-frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── routes/
│   │   └── utils/
│   ├── public/
│   ├── package.json
│   └── ...
└── README.md
```

---

## Installation

### Prerequisites

- **Java 17+**
- **Node.js 16+**
- **MySQL 8.0+**
- **Maven 3.6+**

### Backend Setup

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd "Student Management System"
   ```

2. **Configure the database:**
   Edit `student-backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/student_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

3. **Run database migrations (optional):**
   Flyway will auto-run on backend startup.

4. **Build and run the backend:**
   ```bash
   cd student-backend
   ./mvnw spring-boot:run
   # or on Windows
   mvnw.cmd spring-boot:run
   ```
   The backend will start at [http://localhost:8080](http://localhost:8080).

### Frontend Setup

1. **Install dependencies:**
   ```bash
   cd ../student-frontend
   npm install
   ```

2. **Start the development server:**
   ```bash
   npm start
   ```
   The frontend will start at [http://localhost:3000](http://localhost:3000).

---

## Configuration

- **Backend:**
  All configuration is in `student-backend/src/main/resources/application.properties`.
  - Database URL, username, password
  - Server port (default: 8080)
  - JWT secret (if applicable)

- **Frontend:**
  API base URL is set in `src/api/axiosConfig.js` (default: `/api` or `http://localhost:8080`).

---

## Usage

### Default Super Admin

On first run, if no super admin exists, you can log in with:
- **Email:** `admin@example.com`
- **Password:** `Admin@123`

Or use the `/auth/bootstrap` endpoint to create one.

### Example: Logging In

1. Open [http://localhost:3000](http://localhost:3000)
2. Enter super admin credentials.
3. You will be redirected to the admin dashboard.

### Example: API Usage

**Login:**
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "Admin@123"
}
```

**Protected Endpoint:**
```http
GET /admin/dashboard/stats
Authorization: Bearer <JWT_TOKEN>
```

---

## API Documentation

### Authentication

- `POST /auth/login` — User login
- `POST /auth/bootstrap` — Create initial super admin

### Admin Endpoints

- `GET /admin/dashboard/stats` — Dashboard statistics
- `GET /admin/teachers` — List teachers
- `POST /admin/teachers` — Add teacher
- `GET /admin/students` — List students
- `POST /admin/students` — Add student
- `GET /admin/courses` — List courses
- `POST /admin/courses` — Add course

### Teacher Endpoints

- `GET /teacher/dashboard` — Teacher dashboard
- `GET /teacher/courses` — Teacher's courses
- `GET /teacher/students` — Enrolled students
- `POST /teacher/grades` — Assign grades

### Student Endpoints

- `GET /student/dashboard` — Student dashboard
- `GET /student/courses` — Enrolled courses
- `POST /student/enroll` — Enroll in course
- `GET /student/grades` — View grades

### Security

- JWT-based authentication
- Role-based access control (SUPER_ADMIN, TEACHER, STUDENT)
- Password encryption (BCrypt)
- Protected frontend routes

---

## Scripts & Commands

### Backend

- **Run backend:**
  `./mvnw spring-boot:run` or `mvnw.cmd spring-boot:run`
- **Build JAR:**
  `./mvnw clean package`
- **Run JAR:**
  `java -jar target/Student-Management-System-0.0.1-SNAPSHOT.jar`

### Frontend

- **Start dev server:**
  `npm start`
- **Build production:**
  `npm run build`
- **Run tests:**
  `npm test`

---

## Testing

### Backend

- **Run tests:**
  ```bash
  ./mvnw test
  ```

### Frontend

- **Run tests:**
  ```bash
  npm test
  ```
  (Uses React Testing Library and Jest. Add tests in `src/*.test.js`.)

---

## Contribution Guidelines

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push to your branch.
5. Create a Pull Request.

Please ensure code quality and add tests for new features.

---

## License

**Suggested:** MIT License
*(No explicit license file detected. Please add a LICENSE file to clarify terms.)*

---

## Assumptions

- The backend and frontend are intended to run locally for development.
- Production deployment involves building the frontend and copying the build to the backend's `static/` directory.
- The default super admin credentials are as shown above.
- All endpoints and features are inferred from code and may be extended.

---

**For questions or support, please open an issue or contact the maintainers.**

