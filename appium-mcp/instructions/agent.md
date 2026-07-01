## Test: Login Flow

  ### Setup
  - Read lt-rd-capabilities.json — it contains an array of capability objects, one per device
  - For each entry in the array, create a separate Appium session on TestMu AI (run all sessions in parallel)
  - Track each session ID against its capability entry for teardown

  ### Steps
  Run the following steps for every active session:
  1. Take a screenshot of the home screen
  2. Find the COLOR button and click on it
  3. Find the TEXT button and click on it
  4. Take a screenshot of the current screen
  5. Find the TOAST button and click on it
  6. Assert if the toast message is not visible

  ### Teardown
  For each session:
  - If executing on LambdaTest, immediately after the last assertion (while the session is still active) call the LambdaTest REST API to mark the test status:
    PATCH https://mobile-api.lambdatest.com/mobile-automation/api/v1/sessions/{sessionId}
    body: {"status_ind": "passed"} or {"status_ind": "failed"}
    Do this in the same step as ending the session — do NOT wait or do anything else in between.
  - End the Appium session
  - If executing on a local emulator, uninstall the app from the device after the session is ended
