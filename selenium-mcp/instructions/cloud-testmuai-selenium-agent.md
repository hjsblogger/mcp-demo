# Cloud Selenium Agent — TestMu AI (LambdaTest) Grid

Parallel cloud Chrome and Firefox sessions, run directly against this repo's
`selenium-mcp-cloud` MCP server on the remote TestMu AI (LambdaTest) grid.

> **Why this needs subagents:** this MCP server tracks exactly one active
> browser session per connection (`state.currentSession` is a single
> pointer, not a per-call session argument). `start_browser` returns a
> `session_id`, but no tool accepts one back in — every subsequent tool call
> on a connection always acts on whatever session that connection started
> most recently. One connection literally cannot drive two browsers at
> once. To get real parallelism, spin up one MCP server connection **per
> browser**, each in its own subagent, and dispatch them together — that's
> also how TestMu AI actually parallelizes work: independent remote
> sessions on the grid, not multiple sessions multiplexed over one
> WebDriver connection.

## Prerequisites

- `selenium-mcp-cloud` MCP server running with `SELENIUM_REMOTE_URL`,
  `LT_USERNAME`, and `LT_ACCESS_KEY` set (see `.claude/settings.json`), so it
  launches sessions on the TestMu AI (LambdaTest) grid instead of a local
  browser.
- Valid TestMu AI (LambdaTest) account credentials exported as `LT_USERNAME`
  / `LT_ACCESS_KEY`.

## Setup (orchestrator)

Launch one subagent for Chrome and one for Firefox, **in the same
dispatch** — do not launch one, wait for it, then launch the other. Each
subagent gets its own `selenium-mcp-cloud` connection (its own
`state.currentSession`) and runs the full **Steps** and **Teardown**
sections below independently.

## Steps (run inside each subagent, against its own session)

> There is no wait/sleep tool on this MCP server, so "delay" here means the
> agent pausing between tool calls (e.g. `sleep 5` via Bash) rather than a
> Selenium action.

1. `start_browser` with `browser: chrome` (or `firefox` for the other
   subagent) and `options: { arguments: ["--start-maximized"], build:
   "TestMu AI - Selenium MCP Parallel Execution Demo", name:
   "<Browser>-Search-Checkout" }` to start a maximized cloud session on
   TestMu AI, tagged with the shared build name so both runs group together
   on the dashboard.
2. `navigate` to `https://ecommerce-playground.lambdatest.io/`.
3. `send_keys` into the search box (`by: css`, `value: 'input[name="search"]'`)
   with `text: "iPhone"`, then `interact` with `action: click`, `by: css`,
   `value: 'button.type-text'` to click the Search button.
4. Wait for the results page to load, then `execute_script` with
   `script: "return document.title;"` and verify the returned title
   **contains** `"iPhone"`. Fail the test if it doesn't.
5. `interact` with `action: click` on the first result's Add to Cart control
   (e.g. `by: xpath`, `value: '(//button[@title="Add to Cart"])[1]'`) to add
   the first item to the cart.
6. `interact` with `action: click` on the Checkout button (e.g. `by: css`,
   `value: 'a[href*="route=checkout/checkout"]'`).
7. Wait 5 seconds.
8. `execute_script` with `script: "return window.location.href;"` and assert
   the returned URL contains `"checkout"`. Fail the test if it doesn't.
9. Close the browser session at the end, whether the assertions in steps 4
   and 8 passed or failed.
10. Mark the test **Passed** if neither assertion failed; otherwise mark it
    **Failed**.

## Teardown (run inside each subagent, immediately after step 8)

Run this while the subagent's session is still active, back-to-back with
nothing in between — same as the Appium agent's REST PATCH-then-teardown
pattern:

1. `execute_script` with the LambdaTest hook to record the result:
   ```
   lambda-hook: {"action":"setTestStatus","arguments":{"status":"passed","remark":"<summary>"}}
   ```
   (use `"status":"failed"` and a failure remark if either assertion from
   steps 4 or 8 failed).
2. Immediately call `close_session` — this must run regardless of whether
   the assertions passed or failed.

Each subagent's connection ends here — do not reuse it for another browser.

## Summary (orchestrator)

Once both subagents have reported back, produce one combined summary
listing each browser's result: browser, session ID, TestMu AI dashboard
link, and Passed/Failed with a short reason. Report both entries together —
don't emit a summary per browser as it finishes.
