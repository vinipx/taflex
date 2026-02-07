#!/bin/bash

# TAFLEX - Test Automation Framework Setup Script
# This script initializes the test automation framework environment

set -e

echo "=========================================="
echo "  TAFLEX - Framework Setup"
echo "=========================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored messages
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}!${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

# Check if running on macOS or Linux
OS="unknown"
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS="macos"
else
    print_warning "Unsupported OS: $OSTYPE. Some features may not work."
fi

print_info "Detected OS: $OS"
echo ""

# ==========================================
# 1. Check Java Version (21+)
# ==========================================
echo "Checking Java installation..."

# Function to check Java version
get_java_version() {
    local java_cmd=$1
    if [ -x "$java_cmd" ]; then
        "$java_cmd" -version 2>&1 | head -n 1 | cut -d'"' -f2
    else
        echo ""
    fi
}

# Try to find Java 23+
JAVA_CMD=""

# First check JAVA_HOME if set
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_VERSION=$(get_java_version "$JAVA_HOME/bin/java")
    JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f1)
    if [ "$JAVA_MAJOR" -ge 23 ]; then
        JAVA_CMD="$JAVA_HOME/bin/java"
    fi
fi

# If not found, check common locations
if [ -z "$JAVA_CMD" ]; then
    for java_path in \
        "/Users/$(whoami)/Library/Java/JavaVirtualMachines/corretto-21*/Contents/Home/bin/java" \
        "/Users/$(whoami)/Library/Java/JavaVirtualMachines/openjdk-21*/Contents/Home/bin/java" \
        "/Library/Java/JavaVirtualMachines/jdk-21*/Contents/Home/bin/java" \
        "/usr/lib/jvm/java-21*/bin/java"
    do
        for found_java in $java_path; do
            if [ -x "$found_java" ]; then
                JAVA_VERSION=$(get_java_version "$found_java")
                JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f1)
                if [ "$JAVA_MAJOR" -ge 21 ]; then
                    JAVA_CMD="$found_java"
                    break 2
                fi
            fi
        done
    done
fi

# If still not found, check default java command
if [ -z "$JAVA_CMD" ]; then
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(get_java_version "java")
        JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f1)
        if [ "$JAVA_MAJOR" -ge 21 ]; then
            JAVA_CMD="java"
        fi
    fi
fi

if [ -n "$JAVA_CMD" ]; then
    JAVA_VERSION=$(get_java_version "$JAVA_CMD")
    print_status "Java $JAVA_VERSION found (>= 21)"
    print_info "Using: $JAVA_CMD"
    
    # Export JAVA_HOME if not already set
    if [ -z "$JAVA_HOME" ]; then
        export JAVA_HOME=$(dirname $(dirname "$JAVA_CMD"))
        print_info "JAVA_HOME set to: $JAVA_HOME"
    fi
else
    print_error "Java 21 or higher not found"
    print_info "Please install Java 21 or higher: https://adoptium.net/"
    print_info "Detected Java installations:"
    /usr/libexec/java_home -V 2>&1 | grep -E "(21|23|24|25)" || echo "  None found"
    exit 1
fi

# ==========================================
# 2. Check Gradle
# ==========================================
echo ""
echo "Checking Gradle installation..."

if command -v ./gradlew &> /dev/null; then
    print_status "Gradle wrapper found"
    GRADLE_CMD="./gradlew"
elif command -v gradle &> /dev/null; then
    GRADLE_VERSION=$(gradle -version | grep Gradle | head -n 1 | cut -d' ' -f2)
    print_status "Gradle $GRADLE_VERSION found"
    GRADLE_CMD="gradle"
else
    print_error "Gradle not found"
    print_info "Please install Gradle 8.0 or higher: https://gradle.org/install/"
    print_info "Or use the included Gradle wrapper"
    exit 1
fi

# ==========================================
# 3. Create automation.properties from template
# ==========================================
echo ""
echo "Setting up configuration..."

if [ ! -f "automation.properties" ]; then
    if [ -f "automation.properties.template" ]; then
        cp automation.properties.template automation.properties
        print_status "Created automation.properties from template"
        print_warning "Please edit automation.properties with your environment settings"
    else
        print_error "Template file automation.properties.template not found"
        exit 1
    fi
else
    print_status "automation.properties already exists"
fi

# ==========================================
# 4. Create required directories
# ==========================================
echo ""
echo "Creating required directories..."

directories=(
    "logs"
    "reports"
    "screenshots"
    "test-output"
    "src/test/resources/locators/web"
    "src/test/resources/locators/api"
    "src/test/resources/locators/mobile"
    "src/test/resources/data"
    "src/test/resources/testng"
)

for dir in "${directories[@]}"; do
    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
        print_status "Created: $dir"
    else
        print_status "Exists: $dir"
    fi
done

# ==========================================
# 5. Resolve Gradle dependencies
# ==========================================
echo ""
echo "Resolving Gradle dependencies..."
echo "This may take a few minutes on first run..."

if $GRADLE_CMD dependencies --configuration compileClasspath > /dev/null 2>&1; then
    print_status "Dependencies resolved successfully"
else
    print_error "Failed to resolve dependencies"
    print_info "Try running: $GRADLE_CMD dependencies"
    exit 1
fi

# ==========================================
# 6. Compile test sources
# ==========================================
echo ""
echo "Compiling test sources..."

if $GRADLE_CMD compileTestJava > /dev/null 2>&1; then
    print_status "Compilation successful"
else
    print_error "Compilation failed"
    echo ""
    print_info "Running compilation with verbose output to diagnose the issue..."
    echo ""
    $GRADLE_CMD compileTestJava --info 2>&1 | tail -100
    exit 1
fi

# ==========================================
# 7. Check optional dependencies
# ==========================================
echo ""
echo "Checking optional dependencies..."

# Check if Appium is installed (for mobile)
if command -v appium &> /dev/null; then
    APPIUM_VERSION=$(appium -v 2>/dev/null || echo "unknown")
    print_status "Appium found: $APPIUM_VERSION"
else
    print_warning "Appium not found (required for mobile testing)"
    print_info "Install with: npm install -g appium"
fi

# Check Docker (for ReportPortal)
if command -v docker &> /dev/null; then
    print_status "Docker found"
else
    print_warning "Docker not found (required for ReportPortal local setup)"
fi

# ==========================================
# 8. Display configuration summary
# ==========================================
echo ""
echo "=========================================="
echo "  Setup Complete!"
echo "=========================================="
echo ""
print_status "Framework is ready to use"
echo ""
echo "Next steps:"
echo ""
echo "1. Configure automation.properties:"
echo "   $EDITOR automation.properties"
echo ""
echo "2. Run verification test:"
echo "   $GRADLE_CMD test --tests SetupVerificationTest"
echo ""
echo "3. Run sample Web tests:"
echo "   $GRADLE_CMD webTest"
echo ""
echo "4. Run sample API tests:"
echo "   $GRADLE_CMD apiTest"
echo ""
echo "5. Run sample Mobile tests:"
echo "   $GRADLE_CMD mobileTest"
echo ""
echo "Documentation:"
echo "  - Getting Started: docs/GETTING_STARTED.md"
echo "  - Architecture: ARCHITECTURE.md"
echo "  - API Reference: docs/API_REFERENCE.md"
echo ""
echo "Support:"
echo "  - Slack: #test-automation-framework"
echo "  - Email: automation-team@company.com"
echo ""
echo "Happy Testing! 🚀"
echo ""