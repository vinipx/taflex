/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  docs: [
    {
      type: "category",
      label: "Documentation",
      collapsed: false,
      items: ["index", "getting-started/quickstart", "architecture/overview"],
    },
    {
      type: "category",
      label: "Tutorials",
      items: [
        "tutorials/web-tests",
        "tutorials/bdd-tests",
        "tutorials/api-tests",
        "tutorials/mobile-tests",
        "tutorials/contract-testing",
        "tutorials/cloud-execution",
      ],
    },
    {
      type: "category",
      label: "Core Guides",
      items: [
        "guides/reporting",
        "guides/code-quality",
        "guides/pact-testing",
        "guides/locators",
        "guides/api-testing",
        "guides/bdd-testing",
        "guides/cloud-testing",
        "guides/database",
        "guides/unit-testing",
      ],
    },
    {
      type: "category",
      label: "API Reference",
      items: ["api/core-interfaces"],
    },
    {
      type: "category",
      label: "Guides",
      items: [
        "guides/qa-engineers",
        "guides/developers",
        "guides/managers",
      ],
    },
    {
      type: "category",
      label: "Resources",
      items: [
        "best-practices/test-design",
        "troubleshooting/common-issues",
        "contributing/guidelines",
        "changelog",
      ],
    },
  ],
};

export default sidebars;
