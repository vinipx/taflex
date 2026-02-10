---
sidebar_position: 99
title: Changelog
---

# Changelog

All notable changes to this project will be documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Changed
- Migrated documentation engine from MkDocs to Docusaurus
- Added comprehensive navigation with Getting Started, Guides, Resources dropdowns
- Fixed Java version references to match build configuration (Java 21+)
- Updated package path references in documentation to match actual codebase (`io.github.vinipx`)

### Added
- Expanded best practices guide with full code examples and platform-specific tips
- Enhanced contributing guidelines with workflow, checklist, and commit conventions
- GitHub Actions workflow for automated documentation deployment
- PR validation workflow with size checks and commit message validation

## [1.0.0] - 2026-02-08

### Added
- Initial release of TAFLEX framework
- **Core Architecture**: Strategy Pattern-based driver resolution via `DriverFactory`
- **Web Testing**: Playwright driver strategy with CSS/XPath selector support
- **API Testing**: Apache HttpClient driver strategy with GET/POST/PUT/DELETE/PATCH support
- **Mobile Testing**: Appium driver strategy with Android and iOS support, auto-detection of connected devices, and auto-start of Appium server
- **Unified Element Interface**: `Element` interface with platform-specific implementations (`PlaywrightElement`, `ApiElement`, `MobileElement`)
- **Externalized Locators**: `PropertiesLocatorStrategy` with hierarchical loading (global → mode → page)
- **Configuration Management**: `ConfigManager` with properties file support, system property overrides, and typed accessors
- **Database Integration**: `DatabaseManager` with HikariCP connection pooling, parameterized queries, batch operations, and transaction support
- **Test Data Provider**: `TestDataProvider` interface for pluggable data sources
- **Test Infrastructure**: `BaseTest` with automatic driver lifecycle, screenshot capture on failure, and logging
- **TestNG Listeners**: `TestListener` for logging, `RetryAnalyzer` for automatic retries, `AnnotationTransformer` for global retry application
- **Gradle Build**: Custom tasks for `webTest`, `apiTest`, `mobileTest`, `smokeTest`, `regressionTest`
- **Setup Script**: `setup.sh` for automated environment configuration
- **CI/CD**: GitHub Actions workflows for build, test compilation, docs deployment, and PR validation
- **Documentation**: Full Docusaurus site with architecture, API reference, guides, best practices, and troubleshooting
