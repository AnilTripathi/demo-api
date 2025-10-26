# Project overview
This repository is a Spring Boot REST API. Follow these high-level goals when writing or editing code:
- Design for clarity, testability, and security.
- Use layered architecture: `controller` → `service` → `repository` → `domain/dto`.
- Prefer immutability for DTOs and domain objects where practical.
- Keep controllers thin: controllers map requests/responses and orchestration only; business logic lives in services.
- Use modern Java features (local variable type inference `var` where readable, sealed classes/records/Pattern Matching when appropriate) — but prefer readability over novelty.
- Aim for clean, easily reviewed changes and consistent styling across files.

# Java & build
- Use the Java version defined in the build file (pom.xml or build.gradle). Prefer and default to the current LTS (e.g., Java 21+) if not specified.
- Build with Maven or Gradle (use the repo's existing build tool).
- Provide commands:
  - Build: `mvn -T 1C -DskipTests=false clean verify` or `./gradlew clean build` (do not skip tests by default).
  - Run tests + coverage: `mvn clean verify` (JaCoCo configured) or `./gradlew clean test jacocoTestReport`.
  - Run app locally: `mvn spring-boot:run` or `./gradlew bootRun`.
- CI should fail if unit test coverage < 90% (configure JaCoCo rule).

# API design & best practices
- Use RESTful resource-oriented routes and HTTP semantics (GET/POST/PUT/PATCH/DELETE).
- Use HTTP status codes accurately and return meaningful error bodies.
- Model request/response with immutable DTOs (use Java `record` or Lombok if project uses it).
- Validate inputs using `javax.validation`/`jakarta.validation` annotations and Spring's `@Valid`.
- Implement global exception handling with `@ControllerAdvice` and custom error payloads.
- Keep transactional boundaries in the service layer. Use `@Transactional` where needed.
- Favor returning `Optional<T>` from repositories and map to proper responses in services/controllers.
- Use Spring Data repositories interfaces for persistence.
- Use DTOs for external API surface; do not expose JPA entities directly.

# Concurrency, performance & Java modern features
- Use `CompletableFuture` or reactive programming only where it fits the use-case; default to imperative blocking MVC unless reactive required.
- Where helpful, use Java records for DTOs and `sealed` classes for well-bounded hierarchies.
- Use `Stream`/`Optional` idiomatically—avoid nested optionals and overly complex stream chains that harm readability.
- Use `var` for local variables where it improves readability.
- Prefer `Map.of`/`List.of` for small immutable collections.

# Security & configuration
- Do NOT commit secrets. Secrets must come from env vars, secret manager, or CI pipeline variables.
- Use Spring Security for authn/authz. Keep endpoints protected by default; explicitly allow public endpoints.
- Use HTTPS in production and enable secure cookie flags where relevant.
- Default to least-privilege principle for database and external services.

# Observability & ops
- Include Spring Actuator endpoints for health, info, and metrics (limit exposure in production).
- Add structured logging (slf4j). Prefer consistent log message templates and avoid logging secrets.
- Provide OpenAPI / Swagger documentation for controllers (springdoc-openapi or springfox).
- Add meaningful health checks for dependencies (DB, message brokers).

# Testing guidance (goal: ≥90% unit test coverage)
- Unit tests: JUnit 5 (Jupiter) + Mockito (or MockK). Mock external dependencies: repos, clients, services.
- Controller tests: use `@WebMvcTest` + `MockMvc` to verify request/response mapping, validation, status codes, and error payloads.
- Service tests: plain unit tests; assert business logic and transactional boundaries.
- Repository tests: use `@DataJpaTest` with an embedded DB (H2) or Testcontainers for realistic integration tests where index/SQL matters.
- Integration tests: a small set of end-to-end tests using Spring Boot Test and Testcontainers for DB to exercise main flows.
- Use parameterized tests for edge-case combinations.
- Add mutation tests or boundary condition tests as needed to increase confidence.
- Ensure edge cases & error paths are covered (validation errors, not-found, unauthorized, DB exceptions).
- Use test fixtures/factories to generate test data and keep tests deterministic.

# Coverage enforcement
- Configure JaCoCo (Maven or Gradle) to fail the build when coverage < 90% (line and branch coverage rules).
- Prefer many small focused unit tests over few large ones.
- If coverage gaps are flagged by Copilot suggestions, generate additional tests for missed branches and exception paths.

# Code style & quality
- Follow a consistent style (indent 4 spaces, max line length 120).
- Use static analysis: SpotBugs/Checkstyle/PMD or equivalent in CI.
- Write clear JavaDoc for public APIs and complex logic. Keep method names descriptive.
- Keep methods small: prefer single responsibility.
- Use meaningful commit messages. Recommended format:
  - `feat(controller): add endpoint for ...`
  - `fix(service): handle null pointer when ...`
  - `test(unit): add tests for ...`

# Pull Request expectations
- Include an overview and testing steps in PR description.
- Attach screenshots (if relevant) and sample requests/responses.
- Provide `curl` or HTTP snippets for manual testing.
- Ensure all checks pass (build, tests, style, coverage).
- Link to related issue(s).

# Useful prompts for Copilot in this repo
- `@workspace Create a new REST endpoint to [resource], following existing patterns. Add controller, service, DTOs and unit tests with >=90% coverage. Use Java records for DTOs and validate request bodies.`
- `@workspace #file:'src/main/java/.../UserService.java' Write unit tests that mock the repository and assert error handling when DB returns empty. Target branch coverage for this class.`
- `@workspace #file:'src/main/java/.../OrderController.java' Provide integration test using Testcontainers for DB and MockMvc to assert full end-to-end flow.`
- `@workspace Generate a `@ControllerAdvice` error handler that returns `ApiError {timestamp, status, path, message, details[]}` and add tests covering validation and unexpected exceptions.`
- `@workspace #file:'pom.xml' Add JaCoCo configuration to enforce minimum 90% line and branch coverage and show the exact commands to run report generation.`

# Examples / scaffolding rules for Copilot
- When asked to generate DTOs, prefer `record` form like `public record UserDto(UUID id, String name, String email) {}`.
- When asked to generate tests, always:
  - Use `@ExtendWith(MockitoExtension.class)` for pure unit tests.
  - Use clear Arrange-Act-Assert sections and avoid heavy setup in `@BeforeEach`.
  - Mock external calls and verify interactions with `verify(...)`.
  - Assert exception messages where meaningful, and assert HTTP status and JSON payload for controller tests.
- When generating code that interacts with DB, create repository interfaces extending `JpaRepository<T, ID>` and avoid using raw SQL unless required.

# What to avoid
- Don’t generate code that logs or prints secrets.
- Don’t generate long monolithic methods; split into helper methods with tests.
- Don’t assume dev-only libs (like Lombok) are available unless present in repo.
- Avoid using deprecated Spring APIs.

# If Copilot seems to miss context
- Open the relevant files in the IDE to make them editor-visible.
- Use explicit `#file:` references or paste the relevant snippet in the chat.
- If a generated suggestion uses incorrect imports or project-specific base classes, prefer small edits and re-run targeted prompts.

# Contact / docs
- See `README.md` for run/test examples and environment variables.
- Check `src/main/resources/application.yml` for property names and sensible defaults.
- Review existing code in `src/main/java` for architectural patterns and conventions.