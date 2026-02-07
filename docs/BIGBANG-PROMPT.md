# Role
Act as a Senior Quality Engineering Architect. You are tasked with designing a unified Test Automation Framework for a large-scale enterprise project. Your audience includes both technical SDETs (who need implementation details) and Engineering Management (who need roadmap and risk analysis).

# Project Requirements & Stack
The framework must adhere to the following strict technical constraints:
1.  **Core Language:** Java.
2.  **Test Runner:** TestNG.
3.  **Configuration Management:** * Central control via a `automation.properties` file.
    * This file is **not** version controlled (gitignored) but generated via a provided `setup.sh` script (template based).
    * This file controls the execution mode (Web, API, Mobile).
4.  **Web Automation:** Microsoft Playwright (Java bindings).
5.  **Locator Strategy:** STRICT REQUIREMENT - All locators (selectors) must be externalized in `.properties` files, decoupled from the test code.
6.  **API Automation:** Apache HttpClient (supporting REST, SOAP, and gRPC).
7.  **Mobile Automation:** Appium.
8.  **Database:** SQL capability required (recommend a lightweight, robust JDBC wrapper or library).
9.  **Test Data:** Externalized in `.properties`, CSV, or plain text.
10. **Reporting:** Native integration with ReportPortal.

# Assignment
Based on the requirements above, produce a comprehensive design document covering the following four sections:

## 1. Architectural Options (Elaborate on 3)
Propose three distinct architectural patterns for structuring this framework. For each option, explain how it handles the "Unified" nature (Web+API+Mobile in one repo) and the "Externalized Locators" constraint.
* **Option A: The Modular Monolith (Layered).** Focus on standard separation of concerns (Driver Layer, Service Layer, Test Layer).
* **Option B: The Factory/Strategy Pattern.** Focus on dynamic runtime resolution where the `automation.properties` determines the driver instantiation strategy at runtime.
* **Option C: The Data-Driven/Keyword Hybrid.** Focus on the constraint of externalizing locators and data, potentially treating the Java code as a mere engine that processes the properties files.

## 2. Development Roadmap
Create a step-by-step implementation roadmap (Phases 1-4).
* Include the creation of the `setup.sh` utility.
* Include when to integrate ReportPortal.
* Include a "Proof of Concept" phase for the Locator-in-Properties strategy to ensure performance with Playwright.

## 3. Pros & Cons Analysis
For each of the 3 architectural options proposed in Section 1, provide a table highlighting:
* **Pros:** Maintainability, Scalability, Ease of Onboarding.
* **Cons:** Complexity, Debugging difficulty (especially with external locators), Performance overhead.
* **Best For:** Which team size/skill level suits this option best?

## 4. Executive Summary
Write a summary tailored for non-technical management.
* Explain *why* this stack (Java/Playwright/TestNG) is a safe investment.
* Highlight the cost/benefit of the "Unified Framework" approach.
* Provide a final recommendation on which Architecture Option to choose to balance speed-to-market with long-term maintenance.

# Formatting
* Use standard Markdown.
* Use MermaidJS or PlantUML syntax if you need to visualize the folder structure or class hierarchy.
* Keep the tone professional, decisive, and technically accurate.