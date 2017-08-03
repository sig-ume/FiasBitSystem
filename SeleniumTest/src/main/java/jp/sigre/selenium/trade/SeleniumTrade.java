package jp.sigre.selenium.trade;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import jp.sigre.LogMessage;
import jp.sigre.database.ConnectDB;

public class SeleniumTrade {

	WebDriver driver = null;
	String basePath;
	LogMessage log;

	public SeleniumTrade(String basePath) {
		this.basePath = basePath;
		log = new LogMessage(basePath);
	}

	public WebDriver login(String strFolderPath) {
		FileUtils csv = new FileUtils();


		//TODO:idpassword.fbsのフルパスをFileUtilsから取得する
		String strIdPassPath = strFolderPath + "\\" + "idpassword.fbs";

		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));

		String strId = aryIdPass[0];
		String strLoginPass = aryIdPass[1];

		//System.setProperty("webdriver.gecko.driver", "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\lib\\geckodriver.exe");
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\lib\\geckodriver.exe");
		driver = new FirefoxDriver();

		// 検索は8秒以内に終了して欲しい
		WebDriverWait waitForSearch = new WebDriverWait(driver, 8);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		WebElement element = null;

		driver.get("https://www.sbisec.co.jp/ETGate");

		element = driver.findElement(By.name("user_id"));

		element.sendKeys(new String[]{strId});

		element = driver.findElement(By.name("user_password"));

		element.sendKeys(new String[]{strLoginPass});

		element = driver.findElement(By.name("ACT_login"));

		element.click();

		waitForSearch.until(ExpectedConditions.presenceOfElementLocated(By.id("logout")));

		log.writelnLog("SBIへのログインが完了しました。");
		System.out.println("Page title is: " + driver.getTitle());

		return driver;

	}

	public void logout() {
		//TODO:ログアウト処理
		log.writelnLog("SBIからのログアウトが完了しました。");
		driver.quit();
	}

	public List<TradeDataBean> buyStocks(List<TradeDataBean> beanList, String strFolderPath) {
		return tradeStocks(beanList, strFolderPath, true);
	}

	public List<TradeDataBean> sellStocks(List<TradeDataBean> beanList, String strFolderPath) {
		return tradeStocks(beanList, strFolderPath, false);
	}

	public List<TradeDataBean> tradeStocks(List<TradeDataBean> beanList, String strFolderPath, boolean isBuying) {
		FileUtils csv = new FileUtils();
		String strIdPassPath = new FileUtils().getIdPassFilePath(strFolderPath);

		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));

		String strTorihPass = aryIdPass[2];

		List<TradeDataBean> failedTradeList = new ArrayList<>();

		for (TradeDataBean bean : beanList) {

			//TODO：売買それぞれの補正entryVolume計算
			calcCorrectedEntryVolume(bean, isBuying);

			if (bean.getMINI_CHECK_flg().equals("1")) {
				tradeSmallStock(bean, strTorihPass, isBuying);
			} else if (bean.getMINI_CHECK_flg().equals("0")){
				tradeNormalStocks(bean, strTorihPass, isBuying);
			}
			String strResult = getTradeCollectResult();

			System.out.println(bean.getCode() + ": " + strResult);

			if (strResult.contains("ご注文を受付いたします。")
					||strResult.contains("取引となります。")) {

				if (!isBuying) {
					bean.setCorrectedEntryVolume(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
					bean.setRealEntryVolume		(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
				}

				ConnectDB connect = new ConnectDB();
				connect.connectStatement();
				connect.insertTradeData(bean);
				connect.closeStatement();

			} else {

				failedTradeList.add(bean);
			}

			log.writelnLog(bean.getCode() + ":" + bean.getRealEntryVolume() + " " + strResult);;
		}

		return failedTradeList;
	}

	private void calcCorrectedEntryVolume(TradeDataBean bean, boolean isBuying) {
		if (isBuying) bean.setCorrectedEntryVolume(bean.getRealEntryVolume());
		else {
			//TODO:補正後売却株数計算処理
			//０．Sデータを使用する売買メソッドのみにフィルタリング
			//１．SデータをCode、ExitMethodごとにまとめてBean化→Code、ExitMethodごとの売却数取得（A）
			//２．TradeViewOfCodeExitからCode、ExitMethodをキーに所有株数を取得（B)
			//	→A>Bなら売却数をBに
			//	　A<Bなら売却数をAに
			//３．TradeViewOfCodeからCodeをキーに所有株数を取得（C)
			//４．BをCodeごとにまとめる→Codeごとの所有数を取得（D）
			//５．C=D→Cを売却
			//　　C>D→C Mod 100＞D Mod 100→Dを売却
			//	 C Mod 100＜D Mod 100→D - D Mod 100 + C Mod 100を売却
			//　　C<D→パターンなし
		}

	}

	public void tradeSmallStock(TradeDataBean bean, String strTorihPass, boolean isBuying) {
		//TODO：driver有効かチェック

		driver.findElement(By.cssSelector("img[alt=\"取引\"]")).click();

		driver.findElement(By.linkText("単元未満株（S株）")).click();
		//買い：genK、売り：genU
		if (isBuying) 	driver.findElement(By.id("genK")).click();
		else			driver.findElement(By.id("genU")).click();

		// ERROR: Caught exception [Error: Dom locators are not implemented yet!]
		driver.findElement(By.name("odd_agreement")).click();
		driver.findElement(By.name("stock_sec_code")).clear();
		driver.findElement(By.name("stock_sec_code")).sendKeys(new String[]{bean.getCode()});

		driver.findElement(By.name("input_quantity")).clear();
		driver.findElement(By.name("input_quantity")).sendKeys(new String[]{bean.getRealEntryVolume()});
		driver.findElement(By.name("trade_pwd")).clear();
		driver.findElement(By.name("trade_pwd")).sendKeys(new String[]{strTorihPass});
		driver.findElement(By.name("skip_estimate")).click();
		driver.findElement(By.name("ACT_place")).click();
	}


	public void tradeNormalStocks(TradeDataBean bean, String strTorihPass, boolean isBuying) {
		//TODO：driver有効かチェック
		driver.findElement(By.cssSelector("img[alt=\"取引\"]")).click();

		//買い：genK、売り：genU
		if (isBuying) 	driver.findElement(By.id("genK")).click();
		else			driver.findElement(By.id("genU")).click();

		driver.findElement(By.name("stock_sec_code")).clear();
		driver.findElement(By.name("stock_sec_code")).sendKeys(new String[]{bean.getCode()});
		driver.findElement(By.name("input_quantity")).clear();
		driver.findElement(By.name("input_quantity")).sendKeys(new String[]{bean.getRealEntryVolume()});
		driver.findElement(By.cssSelector("#gsn1 > input[name=\"in_sasinari_kbn\"]")).click();
		driver.findElement(By.id("pwd3")).clear();
		driver.findElement(By.id("pwd3")).sendKeys(new String[]{strTorihPass});
		driver.findElement(By.name("skip_estimate")).click();
		driver.findElement(By.name("ACT_place")).click();
	}

	public void sellSmallStocks() {

	}

	public void sellNormalStocks() {

	}

	public void getStockList() {

	}

	public String getTradeResult() {
		return "";
	}

	private String getTradeCollectResult() {
		String strMsg = "";
		try{
			WebElement element = driver.findElement(By.name("FORM"));
			System.out.println(element.isEnabled());

			List<WebElement> elements = element.findElements(By.tagName("table"));
			System.out.println(elements.size());

			element = elements.get(2);
			System.out.println(element.isEnabled());

			element = element.findElement(By.tagName("font"));
			System.out.println(element.isEnabled());

			strMsg = element.getText();
		} catch (org.openqa.selenium.NoSuchElementException e) {
			strMsg = e.toString().split("\n")[0];
		}

		return strMsg;
	}

	public String getTradeErrorResult() {
		WebElement element = driver.findElement(By.name("FORM"));
		System.out.println(element.isEnabled());

		List<WebElement> elements = element.findElements(By.tagName("table"));
		System.out.println(elements.size());

		element = elements.get(2);
		System.out.println(element.isEnabled());

		element = element.findElement(By.tagName("font"));
		System.out.println(element.isEnabled());

		String strError = element.getText();
		//String strError = driver.findElement(By.xpath("html/body/div/div/table/tbody")).findElement(By.tagName("d")).getText();

		return strError;
	}
}
