package jp.sigre.selenium.trade;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import jp.sigre.LogMessage;
import jp.sigre.database.ConnectDB;

public class SeleniumTrade {

	WebDriver driver = null;
	LogMessage log;

	public SeleniumTrade() {
		log = new LogMessage();
	}

	public WebDriver login(String strFolderPath, String visible) {
		FileUtils csv = new FileUtils();


		//idpassword.fbsのフルパスをFileUtilsから取得する
		String strIdPassPath = csv.getIdPassFilePath(strFolderPath);

		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));

		String strId = aryIdPass[0];
		String strLoginPass = aryIdPass[1];

		//System.setProperty("webdriver.gecko.driver", "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\lib\\geckodriver.exe");
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\lib\\geckodriver.exe");

		//FirefoxとPhantomJSの選択設定を追加
		if (visible.equals("1")) {
			driver = new FirefoxDriver();
		} else {
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability(
					PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					"C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\lib\\phantomjs.exe"
					);
			driver = new PhantomJSDriver(caps);
		}

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
		//ログアウト処理
		driver.findElement(By.id("logoutM")).click();
		log.writelnLog("SBIからのログアウトが完了しました。");
		driver.quit();
	}

	public List<TradeDataBean> buyStocks(List<TradeDataBean> beanList, String strIdFolderPath) {
		return tradeStocks(beanList, strIdFolderPath, true);
	}

	public List<TradeDataBean> sellStocks(List<TradeDataBean> beanList, String strIdFolderPath) {
		return tradeStocks(beanList, strIdFolderPath, false);
	}

	/**
	 * Sファイルのデータをもとに、売却用TradeData（株数入り）をDBから取得
	 * @param beanList Sファイルの中身
	 * @param strFolderPath
	 * @param isBuying
	 * @return 売却数入りTradeリスト
	 */
	public List<TradeDataBean> getSellData(List<TradeDataBean> beanList) {
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		for (int i = 0; i < beanList.size(); i++) {
			TradeDataBean bean = beanList.get(i);
			TradeDataBean tmpBean = db.getTradeViewOfCodeMethods(bean.getCode(), bean.getEntryMethod(), bean.getExitMethod());
			System.out.println("sellData:"+tmpBean);
			if (!bean.getRealEntryVolume().equals("0")) {
			bean.setRealEntryVolume(tmpBean.getRealEntryVolume());
			bean.setCorrectedEntryVolume(tmpBean.getCorrectedEntryVolume());
			} else {
				beanList.remove(i);
				i--;
			}
		}

		db.closeStatement();

		return beanList;

	}

	/**
	 * 売却用Tradeメソッド
	 * @param beanList ViewOfCodeMethodsから取得したレコードリスト
	 * @param strIdFolderPath
	 * @param isBuying
	 * @return 取引失敗リスト
	 */
	public List<TradeDataBean> newSellStocks(List<TradeDataBean> beanList, String strIdFolderPath) {

		boolean isBuying = false;

		//取引失敗レコードリスト
		List<TradeDataBean> failedTradeList = new ArrayList<>();

		TradeDataBean sumBean = null;

		//各株レコードごとに処理
		for (int i = 0; i < beanList.size(); ) {

			TradeDataBean firstBean = beanList.get(i);

			beanList.remove(i);
			System.out.println(firstBean);

			//同codeのレコードリスト作成
			List<TradeDataBean> sameCodeList = getSameCodeList(firstBean.getCode(), beanList);
			System.out.println("size=0?:" + sameCodeList.size());

			if (sameCodeList.size() != 0) {

				sameCodeList.add(firstBean);

				//売買それぞれの補正entryVolume計算
				calcCorrectedEntryVolume(sameCodeList, isBuying);

				//株数の合計取得
				sumBean = getSumBean(sameCodeList);

				//同コードリストをひとつのメソッドにまとめる
				hurikaeMethods(sameCodeList);

			} else {
				sumBean = firstBean;
			}

			System.out.println("sumBean:" + sumBean);
			//sumBean分Trade
			//sumBeanを単元とミニに分割

			//端数計算
			int hasuu = calcHasuu(sumBean);
			System.out.println("hasuu:" + hasuu);

			//端数があるときは端数分の購入レコードを追加
			if (hasuu>0) {
				sumBean.minusRealEntryVolume(hasuu);
				hurikaeHasuu(hasuu, firstBean);
			}

			List<TradeDataBean> tradeList = getTangennAndS(sumBean);

			//それぞれをトレード
			failedTradeList = trade(tradeList, strIdFolderPath, false);

		}

		return failedTradeList;
	}

	private TradeDataBean getSumBean(List<TradeDataBean> tradeList) {

		TradeDataBean result = tradeList.get(0).clone();

		int sum = Integer.parseInt(result.getRealEntryVolume());

		for (int i = 1; i < tradeList.size(); i++) {
			sum += Integer.parseInt(tradeList.get(i).getRealEntryVolume());
		}

		result.setRealEntryVolume(String.valueOf(sum));

		return result;
	}

	private int calcHasuu(TradeDataBean bean) {

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//code view から株数取得
		TradeDataBean beanCode = db.getTradeViewOfCode(bean.getCode());

		db.closeStatement();

		int stockVolume = Integer.parseInt(beanCode.getRealEntryVolume());
		int tmpSellVolume = Integer.parseInt(bean.getRealEntryVolume());


		//株数と売却数それぞれのMod 100を取得
		int fracStockVolume = stockVolume % 100;
		int fracTmpSellVolume = tmpSellVolume % 100;
		int fracSellVolume = 0;

		//端数計算は所有株数が100を超えてるときのみ
		if (stockVolume > 100) {


			System.out.println("株数と売却数:" + fracStockVolume + " " + fracTmpSellVolume);

			//if 株数Mod < 売却数Mod
			if (fracStockVolume < fracTmpSellVolume) {
				System.out.println("株数Mod < 売却数Mod");

				fracSellVolume = fracStockVolume;
			} else {
				System.out.println("株数Mod > 売却数Mod");
				//	売却端数を売却数Modに設定
				fracSellVolume = fracTmpSellVolume;
			}
			System.out.println("売却端数:" + fracSellVolume);

			return fracTmpSellVolume - fracSellVolume;
		}

		return 0;

	}

	private List<TradeDataBean> getTangennAndS(TradeDataBean bean) {
		List<TradeDataBean> result = new ArrayList<>();

		int volume = Integer.parseInt(bean.getRealEntryVolume());
		int sVolume = volume % 100;

		if (volume < 100) {
			bean.setMINI_CHECK_flg("1");
			result.add(bean);
		} else {

			if (sVolume != 0) {
				TradeDataBean sBean = bean.clone();
				sBean.setRealEntryVolume(String.valueOf(sVolume));
				sBean.setMINI_CHECK_flg("1");
				result.add(sBean);
			}

			TradeDataBean tangenBean = bean.clone();
			tangenBean.minusRealEntryVolume(sVolume);
			tangenBean.setMINI_CHECK_flg("0");
			result.add(tangenBean);
		}
		return result;
	}

	private void hurikaeMethods(List<TradeDataBean> list) {
		TradeDataBean firstBean = list.get(0).clone();

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		for (int i = 0; i < list.size(); i++) {
			TradeDataBean plusBean = firstBean.clone();
			TradeDataBean minusBean = list.get(i).clone();
			plusBean.setRealEntryVolume(minusBean.getRealEntryVolume());
			plusBean.setCorrectedEntryVolume(minusBean.getCorrectedEntryVolume());

			minusBean.setRealEntryVolume("-" + minusBean.getRealEntryVolume());
			minusBean.setCorrectedEntryVolume("-" + minusBean.getCorrectedEntryVolume());

			db.insertTradeData(minusBean);
			db.insertTradeData(plusBean);

		}

		db.closeStatement();
	}

	private void hurikaeHasuu(int hasuu, TradeDataBean firstBean) {
		ConnectDB db = new ConnectDB();

		db.connectStatement();
		TradeDataBean plusBean = db.getHighestTradeViewOfCodeMethods(
				firstBean.getCode(), firstBean.getEntryMethod(), firstBean.getExitMethod());
		System.out.println("plus;" + plusBean);
		String strHasuu = String.valueOf(hasuu);
		plusBean.setRealEntryVolume(strHasuu);
		plusBean.setCorrectedEntryVolume(strHasuu);

		TradeDataBean minusBean = firstBean.clone();
		minusBean.setRealEntryVolume("-" + strHasuu);
		minusBean.setCorrectedEntryVolume("-" + strHasuu);

		db.insertTradeData(minusBean);
		db.insertTradeData(plusBean);

		db.closeStatement();

	}


	private List<TradeDataBean> trade(List<TradeDataBean> beanList, String strIdFolderPath, boolean isBuying) {

		FileUtils csv = new FileUtils();

		//SBI取引パスワード取得
		String strIdPassPath = new FileUtils().getIdPassFilePath(strIdFolderPath);
		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));
		String strTorihPass = aryIdPass[2];
		List<TradeDataBean> failedTradeList = new ArrayList<>();

		for (TradeDataBean bean : beanList) {

			//売買それぞれの補正entryVolume計算
			calcCorrectedEntryVolume(bean, isBuying);

			if (bean.getMINI_CHECK_flg().equals("1")) {
				tradeSmallStock(bean, strTorihPass, isBuying);
			} else if (bean.getMINI_CHECK_flg().equals("0")){
				tradeNormalStocks(bean, strTorihPass, isBuying);
			}
			String strResult = getTradeResult(bean.getMINI_CHECK_flg());

			System.out.println(bean.getCode() + ": " + strResult);

			if (strResult.contains("ご注文を受け付けました。")
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



	/**
	 * リストの中から与えられたcodeのものを抜き出す
	 * @param code
	 * @param list
	 * @return
	 */
	private List<TradeDataBean> getSameCodeList(String code, List<TradeDataBean> list) {
		List<TradeDataBean> result = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			TradeDataBean target = list.get(i);
			if (target.getCode().equals(code)) {
				result.add(target);
				list.remove(i);
				i--;
			}
		}

		return result;
	}

	public List<TradeDataBean> tradeStocks(List<TradeDataBean> beanList, String strIdFolderPath, boolean isBuying) {
		FileUtils csv = new FileUtils();
		String strIdPassPath = new FileUtils().getIdPassFilePath(strIdFolderPath);

		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));

		String strTorihPass = aryIdPass[2];

		List<TradeDataBean> failedTradeList = new ArrayList<>();

		for (TradeDataBean bean : beanList) {

			//売買それぞれの補正entryVolume計算
			calcCorrectedEntryVolume(bean, isBuying);

			if (bean.getMINI_CHECK_flg().equals("1")) {
				tradeSmallStock(bean, strTorihPass, isBuying);
			} else if (bean.getMINI_CHECK_flg().equals("0")){
				tradeNormalStocks(bean, strTorihPass, isBuying);
			}
			String strResult = getTradeResult(bean.getMINI_CHECK_flg());

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

	private void calcCorrectedEntryVolume(List<TradeDataBean> list, boolean isBuying) {

		for (TradeDataBean bean : list) {
			calcCorrectedEntryVolume(bean, isBuying);
		}
	}

	private void calcCorrectedEntryVolume(TradeDataBean bean, boolean isBuying) {
		bean.setCorrectedEntryVolume(bean.getRealEntryVolume());
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

	private String getTradeResult(String strMiniFlg) {
		String strMsg = "";
		try{
			WebElement element = driver.findElement(By.name("FORM"));
			System.out.println(element.isEnabled());

			List<WebElement> elements = element.findElements(By.tagName("table"));
			System.out.println(elements.size());

			element = elements.get(2);
			System.out.println(element.isEnabled());

			if (strMiniFlg.equals("1")) element = element.findElement(By.tagName("font"));
			else if (strMiniFlg.equals("0")) element = element.findElement(By.className("mtext"));

			//element = element.findElement(By.tagName("b"));

			System.out.println(element.isEnabled());

			//WebElement element = driver.findElement(By.xpath("//form/table[3].0.0"));

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

	public void getSBIStock(IniBean iniBean) {


		String entryMethod = "";
		String exitMethod = "";

		for (String[] methodSet : iniBean.getMethodSet()) {

			if (methodSet[2].equals("2")) {
				entryMethod = methodSet[0];
				exitMethod = methodSet[1];
			}
		}

		driver.findElement(By.cssSelector("img[alt=\"ポートフォリオ\"]")).click();
		//middleAreaM2
		WebElement element = driver.findElement(By.className("middleAreaM2")).findElements(By.tagName("table")).get(5);
		if (!element.isEnabled()) {
			System.out.println("ページ番号テーブル取得失敗");
		} else {
			System.out.println("OK1");
		}
		element = element.findElement(By.className("mtext"));
		if (!element.isEnabled()) {
			System.out.println("mtxt取得失敗");
		} else {
			System.out.println("OK2");
		}
		element = element.findElement(By.tagName("b"));
		if (!element.isEnabled()) {
			System.out.println("b取得失敗");
		}

		System.out.println(element.getText());

		//		//1663 Ｋ＆Ｏエナジー
		//		element = driver.findElement(By.xpath("//tr[2]/td/table/tbody/tr[2]/td[2]"));
		//		System.out.println(element.getText());
		//
		//		//34
		//		element = driver.findElement(By.xpath("//tr[2]/td/table/tbody/tr[2]/td[4]"));
		//		System.out.println(element.getText());
		//
		//		//1811 銭高組
		//		element = driver.findElement(By.xpath("//tr[2]/td/table/tbody/tr[3]/td[2]"));
		//		System.out.println(element.getText());

		final String strXpath1 = "//tr[2]/td/table/tbody/tr[";
		final String strXpath2 = "]/td[";
		final String strXpath3 = "]";

		List<TradeDataBean> tradeList = new ArrayList<>();

		for (int i = 2; true; i++) {
			String strXpath = strXpath1 + i + strXpath2 + "2"+ strXpath3;
			try {
				element = driver.findElement(By.xpath(strXpath));
			} catch( org.openqa.selenium.NoSuchElementException e) {
				break;
			}

			String strCodeName = element.getText();

			String strCode = strCodeName.substring(0, 4);

			strXpath = strXpath1 + i + strXpath2 + "4"+ strXpath3;
			element = driver.findElement(By.xpath(strXpath));
			String strStockCount = element.getText().replace(",", "");

			System.out.println(strCodeName + ": " + strStockCount);
			TradeDataBean bean = new TradeDataBean();
			bean.setCode(strCode);
			bean.setCorrectedEntryVolume(strStockCount);
			bean.setDayTime("YYYY-MM-DD");
			bean.setEntry_money("10000");
			bean.setEntryMethod(entryMethod);
			bean.setExitMethod(exitMethod);
			bean.setMINI_CHECK_flg("2");
			bean.setRealEntryVolume(strStockCount);
			bean.setType("DD");
			System.out.println(bean.toString());

			tradeList.add(bean);
		}

		//TODO:DBをリセット前にCSVファイル出力
		//TODO：DBをリセット
		//TODO;tradeListをDBに投入
	}
}
