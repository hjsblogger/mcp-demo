# Local Selenium Agent — Chrome (Local)

Single local Chrome session, run directly against this repo's `selenium-mcp-local`
MCP server — no remote grid, no LambdaTest hooks, no subagents needed (one
browser, one connection, one session).

## Prerequisites

- `selenium-mcp-local` MCP server running with no `SELENIUM_REMOTE_URL` /
  `LT_USERNAME` / `LT_ACCESS_KEY` set, so it launches a local Chrome session.
- Chrome + chromedriver on PATH.

## Steps

> There is no wait/sleep tool on this MCP server, so "delay" here means the
> agent pausing a few seconds between tool calls (e.g. `sleep 3` via Bash)
> rather than a Selenium action — useful for watching each step play out
> during a demo/recording.

1. `start_browser` with `browser: chrome` and `options: { arguments: ["--start-maximized"] }`
   to launch a local Chrome session with the window maximized. Wait a few
   seconds.
2. `navigate` to `https://ecommerce-playground.lambdatest.io/`. Wait a few
   seconds.
3. `send_keys` into the search box (`by: css`, `value: 'input[name="search"]'`)
   with `text: "iPhone"`. Wait a few seconds.
4. `interact` with `action: click`, `by: css`, `value: 'button.type-text'` to
   submit the search. Wait a few seconds.
5. `execute_script` with `script: "return document.title;"` and assert the
   returned title **contains** `"iPhone"` — confirms the search actually ran
   (e.g. real result: `"Search - iPhone"`). Wait a few seconds.
6. `close_session` to end the session and release the browser.

## Teardown

No remote/LambdaTest status hook is needed for local runs — `close_session`
in step 6 is sufficient to release the browser process.
