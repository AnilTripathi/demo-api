# Contributing to MyHealth API

Thank you for your interest in contributing to MyHealth API! This document provides guidelines and instructions for contributing.

## Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally
3. Create a new branch for your feature or bug fix
4. Make your changes
5. Test your changes
6. Submit a pull request

## Development Setup

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 13+ (optional, H2 for testing)
- Git

### Local Development

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/myhealth-api.git
cd myhealth-api

# Build and test
./mvnw clean test

# Run with test profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

## Code Standards

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and concise
- Use Lombok annotations to reduce boilerplate

### Testing

- Write unit tests for all new functionality
- Use `@SpringBootTest` for integration tests
- Maintain test coverage above 80%
- Use the test profile for H2 database testing

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=YourTestClass

# Generate coverage report
./mvnw test jacoco:report
```

### Documentation

- Update README.md for user-facing changes
- Add/update API documentation in code
- Create feature-specific docs in `docs/` folder
- Update CHANGELOG.md for notable changes

## Pull Request Process

### Before Submitting

1. **Test your changes**:
   ```bash
   ./mvnw clean test
   ```

2. **Check code formatting**:
   ```bash
   ./mvnw spring-javaformat:apply
   ```

3. **Update documentation** if needed

4. **Add changelog entry** in CHANGELOG.md

### Pull Request Guidelines

- **Title**: Use clear, descriptive titles
- **Description**: Explain what changes you made and why
- **Testing**: Describe how you tested your changes
- **Breaking Changes**: Clearly mark any breaking changes

### Example PR Template

```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
```

## Commit Message Format

Use conventional commit format:

```
type(scope): description

[optional body]

[optional footer]
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Test changes
- `chore`: Build/tooling changes

### Examples

```bash
feat(auth): add JWT refresh token endpoint
fix(cors): resolve preflight request issues
docs(readme): update API endpoint examples
test(auth): add integration tests for login flow
```

## Issue Reporting

### Bug Reports

Include:
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment details (Java version, OS, etc.)
- Relevant logs or error messages

### Feature Requests

Include:
- Clear description of the feature
- Use case or problem it solves
- Proposed implementation approach
- Any breaking changes

## Code Review Process

1. **Automated Checks**: All PRs must pass CI/CD checks
2. **Peer Review**: At least one maintainer review required
3. **Testing**: Verify tests pass and coverage is maintained
4. **Documentation**: Ensure docs are updated appropriately

## Security

- **Never commit secrets** (passwords, API keys, etc.)
- **Use environment variables** for sensitive configuration
- **Follow security best practices** for authentication/authorization
- **Report security issues privately** to maintainers

## Questions?

- **General Questions**: Open a GitHub Discussion
- **Bug Reports**: Create a GitHub Issue
- **Security Issues**: Email maintainers directly
- **Feature Requests**: Open a GitHub Issue with feature request template

## License

By contributing, you agree that your contributions will be licensed under the MIT License.