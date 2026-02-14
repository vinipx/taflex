#!/bin/bash

# TAFLEX - Test Automation Framework Setup Script
# ==============================================================================
# This script initializes the environment, verifies dependencies, 
# and prepares the framework for the first test run.
# ==============================================================================

set -e

# --- Color Definitions ---
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# --- Helper Functions ---
print_header() {
    echo -e "\n${CYAN}${BOLD}================================================================${NC}"
    echo -e "${CYAN}${BOLD}  🚀 TAFLEX - Framework Setup${NC}"
    echo -e "${CYAN}${BOLD}================================================================${NC}\n"
}

print_status() {
    echo -e "${GREEN}  ✓${NC} $1"
}

print_error() {
    echo -e "${RED}  ✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}  ! $1${NC}"
}

print_info() {
    echo -e "${BLUE}  ℹ${NC} $1"
}

print_step() {
    echo -e "${BOLD}Step $1: $2${NC}"
}

# --- Initialization ---
print_header

# 1. OS Detection
OS="unknown"
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS="macos"
fi
print_info "Platform: $OS"

# 2. Dependency Check: Java 21+
print_step "1" "Verifying Java Installation"
if command -v java >/dev/null 2>&1; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f1 | cut -d'-' -f1)
    
    # Handle version format like "1.8", "11", "21"
    if [ "$JAVA_MAJOR" -eq 1 ]; then
        JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f2)
    fi

    if [ "$JAVA_MAJOR" -ge 21 ]; then
        print_status "Java $JAVA_VERSION detected"
    else
        print_error "Java 21 or higher is required (Found: $JAVA_VERSION)"
        exit 1
    fi
else
    print_error "Java is not installed. Please install JDK 21+."
    exit 1
fi

# 3. Dependency Check: Gradle
print_step "2" "Verifying Build Tool"
if [ -f "./gradlew" ]; then
    chmod +x gradlew
    print_status "Gradle wrapper found and executable"
    GRADLE_CMD="./gradlew"
else
    if command -v gradle >/dev/null 2>&1; then
        print_status "Global Gradle installation detected"
        GRADLE_CMD="gradle"
    else
        print_error "Gradle wrapper or global installation not found."
        exit 1
    fi
fi

# 4. Configuration Setup
print_step "3" "Initializing Configuration"
if [ ! -f "automation.properties" ]; then
    if [ -f "automation.properties.template" ]; then
        cp automation.properties.template automation.properties
        print_status "Created 'automation.properties' from template"
        print_warning "Action Required: Edit 'automation.properties' with your credentials."
    else
        print_error "Template file 'automation.properties.template' is missing."
        exit 1
    fi
else
    print_status "'automation.properties' already exists (skipping)"
fi

# 5. Directory Structure
print_step "4" "Preparing Directories"
required_dirs=(
    "logs"
    "screenshots"
    "build/allure-results"
    "src/test/resources/locators/web"
    "src/test/resources/locators/api"
    "src/test/resources/locators/mobile"
)

for dir in "${required_dirs[@]}"; do
    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
        print_status "Created: $dir"
    fi
done

# 6. Build and Dependency Resolution
print_step "5" "Resolving Dependencies and Compiling"
print_info "This may take a minute on the first run..."
if $GRADLE_CMD verifyBuild --no-daemon > /dev/null 2>&1; then
    print_status "Project compiled and dependencies resolved"
else
    print_error "Compilation failed. Run './gradlew verifyBuild --info' to diagnose."
    exit 1
fi

# 7. Playwright Browser Installation
print_step "6" "Installing Browsers"
if $GRADLE_CMD installPlaywright --no-daemon > /dev/null 2>&1; then
    print_status "Playwright browsers installed successfully"
else
    print_warning "Failed to install Playwright browsers automatically."
    print_info "Manual fix: run './gradlew installPlaywright'"
fi

# 8. Success Summary
echo -e "\n${GREEN}${BOLD}================================================================${NC}"
echo -e "${GREEN}${BOLD}  ✅ Setup Successfully Completed!${NC}"
echo -e "${GREEN}${BOLD}================================================================${NC}\n"

echo -e "${BOLD}Next steps to start testing:${NC}"
echo -e "  1. Update settings:  ${YELLOW}nano automation.properties${NC}"
echo -e "  2. Run unit tests:   ${YELLOW}./gradlew test --tests io.github.vinipx.taflex.tests.unit.*${NC}"
echo -e "  3. Run web tests:    ${YELLOW}./gradlew webTest${NC}"
echo -e "  4. Run API tests:    ${YELLOW}./gradlew apiTest${NC}"
echo ""
echo -e "Documentation: ${CYAN}https://vinipx.github.io/taflex${NC}"
echo -e "Happy Testing! 🚀\n"
