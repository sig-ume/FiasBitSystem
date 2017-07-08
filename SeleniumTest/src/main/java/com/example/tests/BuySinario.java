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
//public class BuySinario {
//	private Selenium selenium;
//
//	//やること
//	//・Firefoxインストール確認、Seleniumインストる確認
//	//・インプットファイル確認
//	//・Dropboxファイル確認
//	//・エラーメッセージ一覧作成、Seleniumでの検知方法確認
//	//・自動スリープ無効化チェック
//
//	@Before
//	public void setUp() throws Exception {
//		WebDriver driver = new FirefoxDriver();
//		String baseUrl = "https://site2.sbisec.co.jp/";
//		selenium = new WebDriverBackedSelenium(driver, baseUrl);
//	}
//
//	@Test
//	public void testBuySinario() throws Exception {
//		selenium.open("/ETGate/?_ControlID=WPLEThmR001Control&_PageID=DefaultPID&_DataStoreID=DSWPLEThmR001Control&_ActionID=DefaultAID&getFlg=on");
//		selenium.type("name=user_id", "Z87-0709018");
//		selenium.type("name=user_password", "Beyond22");
//		selenium.click("name=ACT_login");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("css=img[alt=\"取引\"]");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("link=単元未満株（S株）");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("id=genK");
//		selenium.type("name=stock_sec_code", "1234");
//		selenium.type("name=input_quantity", "50");
//		selenium.click("document.FORM.hitokutei_trade_kbn[1]");
//		selenium.click("name=odd_agreement");
//		selenium.type("name=trade_pwd", "EQZXUMWR");
//		selenium.click("name=ACT_estimate");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("name=stock_sec_code", "7712");
//		selenium.click("name=odd_agreement");
//		selenium.type("name=trade_pwd", "EQZXUMWR");
//		selenium.click("name=ACT_estimate");
//		selenium.waitForPageToLoad("30000");
//		selenium.selectFrame("");
//		selenium.click("id=toolbox-close");
//		selenium.waitForPageToLoad("30000");
//		selenium.selectWindow("null");
//		selenium.click("link=S株（単元未満株）取引可能銘柄の確認方法を教えてください。 - SBI証券");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("name=stock_sec_code", "９９８４");
//		selenium.click("name=odd_agreement");
//		selenium.type("name=trade_pwd", "EQZXUMWR");
//		selenium.click("name=ACT_estimate");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("name=stock_sec_code", "9984");
//		selenium.type("name=trade_pwd", "EQZXUMWR");
//		selenium.click("name=ACT_estimate");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("name=odd_agreement");
//		selenium.type("name=trade_pwd", "EQZXUMWR");
//		selenium.click("name=ACT_estimate");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("name=input_quantity", "1");
//		selenium.click("name=odd_agreement");
//		selenium.type("name=trade_pwd", "EQZXUMWR");
//		selenium.click("name=ACT_estimate");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("name=ACT_place");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("link=注文取消・訂正");
//		selenium.waitForPageToLoad("30000");
//		selenium.click("css=font.mtext-gray > a > u");
//		selenium.waitForPageToLoad("30000");
//		selenium.type("id=pwd3", "EQZXUMWR");
//		selenium.click("name=ACT_place");
//		selenium.waitForPageToLoad("30000");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		selenium.stop();
//	}
//}
