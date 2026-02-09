// @ts-check

import { themes as prismThemes } from "prism-react-renderer";

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: "TAFLEX",
  tagline: "Enterprise Test Automation Framework",
  favicon: "img/logo.svg",

  url: "https://vinipx.github.io",
  baseUrl: "/taflex/",

  organizationName: "vinipx",
  projectName: "taflex",

  onBrokenLinks: "warn",

  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  markdown: {
    mermaid: true,
    hooks: {
      onBrokenMarkdownLinks: "warn",
    },
  },

  themes: ["@docusaurus/theme-mermaid"],

  presets: [
    [
      "classic",
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: "./sidebars.js",
          editUrl: "https://github.com/vinipx/taflex/tree/main/documentation/",
          showLastUpdateTime: true,
        },
        blog: false,
        theme: {
          customCss: "./src/css/custom.css",
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      image: "img/taflex-social-card.png",

      mermaid: {
        theme: {
          light: "base",
          dark: "dark",
        },
        options: {
          themeVariables: {
            primaryColor: "#1a1a2e",
            primaryTextColor: "#e2e8f0",
            primaryBorderColor: "#3b82f6",
            lineColor: "#60a5fa",
            secondaryColor: "#1e1e2e",
            tertiaryColor: "#eff6ff",
          },
        },
      },

      announcementBar: {
        id: "taflex_v1",
        content:
          '⚡ TAFLEX — Unified Enterprise Test Automation for Web, API & Mobile. <a target="_blank" rel="noopener noreferrer" href="https://github.com/vinipx/taflex">Star us on GitHub</a>',
        backgroundColor: "#111111",
        textColor: "#d4d4d8",
        isCloseable: true,
      },

      colorMode: {
        defaultMode: "dark",
        disableSwitch: false,
        respectPrefersColorScheme: true,
      },

      navbar: {
        title: "TAFLEX",
        logo: {
          alt: "TAFLEX Logo",
          src: "img/logo.svg",
          srcDark: "img/logo-dark.svg",
          width: 36,
          height: 36,
        },
        items: [
          {
            type: "docSidebar",
            sidebarId: "docs",
            position: "left",
            label: "Documentation",
          },
          {
            to: "/docs/architecture/overview",
            label: "Architecture",
            position: "left",
          },
          {
            to: "/docs/api/core-interfaces",
            label: "API",
            position: "left",
          },
          {
            href: "https://github.com/vinipx/taflex",
            label: "GitHub",
            position: "right",
          },
        ],
      },

      footer: {
        style: "dark",
        links: [
          {
            title: "Documentation",
            items: [
              { label: "Getting Started", to: "/docs/getting-started/quickstart" },
              { label: "Architecture", to: "/docs/architecture/overview" },
              { label: "API Reference", to: "/docs/api/core-interfaces" },
            ],
          },
          {
            title: "Guides",
            items: [
              { label: "QA Engineers", to: "/docs/guides/qa-engineers" },
              { label: "Developers", to: "/docs/guides/developers" },
              { label: "Managers", to: "/docs/guides/managers" },
            ],
          },
          {
            title: "Resources",
            items: [
              { label: "Best Practices", to: "/docs/best-practices/test-design" },
              { label: "Troubleshooting", to: "/docs/troubleshooting/common-issues" },
              { label: "Changelog", to: "/docs/changelog" },
            ],
          },
          {
            title: "Links",
            items: [
              { label: "GitHub", href: "https://github.com/vinipx/taflex" },
              { label: "Contributing", to: "/docs/contributing/guidelines" },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} TAFLEX — MIT License`,
      },

      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: [
          "java",
          "groovy",
          "bash",
          "json",
          "properties",
          "yaml",
          "markup",
          "csv",
        ],
      },

      tableOfContents: {
        minHeadingLevel: 2,
        maxHeadingLevel: 4,
      },
    }),
};

export default config;
