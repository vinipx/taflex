# Testing in the Cloud

This tutorial will guide you through the process of executing your TAFLEX tests on **BrowserStack** or **SauceLabs**.

## 1. Prerequisites

1.  A BrowserStack or SauceLabs account.
2.  Your **Username** and **Access Key** from the provider's dashboard.
3.  An existing TAFLEX project.

---

## 2. Configuration

TAFLEX is designed to switch to cloud execution purely through configuration in `automation.properties`.

### For BrowserStack
```properties
cloud.platform=browserstack
cloud.user=your_browserstack_username
cloud.key=your_browserstack_access_key

# Target Environment
os=Windows
os.version=11
browser=chromium
```

### For SauceLabs
```properties
cloud.platform=saucelabs
cloud.user=your_sauce_username
cloud.key=your_sauce_access_key

# Target Environment
os=Windows 11
browser=chromium
```

---

## 3. Execution

Once configured, you don't need to change any code. Run your tests as usual:

```bash
./gradlew webTest
```

The framework will:
1.  Detect `cloud.platform` is not `local`.
2.  Use `CapabilityBuilder` to construct the required capabilities.
3.  Establish a remote connection to the cloud grid.
4.  Execute the tests and report results.

---

## 4. Troubleshooting

- **Invalid Credentials**: Ensure `cloud.user` and `cloud.key` are correct.
- **Unsupported Platform**: Ensure the `os` and `os.version` strings match what the provider expects.
- **Connection Issues**: Ensure your network allows outbound connections to the cloud grid endpoints.
