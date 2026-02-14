# Contributing Guidelines

Thank you for your interest in contributing to TAFLEX!

## Development Flow

1. **Fork the repository**.
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`.
3. **Implement your changes** following the project's coding standards.
4. **Ensure all tests pass**:
   ```bash
   # Run all verifications (compilation and unit tests)
   ./gradlew verifyBuild
   ```
5. **Submit a pull request** with a clear description of your changes.

## Coding Standards

- **Java 21+**: Use modern Java features where appropriate.
- **Naming**: Follow standard Java naming conventions (PascalCase for classes, camelCase for methods/variables).
- **Structure**: Maintain the existing package structure under `io.github.vinipx.taflex`.
- **Testing**: Add unit tests for any new core framework features in `src/test/java/io/github/vinipx/taflex/tests/unit`.
- **Documentation**: Update the relevant `.md` files in the `documentation/docs` directory if your changes affect the public API or configuration.
