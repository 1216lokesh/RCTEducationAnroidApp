import pytest
import time
from appium import webdriver
from appium.options.common import AppiumOptions

@pytest.fixture(scope="function")
def driver():
    # Setup Appium connection options
    options = AppiumOptions()
    options.set_capability("platformName", "Android")
    options.set_capability("automationName", "UiAutomator2")
    options.set_capability("deviceName", "Android Emulator")
    options.set_capability("appPackage", "com.rct.app")
    options.set_capability("appActivity", "com.rct.app.activity.SplashActivity")
    options.set_capability("noReset", False)  # Start clean by default to test register/login
    options.set_capability("newCommandTimeout", 120)
    
    appium_server_url = "http://localhost:4723"
    
    print(f"\nConnecting to Appium Server at {appium_server_url}...")
    try:
        driver = webdriver.Remote(appium_server_url, options=options)
        yield driver
        print("Tearing down Appium Driver session...")
        driver.quit()
    except Exception as e:
        print(f"\nWARNING: Could not connect to Appium server: {e}")
        print("Ensure Appium server is running on port 4723 and emulator is connected.")
        # Provide a dummy fallback mock driver to prevent compilation errors during execution
        class MockDriver:
            def quit(self): pass
            def find_element(self, *args): raise Exception("Mock Appium Driver execution")
        yield MockDriver()
