/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  docs: [
    {
      type: "doc",
      id: "index",
      label: "Introduction",
    },
    {
      type: "category",
      label: "Getting Started",
      collapsed: false,
      items: ["getting-started/quickstart"],
    },
    {
      type: "category",
      label: "Architecture",
      collapsed: false,
      items: ["architecture/overview"],
    },
    {
      type: "category",
      label: "User Guides",
      items: [
        "guides/locators",
        "guides/api-testing",
        "guides/pact-testing",
        "guides/reporting",
        "guides/code-quality",
        "guides/database",
        "guides/qa-engineers",
        "guides/developers",
        "guides/managers",
      ],
    },
    {
      type: "category",
      label: "Tutorials",
      items: [
        "tutorials/web-tests",
        "tutorials/api-tests",
        "tutorials/mobile-tests",
        "tutorials/contract-testing",
        "tutorials/cloud-execution",
        "tutorials/bdd-tests",
      ],
    },
    {
      type: "category",
      label: "API Reference",
      items: ["api/core-interfaces"],
    },
    {
      type: "category",
      label: "Best Practices",
      items: ["best-practices/test-design"],
    },
    {
      type: "category",
      label: "Troubleshooting",
      items: ["troubleshooting/common-issues"],
    },
    {
      type: "category",
      label: "Contributing",
      items: ["contributing/guidelines"],
    },
    {
      type: "doc",
      id: "changelog",
      label: "Changelog",
    },
  ],
};

export default sidebars;
