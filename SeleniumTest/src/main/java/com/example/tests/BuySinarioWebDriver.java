//package com.example.tests;
//
//import java.util.regex.Pattern;
//import java.util.concurrent.TimeUnit;
//import org.junit.*;
//import static org.junit.Assert.*;
//import static org.hamcrest.CoreMatchers.*;
//import org.openqa.selenium.*;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.support.ui.Select;
//
//public class BuySinarioWebDriver {
//	private WebDriver driver;
//	private String baseUrl;
//	private boolean acceptNextAlert = true;
//	private StringBuffer verificationErrors = new StringBuffer();
//
//	@Before
//	public void setUp() throws Exception {
//		driver = new FirefoxDriver();
//		baseUrl = "https://site2.sbisec.co.jp/";
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//	}
//
//
//	public void testBuySinarioWebDriver() throws Exception {
//		driver.get(baseUrl + "/ETGate/?_ControlID=WPLEThmR001Control&_PageID=DefaultPID&_DataStoreID=DSWPLEThmR001Control&_ActionID=DefaultAID&getFlg=on");
//		driver.findElement(By.name("user_id")).clear();
//		driver.findElement(By.name("user_id")).sendKeys("Z87-0709018");
//		driver.findElement(By.name("user_password")).clear();
//		driver.findElement(By.name("user_password")).sendKeys("Beyond22");
//		driver.findElement(By.name("ACT_login")).click();
//		driver.findElement(By.cssSelector("img[alt=\"取引\"]")).click();
//		driver.findElement(By.linkText("単元未満株（S株）")).click();
//		driver.findElement(By.id("genK")).click();
//		driver.findElement(By.name("stock_sec_code")).clear();
//		driver.findElement(By.name("stock_sec_code")).sendKeys("1234");
//		driver.findElement(By.name("input_quantity")).clear();
//		driver.findElement(By.name("input_quantity")).sendKeys("50");
//		// ERROR: Caught exception [Error: Dom locators are not implemented yet!]
//		driver.findElement(By.name("odd_agreement")).click();
//		driver.findElement(By.name("trade_pwd")).clear();
//		driver.findElement(By.name("trade_pwd")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_estimate")).click();
//		driver.findElement(By.name("stock_sec_code")).clear();
//		driver.findElement(By.name("stock_sec_code")).sendKeys("7712");
//		driver.findElement(By.name("odd_agreement")).click();
//		driver.findElement(By.name("trade_pwd")).clear();
//		driver.findElement(By.name("trade_pwd")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_estimate")).click();
//		// ERROR: Caught exception [ERROR: Unsupported command [selectFrame |  | ]]
//		driver.findElement(By.id("toolbox-close")).click();
//		// ERROR: Caught exception [ERROR: Unsupported command [selectWindow | null | ]]
//		driver.findElement(By.linkText("S株（単元未満株）取引可能銘柄の確認方法を教えてください。 - SBI証券")).click();
//		driver.findElement(By.name("stock_sec_code")).clear();
//		driver.findElement(By.name("stock_sec_code")).sendKeys("９９８４");
//		driver.findElement(By.name("odd_agreement")).click();
//		driver.findElement(By.name("trade_pwd")).clear();
//		driver.findElement(By.name("trade_pwd")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_estimate")).click();
//		driver.findElement(By.name("stock_sec_code")).clear();
//		driver.findElement(By.name("stock_sec_code")).sendKeys("9984");
//		driver.findElement(By.name("trade_pwd")).clear();
//		driver.findElement(By.name("trade_pwd")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_estimate")).click();
//		driver.findElement(By.name("odd_agreement")).click();
//		driver.findElement(By.name("trade_pwd")).clear();
//		driver.findElement(By.name("trade_pwd")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_estimate")).click();
//		driver.findElement(By.name("input_quantity")).clear();
//		driver.findElement(By.name("input_quantity")).sendKeys("1");
//		driver.findElement(By.name("odd_agreement")).click();
//		driver.findElement(By.name("trade_pwd")).clear();
//		driver.findElement(By.name("trade_pwd")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_estimate")).click();
//		driver.findElement(By.name("ACT_place")).click();
//		driver.findElement(By.linkText("注文取消・訂正")).click();
//		driver.findElement(By.cssSelector("font.mtext-gray > a > u")).click();
//		driver.findElement(By.id("pwd3")).clear();
//		driver.findElement(By.id("pwd3")).sendKeys("EQZXUMWR");
//		driver.findElement(By.name("ACT_place")).click();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		driver.quit();
//		String verificationErrorString = verificationErrors.toString();
//		if (!"".equals(verificationErrorString)) {
//			fail(verificationErrorString);
//		}
//	}
//
//	private boolean isElementPresent(By by) {
//		try {
//			driver.findElement(by);
//			return true;
//		} catch (NoSuchElementException e) {
//			return false;
//		}
//	}
//
//	private boolean isAlertPresent() {
//		try {
//			driver.switchTo().alert();
//			return true;
//		} catch (NoAlertPresentException e) {
//			return false;
//		}
//	}
//
//	private String closeAlertAndGetItsText() {
//		try {
//			Alert alert = driver.switchTo().alert();
//			String alertText = alert.getText();
//			if (acceptNextAlert) {
//				alert.accept();
//			} else {
//				alert.dismiss();
//			}
//			return alertText;
//		} finally {
//			acceptNextAlert = true;
//		}
//	}
//}
