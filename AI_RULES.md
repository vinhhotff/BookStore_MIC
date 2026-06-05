# AI Coding Rules & GitFlow Guidelines for Spring Boot

This document defines the coding standards, project architecture, and GitFlow rules for this Java Spring Boot project. Any AI coding assistant working on this repository MUST strictly read, understand, and adhere to these guidelines.

---

## 1. Project Architecture & Coding Standards

### 1.1 Layered Architecture
Always respect the 3-layer architecture pattern:
*   **Controller Layer**: Handles incoming HTTP requests, validates inputs using `@Valid`, and returns responses wrapped in `ApiResponse<T>`. 
    *   *Rule*: Separate REST APIs (`@RestController` returning JSON) from UI controllers (`@Controller` returning Thymeleaf templates).
*   **Service Layer**: Implements business logic and transaction management (`@Transactional`).
    *   *Rule*: Inject dependencies using Constructor Injection (Lombok's `@RequiredArgsConstructor` is preferred). Avoid field injection (`@Autowired` on class variables).
*   **Repository Layer**: Performs database interactions using Spring Data JPA. Use JPQL or Specifications for advanced querying.

### 1.2 Entities vs. DTOs
*   **NEVER** expose JPA Entity classes directly in the Controller layer or accept them as `@RequestBody`.
*   Always use DTOs (Data Transfer Objects):
    *   `*Request`: For receiving request payloads (e.g., `BookRequest`).
    *   `*Response`: For returning response payloads (e.g., `BookResponse`).
*   Use **MapStruct** for clean, automated mapping between Entities and DTOs.

### 1.3 Exception Handling & Error Codes
*   Do not throw generic `RuntimeException` or handle errors ad-hoc.
*   Always define business error codes in the `ErrorCode` enum (containing `code`, `message`, and `httpStatus`).
*   Throw a custom `AppException(ErrorCode.XYZ)` and let the `GlobalExceptionHandler` intercept and format the response.

### 1.4 Lombok Best Practices
*   Avoid using `@Data` on JPA Entity classes. `@Data` overrides `equals()`, `hashCode()`, and `toString()`, which can trigger lazy-loading exceptions or infinite recursion in bidirectional relationships.
*   Instead, use `@Getter`, `@Setter`, `@NoArgsConstructor`, and `@AllArgsConstructor` on Entities.

### 1.5 Configuration & Spring Profiles
*   Always separate environment-specific settings using Spring Profiles:
    *   `application-dev.properties`: Local setup, in-memory databases or local PostgreSQL, `ddl-auto=update`, SQL logging enabled.
    *   `application-prod.properties`: Production setup, strict validation, credentials loaded from environment variables.
*   Keep common properties in `application.properties`.

---

## 2. GitFlow & Git Commit Guidelines

### 2.1 Branching Strategy
Follow a standard GitFlow workflow:
*   `master` (or `main`): Production-ready branch. Code here must be stable and verified.
*   `develop`: Integration branch for all features.
*   `feature/*`: Branch off `develop` to build new features (e.g., `feature/thymeleaf-homepage`). Merge back to `develop` via Pull Request.
*   `bugfix/*` or `hotfix/*`: Branch off `master` (for hotfixes) or `develop` (for bugfixes) to address issues.

### 2.2 Conventional Commits
All commit messages written by the AI must follow the Conventional Commits specification:
*   `feat: ...` - A new feature (e.g., `feat: integrate thymeleaf home page`).
*   `fix: ...` - A bug fix (e.g., `fix: resolve null pointer in user mapping`).
*   `docs: ...` - Documentation changes (e.g., `docs: add AI_RULES.md`).
*   `refactor: ...` - Code restructuring with no feature changes (e.g., `refactor: rename welcome controller`).
*   `config: ...` - Environment or configuration changes (e.g., `config: split application properties`).
*   `test: ...` - Adding or fixing tests (e.g., `test: add book controller unit tests`).

### 2.3 Verification Rule
*   Before staging, committing, or pushing any code, the AI **MUST** run the compiler (`./mvnw clean compile` or `./mvnw test`) to verify the code compiles without errors.

---

## 3. Tech Stack Reference
*   **Java**: 17+
*   **Spring Boot**: 3.x
*   **Database**: PostgreSQL
*   **ORM**: Spring Data JPA
*   **View Engine**: Thymeleaf + Bootstrap
*   **Security**: Spring Security + BCrypt
