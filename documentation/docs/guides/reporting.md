# Reporting & Governance

TAFLEX provides a robust reporting ecosystem designed to satisfy different stakeholders, from Developers needing quick feedback to Managers requiring high-level quality governance.

## Strategy: Choosing the Right Tool

Depending on your project's maturity and requirements, you might want to use one or a combination of these tools:

| Tool | Primary Audience | Best For... | Why use it? |
| :--- | :--- | :--- | :--- |
| **Local HTML** | Developers / QAs | Local debugging | Fast, zero-config, included by default in TestNG. |
| **Allure** | QA / Dev Teams | Sprint-level results | Beautiful interactive charts, history, and categories. |
| **ReportPortal** | Managers / DevOps | Multi-project visibility | AI-powered failure analysis and cross-build trends. |
| **Xray (Jira)** | Managers / POs | Requirements Traceability | Links automated results directly to Jira User Stories. |

---

## 📊 Allure Report

Allure is an open-source framework designed to create interactive and easy-to-read reports.

### Configuration
Enable it in your `automation.properties`:
```properties
allure.enabled=true
allure.results.directory=build/allure-results
```

### Generating Reports
```bash
# Generate report from results
./gradlew allureReport

# Serve report locally
./gradlew allureServe
```

---

## 🛠 Xray Integration (Jira)

Xray is the preferred choice for teams requiring strict **Governance** and **Traceability**.

### Configuration
Enable it in your `automation.properties`:
```properties
xray.enabled=true
xray.client.id=your_client_id
xray.client.secret=your_client_secret
```

### How it works
The `XrayService` handles authentication with the Xray Cloud API using your credentials and provides methods to import execution results. Results are typically mapped via Jira Issue keys tagged in your tests.

---

## 🚀 ReportPortal

ReportPortal is an AI-powered dashboard used for enterprise-scale test management.

### Configuration
Configure the connection in `automation.properties`:
```properties
reportportal.enabled=true
reportportal.endpoint=https://rp.yourcompany.com
reportportal.api.key=your_api_key
reportportal.project=my_project
```

### Why use it?
- **AI Analysis**: Automatically categorizes failures (Product Bug, Automation Bug, System Issue).
- **Consolidated View**: See results from different pipelines in a single place.

---

## 📝 Built-in TestNG Report

The standard TestNG HTML report is always available for technical inspection.

- **Location**: `build/reports/tests/index.html`
- **Screenshots**: Attached automatically on failure if `screenshot.on.failure=true`.
- **Logs**: Detailed execution logs in `logs/test-automation.log`.
