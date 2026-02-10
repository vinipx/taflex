import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import styles from './index.module.css';

function HeroBanner() {
  const { siteConfig } = useDocusaurusContext();
  return (
    <header className={styles.heroBanner}>
      <div className="container">
        <div className={styles.heroContent}>
          <div className={styles.heroBadge}>Enterprise Test Automation</div>
          <h1 className={styles.heroTitle}>{siteConfig.title}</h1>
          <p className={styles.heroSubtitle}>
            Unified Test Automation Framework for Web, API &amp; Mobile
          </p>
          <p className={styles.heroDescription}>
            A modern, enterprise-grade framework powered by Java 21+, the Strategy
            Pattern, and externalized configuration — delivering unified test
            orchestration across every platform with zero code changes.
          </p>
          <div className={styles.heroButtons}>
            <Link className={styles.heroPrimary} to="/docs/getting-started/quickstart">
              Get Started →
            </Link>
            <Link className={styles.heroSecondary} to="/docs/architecture/overview">
              View Architecture
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
}

const capabilityFeatures = [
  {
    badge: 'WEB',
    color: '#2563eb',
    title: 'Web Testing (Playwright)',
    description:
      'Playwright-powered browser automation with externalized locators, auto-wait strategies, and cross-browser support out of the box.',
  },
  {
    badge: 'API',
    color: '#22c55e',
    title: 'API Testing (REST)',
    description:
      'First-class REST API support with typed response handling, fluent assertions, and seamless integration with the unified driver interface.',
  },
  {
    badge: 'MOBILE',
    color: '#8b5cf6',
    title: 'Mobile Testing (Appium)',
    description:
      'Native and hybrid mobile testing via Appium with shared locator management, parallel device execution, and cloud lab integration.',
  },
  {
    badge: 'DATA',
    color: '#f59e0b',
    title: 'Database Integration',
    description:
      'HikariCP connection pooling with JDBC wrapper for test data setup, validation, and teardown — fully integrated into the test lifecycle.',
  },
];

const coreFeatures = [
  {
    icon: '🧩',
    title: 'Strategy Pattern Architecture',
    description:
      'Runtime driver resolution lets you switch between Web, API, and Mobile contexts without changing test code. One interface, multiple implementations.',
  },
  {
    icon: '📄',
    title: 'Externalized Locators',
    description:
      'All selectors stored in .properties files, completely decoupled from test logic. Change locators without touching a single test class.',
  },
  {
    icon: '⚡',
    title: 'Parallel Execution',
    description:
      'Built-in parallel test execution with TestNG thread management for maximum throughput on CI/CD pipelines and local development.',
  },
  {
    icon: '📊',
    title: 'Rich Reporting',
    description:
      'Native ReportPortal integration with detailed test analytics, dashboards, and failure analysis for enterprise visibility.',
  },
  {
    icon: '🔧',
    title: 'Gradle Build System',
    description:
      'Modern Gradle build with dependency management, custom tasks, and seamless IDE integration for a productive developer experience.',
  },
  {
    icon: '🚀',
    title: 'CI/CD Ready',
    description:
      'GitHub Actions workflows, Docker support, and environment-based configuration for seamless continuous integration and deployment.',
  },
];

const techStack = [
  { name: 'Java 21+', desc: 'Latest LTS' },
  { name: 'Playwright', desc: 'Web' },
  { name: 'Appium', desc: 'Mobile' },
  { name: 'TestNG', desc: 'Test Runner' },
  { name: 'Gradle', desc: 'Build System' },
  { name: 'HikariCP', desc: 'DB Pool' },
  { name: 'ReportPortal', desc: 'Reporting' },
  { name: 'Docker', desc: 'Containers' },
];

function CapabilitiesSection() {
  return (
    <section className={styles.capabilities}>
      <div className="container">
        <div className={styles.sectionHeader}>
          <h2>Multi-Platform Coverage</h2>
          <p>Test across every platform channel in a single unified framework</p>
        </div>
        <div className={styles.capabilityGrid}>
          {capabilityFeatures.map((item, idx) => (
            <div key={idx} className={styles.capabilityCard}>
              <span className={styles.capabilityBadge} style={{ backgroundColor: item.color }}>
                {item.badge}
              </span>
              <h3>{item.title}</h3>
              <p>{item.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function ArchitectureSection() {
  return (
    <section className={styles.architecture}>
      <div className="container">
        <div className={styles.sectionHeader}>
          <h2>Strategy-Driven Architecture</h2>
          <p>Clean separation of concerns with runtime driver resolution</p>
        </div>
        <div className={styles.archDiagram}>
          <div className={styles.archLayer} data-layer="4">
            <div className={styles.archLabel}>Layer 4 — Test Definition</div>
            <div className={styles.archClasses}>
              TestNG · Test Classes · Assertions · ReportPortal
            </div>
          </div>
          <div className={styles.archLayer} data-layer="3">
            <div className={styles.archLabel}>Layer 3 — Configuration</div>
            <div className={styles.archClasses}>
              .properties Locators · Environment Config · Driver Factory
            </div>
          </div>
          <div className={styles.archLayer} data-layer="2">
            <div className={styles.archLabel}>Layer 2 — Strategy Core</div>
            <div className={styles.archClasses}>
              DriverStrategy Interface · Runtime Resolution · Context Manager
            </div>
          </div>
          <div className={styles.archLayer} data-layer="1">
            <div className={styles.archLabel}>Layer 1 — Platform Adapters</div>
            <div className={styles.archClasses}>
              Playwright (Web) · REST Client (API) · Appium (Mobile) · JDBC (DB)
            </div>
          </div>
        </div>
        <div className={styles.archCta}>
          <Link to="/docs/architecture/overview">Explore Full Architecture →</Link>
        </div>
      </div>
    </section>
  );
}

function FeaturesSection() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className={styles.sectionHeader}>
          <h2>Built for Enterprise</h2>
          <p>Production-grade capabilities for mission-critical test automation</p>
        </div>
        <div className={styles.featuresGrid}>
          {coreFeatures.map((item, idx) => (
            <div key={idx} className={styles.featureCard}>
              <div className={styles.featureIcon}>{item.icon}</div>
              <h3>{item.title}</h3>
              <p>{item.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function TechStackSection() {
  return (
    <section className={styles.techStack}>
      <div className="container">
        <div className={styles.sectionHeader}>
          <h2>Technology Stack</h2>
          <p>Modern, battle-tested libraries for enterprise reliability</p>
        </div>
        <div className={styles.techGrid}>
          {techStack.map((item, idx) => (
            <div key={idx} className={styles.techPill}>
              <span className={styles.techName}>{item.name}</span>
              <span className={styles.techDesc}>{item.desc}</span>
            </div>
          ))}
        </div>
        <div className={styles.archCta}>
          <Link to="/docs/getting-started/quickstart">View Full Setup Guide →</Link>
        </div>
      </div>
    </section>
  );
}

function QuickStartSection() {
  return (
    <section className={styles.quickStart}>
      <div className="container">
        <div className={styles.sectionHeader}>
          <h2>Quick Start</h2>
          <p>Up and running in under a minute</p>
        </div>
        <div className={styles.codeBlock}>
          <pre>
            <code>{`# Clone the repository
git clone https://github.com/vinipx/taflex.git
cd taflex

# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run with a specific driver strategy
./gradlew test -Ddriver.strategy=web`}</code>
          </pre>
        </div>
      </div>
    </section>
  );
}

export default function Home() {
  const { siteConfig } = useDocusaurusContext();
  return (
    <Layout
      title="Enterprise Test Automation Framework"
      description="TAFLEX — Unified, enterprise-grade test automation framework for Web, API, and Mobile. Java 21+, Strategy Pattern, externalized configuration."
    >
      <HeroBanner />
      <main>
        <CapabilitiesSection />
        <ArchitectureSection />
        <FeaturesSection />
        <TechStackSection />
        <QuickStartSection />
      </main>
    </Layout>
  );
}
