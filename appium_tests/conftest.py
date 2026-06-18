import pytest
import time
from appium import webdriver
from appium.options.common import AppiumOptions

@pytest.fixture(scope="module")
def driver():
    # Setup Appium connection options
    options = AppiumOptions()
    options.set_capability("platformName", "Android")
    options.set_capability("automationName", "UiAutomator2")
    options.set_capability("deviceName", "Android Emulator")
    options.set_capability("appPackage", "com.rct.app")
    options.set_capability("appActivity", "com.rct.app.activity.SplashActivity")
    options.set_capability("noReset", False)  # Start clean by default to test register/login
    options.set_capability("autoGrantPermissions", True)
    options.set_capability("newCommandTimeout", 120)
    
    appium_server_url = "http://localhost:4723"
    
    print(f"\nConnecting to Appium Server at {appium_server_url}...")
    try:
        driver = webdriver.Remote(appium_server_url, options=options)
        
        # Automatically select English language if LanguageActivity is presented
        try:
            from selenium.webdriver.support.ui import WebDriverWait
            from selenium.webdriver.support import expected_conditions as EC
            from selenium.webdriver.common.by import By
            btn_english = WebDriverWait(driver, 8).until(
                EC.presence_of_element_located((By.ID, "com.rct.app:id/btnEnglish"))
            )
            print("Language selection screen detected. Selecting English...")
            btn_english.click()
            # Wait for Login screen to load
            WebDriverWait(driver, 5).until(
                EC.presence_of_element_located((By.ID, "com.rct.app:id/etEmail"))
            )
            print("Successfully transitioned to Login screen.")
        except Exception as lang_err:
            print(f"No language selection screen found or error selecting language: {lang_err}")
            
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
