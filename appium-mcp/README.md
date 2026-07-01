# Appium MCP: AI-Powered Mobile Test Automation

<img width="610" height="351" alt="Screenshot 2026-07-01 at 6 07 01 PM" src="https://github.com/user-attachments/assets/b0268e12-4a05-442b-8fee-8aab523eb6ea" />

This repo is a working demo of **Appium MCP** — an MCP server that wraps Appium's mobile automation capabilities as AI-agent-callable tools, letting you drive Android/iOS test sessions with natural-language prompts via Claude CLI or Cline-based AI-IDEs (Kiro, Cursor, Windsurf, Antigravity).

> Based on the companion blog: *Appium MCP: AI-Powered Mobile Test Automation*

## Table of Contents
- [What is MCP (Model Context Protocol)?](#what-is-mcp-model-context-protocol)
- [What is Appium MCP?](#what-is-appium-mcp)
- [Appium MCP Architecture](#appium-mcp-architecture)
- [Repo Layout](#repo-layout)
- [Setup](#setup)
- [Running Tests](#running-tests)
- [Code Generation with Appium MCP](#code-generation-with-appium-mcp)
- [Best Practices](#best-practices)

## What is MCP (Model Context Protocol)?

MCP is an open-source standard, introduced by Anthropic, for connecting AI applications to external systems — often called "USB-C for AI applications" since it gives AI models a universal connector to any external tool or service. AI assistants (Claude, ChatGPT, Google's Antigravity) and AI-native IDEs (Kiro, Cursor, VS Code, MCPJam) have all adopted it as a "build once, integrate everywhere" standard.

## What is Appium MCP?

Dubbed **"Appium on Steroids"**, Appium MCP is an MCP server built on top of Appium that exposes standard Appium commands as agent-callable tools:

- Create/end device sessions (Android UiAutomator2 / iOS XCUITest)
- Launch or restart an app by package name or bundle ID
- Find elements via natural language or accessibility-based detection
- Perform gestures (tap, swipe, scroll, long-press)
- Capture screenshots and dump the UI hierarchy

This lets an AI agent explore an app, generate locators, and author tests without ever opening Appium Inspector manually.

### Key Features

| Feature | Description |
|---|---|
| Cross-Platform Support | Automate Android (UiAutomator2) and iOS (XCUITest) on real devices, emulators, and simulators |
| Locate Elements with AI | AI-assisted element location using natural language descriptions and vision models |
| Priority-based Locator Generation | Tries locators in ranked order — Accessibility ID, Resource ID/Name, ClassName, XPath, etc. |
| Device Session Management | Create and manage sessions on local and cloud-based Android/iOS devices, emulators, and simulators |
| AI-Assisted Test Generation | Generate Appium test code from natural language via an AI assistant |
| AI-Assisted Page Object Model Support | Generate POM-based automation code using application context discovered at runtime |
| Multilingual Support | Automate using natural instructions in English, Spanish, Chinese, Japanese, Korean |

Appium MCP is open source (hosted on GitHub). At time of writing, latest versions: Appium MCP `1.86.4`, Appium `3.2.0`.

## Appium MCP Architecture

Appium MCP acts as a bridge between AI assistants and the Appium automation framework, following a layered flow:

```
AI Client -> MCP Protocol (JSON-RPC 2.0) -> MCP Server -> Underlying Service (Appium Server -> Device)
```

<img width="634" height="451" alt="Screenshot 2026-07-01 at 6 07 21 PM" src="https://github.com/user-attachments/assets/b5362833-3293-4611-8e56-a25f47439c70" />
Figure 1: Appium MCP Architecture (AI-generated diagram)
<p></p>

#### 1. AI Client Layer
AI assistants (Claude, ChatGPT, Gemini) and AI-native IDEs (Cursor, Kiro, Antigravity) interpret user intent from natural-language prompts.

  #### 2. MCP Client
  Embedded in the AI assistant/IDE; selects and invokes the appropriate MCP tool call, and relays responses back to the AI.

  #### 3. Appium MCP Server
  Exposes mobile automation as callable tools (`find_element`, `tap`, `swipe`, `take_screenshot`, etc.), converting invocations into standard WebDriver commands.

  #### 4. Appium Server
  Translates WebDriver-compliant commands into platform-specific instructions for the Device Under Test (DUT).

  #### 5. Device Layer
  The execution environment: emulators/simulators for local dev, or a cloud platform like **TestMu AI** for scalable, cross-device testing.

## Repo Layout

| Path | Purpose |
|---|---|
| `app/proverbial_android.apk`, `app/proverbial_ios.ipa` | Sample apps under test |
| `capabilities/lt-rd-capabilities.json` | Appium capabilities for parallel runs on TestMu AI real devices (Galaxy S24, Pixel 9) |
| `capabilities/lt-local-capabilities.json` | Appium capabilities for a local emulator run |
| `instructions/agent.md` | Natural-language test plan (setup / steps / assertions / teardown) the AI agent executes |
| `cline/cline_mcp_settings.json` | MCP server config for Cline-based AI-IDEs (Kiro, Cursor, etc.) |
| `tests-java/` | Generated Java/TestNG POM-based automation code |
| `.claude/settings.json` | MCP server config for Claude Code CLI |

## Setup

### Prerequisites
- Node.js (v22+)
- npm or yarn
- Java Development Kit (JDK 8+)
- Android SDK (for Android testing)
- Xcode, macOS only (for iOS testing)

### Install Appium and Appium MCP
```bash
npm install -g appium
appium --version

npm install -g appium-mcp
appium-mcp --help
# or run without a global install:
npx -y appium-mcp@latest
```

### Configure with Claude Code CLI

Install Claude Code CLI:
```bash
npm install -g @anthropic-ai/claude-code
```

If using Claude CLI, no separate MCP config file is needed — register the server directly:
```bash
claude mcp add appium-mcp -- npx -y appium-mcp@latest
```

Export TestMu AI credentials for cloud runs:
```bash
export LT_USERNAME="YOUR_LT_USERNAME"
export LT_ACCESS_KEY="YOUR_LT_ACCESS_KEY"
```

`~/.claude/settings.json` defines two MCP server entries — `appium-mcp-local` (points to `localhost:4723` over http, no credentials) and `appium-mcp-cloud` (points to `mobile-hub.lambdatest.com` over https, reads `$LT_USERNAME`/`$LT_ACCESS_KEY`):

```json
{
  "mcpServers": {
    "appium-mcp-local": {
      "command": "npx",
      "args": ["-y", "appium-mcp@latest"],
      "env": {
        "APPIUM_HOST": "localhost",
        "APPIUM_PORT": "4723",
        "APPIUM_PATH": "/",
        "APPIUM_PROTOCOL": "http"
      }
    },
    "appium-mcp-cloud": {
      "command": "npx",
      "args": ["-y", "appium-mcp@latest"],
      "env": {
        "APPIUM_HOST": "mobile-hub.lambdatest.com",
        "APPIUM_PORT": "80",
        "APPIUM_PATH": "/wd/hub",
        "APPIUM_PROTOCOL": "https",
        "LT_USERNAME": "${LT_USERNAME}",
        "LT_ACCESS_KEY": "${LT_ACCESS_KEY}"
      }
    }
  }
}
```

> **Security:** never hard-code `LT_USERNAME`/`LT_ACCESS_KEY` in the config file — always pass them as environment variables.

### Configure with Kiro / Cline-based AI-IDEs

Kiro, Cursor, Windsurf, and Antigravity are all VS Code-based, so the **Cline** extension configures Appium MCP identically across all of them:

1. In Kiro: `View → Extensions` → search for and install **Cline**.
2. Edit Cline's MCP settings file, e.g. `~/Library/Application Support/Kiro/User/globalStorage/saoudrizwan.claude-dev/settings/cline_mcp_settings.json` (see `cline/cline_mcp_settings.json` in this repo).
3. Export `LT_USERNAME` / `LT_ACCESS_KEY` in the Kiro terminal — the MCP server picks them up from the inherited shell environment (do not hard-code them in the JSON).

Tip: prefer free models (e.g. DeepSeek) in Cline for cost-effective execution over paid Cline credits.

## Running Tests

Upload the sample app to TestMu AI cloud:
```bash
curl -u "$LT_USERNAME:$LT_ACCESS_KEY" -X POST "https://manual-api.lambdatest.com/app/upload/realDevice" \
  -F "appFile=@proverbial_android.apk" -F "name=proverbial_android" -F "type=android"
```

With the returned App ID plugged into `capabilities/lt-rd-capabilities.json` and the test plan in `instructions/agent.md`, run tests on the TestMu AI cloud grid (in parallel across devices, e.g. Galaxy S24 and Pixel 9) with a single prompt in Claude Code CLI:

```
Read device capabilities from lt-rd-capabilities.json and trigger tests from instructions/agent.md
```
<img width="638" height="433" alt="Screenshot 2026-07-01 at 6 19 25 PM" src="https://github.com/user-attachments/assets/8b26a8b9-ebba-4bc4-8a13-abf8e3dc7d74" />
<p></p>
<img width="637" height="428" alt="Screenshot 2026-07-01 at 6 19 34 PM" src="https://github.com/user-attachments/assets/32752b54-916c-4b8a-9fbd-563bb547c361" />
<p></p>

*Test running in parallel on two real devices via the Appium MCP server, with results visible on the TestMu AI dashboard and status reflected in Claude Code CLI*

<img width="636" height="318" alt="Screenshot 2026-07-01 at 6 23 37 PM" src="https://github.com/user-attachments/assets/387e7d82-f09d-40e3-901e-1dc63207b585" /><p></p>
<img width="632" height="291" alt="Screenshot 2026-07-01 at 6 23 48 PM" src="https://github.com/user-attachments/assets/737a4e93-cb63-4807-953e-aad93baf2891" />
<p></p>

For a **local emulator/simulator** run, update `capabilities/lt-local-capabilities.json` (or launch one first, e.g. `emulator -avd Pixel_7_Android_34 -wipe-data`) and use the same `agent.md` with an adjusted prompt:

```
Read device capabilities from lt-local-capabilities.json and trigger tests from instructions/agent.md on local emulator
```

The same workflow (real device, emulator, cloud) applies equally to iOS apps.

<img width="586" height="550" alt="Screenshot 2026-07-01 at 6 25 08 PM" src="https://github.com/user-attachments/assets/0eaca017-0450-4ccc-ae73-2fabe5d81d1b" /><p></p>
*Same agent instructions executed successfully via Cline in Kiro*

## Code Generation with Appium MCP

Beyond execution, Appium MCP can generate automated test code from natural-language scenarios using the `appium_generate_tests` tool, discovering real element locators during the live session rather than guessing them.

Example prompt:
```
Generate a TestNG test script for the Proverbial Android app that clicks the COLOR, TEXT, and TOAST
buttons and asserts the toast message. Use POM design pattern and export the code in different files
```

This produces a maintainable Page Object Model structure (see `tests-java/`):
- A base test class for common setup/teardown
- A Page Object class with discovered locators and reusable methods
- A test class that consumes the Page Object for test scenarios

<img width="629" height="585" alt="Screenshot 2026-07-01 at 6 27 58 PM" src="https://github.com/user-attachments/assets/0abac2e7-320b-4570-91db-4a821ca4bb2a" />
*Generated Java/TestNG code organized into base test, Page Object, and test classes*

While `appium_generate_tests` natively targets Java/TestNG, Claude CLI or Cline can generate code in other languages/frameworks (e.g., Python pytest) based on the same session interactions.

## Best Practices

- **Optimize session management** — always create an Appium session before any MCP tool call; run multiple sessions in parallel on TestMu AI cloud for faster feedback on large suites.
- **Follow recommended tool usage** — use `appium_find_element` before tapping/typing/long-pressing; when an element is off-screen, use `appium_gesture` with `action=scroll_to_element` first:
  ```json
  {
    "tool": "appium_gesture",
    "arguments": {
      "action": "scroll_to_element",
      "strategy": "xpath",
      "selector": "//*[contains(@text,'My header')]",
      "direction": "down",
      "maxScrollAttempts": 40,
      "scrollDistancePreset": "medium"
    }
  }
  ```
- **Use AI-powered element detection when locators are unreliable** — set `AI_VISION_ENABLED=true` and prefer the `appium_ai` tool for visual element detection:
  ```json
  {
    "tool": "appium_ai",
    "arguments": {
      "action": "find_element",
      "instruction": "yellow search button at the bottom of the screen"
    }
  }
  ```
- **Keep separate MCP server entries for local vs. cloud** and never hard-code credentials in config files — pass them as environment variables.
- **Write clear agent instruction files** (`agent.md`) covering setup, steps, assertions, and teardown so the agent produces consistent runs. Always include teardown — end the Appium session and, for LambdaTest runs, `PATCH` the session status immediately after the last assertion:
  ```
  PATCH https://mobile-api.lambdatest.com/mobile-automation/api/v1/sessions/{sessionId}
  body: {"status_ind": "passed"} or {"status_ind": "failed"}
  ```
  Uninstall the app after ending local emulator sessions.
- **Pair with TestMu AI MCP for debugging** — fetch test details, command logs, and network logs directly from the TestMu AI cloud alongside Appium MCP for deeper triage.

## Have feedback or need assistance?
Feel free to fork the repo and contribute to make it better! Thanks to the awesome AltTester team for the great support that they provided throughout the course of the integration!

Email to [himanshu[dot]sheth[at]gmail[dot]com](mailto:himanshu.sheth@gmail.com) for any queries or ping me on the following social media sites:

<b>LinkedIn</b>: [@hjsblogger](https://linkedin.com/in/hjsblogger)<br/>
<b>Twitter</b>: [@hjsblogger](https://www.twitter.com/hjsblogger)