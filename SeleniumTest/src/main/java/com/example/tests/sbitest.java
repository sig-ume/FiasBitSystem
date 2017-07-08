//package com.example.tests;
//
//import com.thoughtworks.selenium.Selenium;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.WebDriver;
//import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import java.util.regex.Pattern;
//import static org.apache.commons.lang3.StringUtils.join;
//
//public class sbitest {
//	private Selenium selenium;
//
//	@Before
//	public void setUp() throws Exception {
//		WebDriver driver = new FirefoxDriver();
//		String baseUrl = "https://www.sbisec.co.jp/ETGate";
//		selenium = new WebDriverBackedSelenium(driver, baseUrl);
//	}
//
//	@Test
//	public void testSbitest() throws Exception {
//		selenium.open("/ETGate");
//		selenium.type("name=user_id", "Z87-0709018");
//		selenium.click("name=user_password");
//		selenium.type("name=user_password", "beyond22");
//		selenium.click("name=ACT_login");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("css=img[alt=\"取引\"]");
//		selenium.waitForPageToLoad("30000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		selenium.stop();
//	}
//}
