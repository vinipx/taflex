# Code Quality & Hygiene

To maintain a healthy and maintainable codebase, TAFLEX incorporates automated static analysis tools that enforce coding standards and detect potential bugs.

## 🛠 Quality Tools

We use two primary tools in our ecosystem:

| Tool | Purpose | Equivalent in JS |
| :--- | :--- | :--- |
| **Checkstyle** | Enforces **coding standards** (naming, indentation, whitespace). | ESLint |
| **PMD** | Detects **code smells** and **programming errors** (unused variables, empty catch blocks). | SonarQube / ESLint |

---

## 🚀 Running Checks Locally

Before submitting a Pull Request, you should run the full verification suite:

```bash
# Run all static analysis and unit tests
./gradlew verifyBuild test
```

Specific tasks:
- `checkstyleMain` / `checkstyleTest`: Run style checks.
- `pmdMain` / `pmdTest`: Run bug detection.

Reports are generated at:
- `build/reports/checkstyle/`
- `build/reports/pmd/`

---

## 🔍 Issues Detected

### 1. Standard Violations (Checkstyle)
- **Unused Imports**: Keeps the code clean and compilation fast.
- **Star Imports**: Avoids `import java.util.*` to prevent naming collisions.
- **Naming Conventions**: Ensures classes use `PascalCase` and methods use `camelCase`.
- **Line Length**: Enforces a maximum of 140 characters per line for readability.

### 2. Code Smells (PMD)
- **Duplicate Literals**: Detects strings used multiple times that should be constants.
- **Unused Private Fields**: Identifies dead code that can be removed.
- **Missing SerialVersionUID**: Ensures exceptions and serializable classes are robust.
- **Empty Control Statements**: Catches empty `if` or `while` blocks that might be logical errors.

---

## 🤖 CI/CD Integration

Every Push and Pull Request is automatically analyzed by our CI pipelines.

- **GitHub Actions**: The `lint` job in `ci.yml` and `pr-validation.yml` ensures all contributions meet standards.
- **GitLab CI**: The `code_hygiene` stage in `.gitlab-ci.yml` provides the same level of verification for GitLab users.
- **PR Validation**: Blocks merging if new code quality issues are introduced.
- **Upload Reports**: If a check fails in CI, the full HTML reports are uploaded as build artifacts for easy debugging.

---

## 🔧 Customizing Rules

Rules are defined in the `config/` directory:
- `config/checkstyle/checkstyle.xml`
- `config/pmd/pmd-ruleset.xml`

While we aim for high standards, these rules can be adjusted as the team's needs evolve.
