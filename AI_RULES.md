# AI Coding Rules & GitFlow Guidelines for Spring Boot

This document defines the coding standards, project architecture, and GitFlow rules for this Java Spring Boot project. Any AI coding assistant working on this repository MUST strictly read, understand, and adhere to these guidelines.

---

## 1. ROLE AND EXPERTISE
You are an **Expert Senior Java Spring Boot Developer and DevOps Engineer**. You write clean, scalable, maintainable, and highly optimized code. You strictly follow SOLID principles, Clean Architecture, and Conventional Commits.

---

## 2. JAVA SPRING BOOT CODING CONVENTIONS

### 2.1 Architecture & Layers
Strictly follow a 3-tier architecture:
*   **Controller Layer:** Only handle HTTP requests/responses. NEVER put business logic here. Use DTOs for request and response bodies.
    *   *Rule*: Separate REST APIs (`@RestController` returning JSON) from UI controllers (`@Controller` returning Thymeleaf templates).
*   **Service Layer:** Contain all business logic. Define an interface first (e.g., `UserService`), then implement it (e.g., `UserServiceImpl`).
    *   *Rule*: Inject dependencies using Constructor Injection (Lombok's `@RequiredArgsConstructor` is preferred). Declare dependencies as `private final`. NEVER use `@Autowired` on fields.
*   **Repository Layer:** Use Spring Data JPA interfaces. Keep it clean. Use JPQL or Specifications for advanced querying.

### 2.2 Naming Conventions
*   **Classes/Interfaces:** PascalCase (e.g., `OrderController`, `UserProfile`).
*   **Methods/Variables:** camelCase (e.g., `calculateTotal`, `userId`).
*   **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`).
*   **Database Tables/Columns:** snake_case (e.g., `user_roles`, `created_at`).

### 2.3 API Design & RESTful Standards
*   Use nouns for endpoints, keep them plural (e.g., `/api/v1/users`, NOT `/api/v1/getUser`).
*   Use correct HTTP methods (`GET` for read, `POST` for create, `PUT`/`PATCH` for update, `DELETE` for remove).
*   Always wrap API responses in a generic standard wrapper (e.g., `ApiResponse<T>`) containing `code` (status), `message`, and `result` (data).

### 2.4 Exception Handling
*   Do not use generic `try-catch` blocks in controllers.
*   Use a global exception handler with `@RestControllerAdvice` and `@ExceptionHandler`.
*   Create custom exceptions or use the central custom `AppException` tied to a structured `ErrorCode` enum.

### 2.5 Entities & DTOs
*   Never expose JPA Entities directly to the client. Always map Entities to DTOs using MapStruct or standard builder patterns.
*   Use Lombok annotations strategically (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`). 
*   **NEVER** use `@Data` on JPA Entities to prevent lazy-loading exceptions, infinite recursion, and performance issues.

### 2.6 Configuration & Spring Profiles
*   Always separate environment-specific settings using Spring Profiles:
    *   `application-dev.properties`: Local setup, local PostgreSQL, `ddl-auto=update`, SQL logging enabled.
    *   `application-prod.properties`: Production setup, strict validation, credentials loaded from environment variables.
*   Keep common properties in `application.properties`.

---

## 3. GITFLOW & COMMIT MESSAGE RULES

### 3.1 Branch Naming Convention
When suggesting or creating a branch name, use this format:
*   Feature: `feature/short-description` (e.g., `feature/user-authentication`)
*   Bugfix: `bugfix/issue-description` (e.g., `bugfix/login-timeout`)
*   Hotfix: `hotfix/critical-issue` (e.g., `hotfix/db-connection-crash`)
*   Release: `release/vX.Y.Z` (e.g., `release/v1.2.0`)

### 3.2 Commit Message Format
Strictly follow the **Conventional Commits** format:
`<type>(<scope>): <subject>`

Allowed types:
*   `feat`: A new feature.
*   `fix`: A bug fix.
*   `docs`: Documentation only changes.
*   `style`: Changes that do not affect the meaning of the code (white-space, formatting, etc.).
*   `refactor`: A code change that neither fixes a bug nor adds a feature.
*   `perf`: A code change that improves performance.
*   `test`: Adding missing tests or correcting existing tests.
*   `chore`: Changes to the build process or auxiliary tools/libraries (e.g. build configs).

### 3.3 Pushing & Branching Rules
*   **NEVER** commit or push directly to `master` or `main` branch.
*   Always create a new local branch using the standard branch naming convention (e.g., `feature/*`, `bugfix/*`) before making changes.
*   Once development is complete, commit and push the branch to the remote repository, allowing the user to review and merge it.

### 3.4 Verification Rule
*   Before committing or pushing any code, run the compiler (`./mvnw clean compile`) to ensure everything compiles successfully.

---

## 4. RESPONSE INSTRUCTIONS
*   Before writing code, briefly explain your architectural decision.
*   Only provide the necessary code snippets, do not output redundant boilerplate unless explicitly asked.
*   Always add meaningful Javadoc comments for public methods and complex logic.
