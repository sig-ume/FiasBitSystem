package jp.sigre.fbs.selenium.trade;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.utils.FileUtils;

public class SeleniumTrade {

	private WebDriver driver = null;
	private final LogMessage log;

	public SeleniumTrade() {
		log = new LogMessage();
	}

	public boolean login(String strFolderPath, String visible) {
		FileUtils csv = new FileUtils();


		//idpassword.fbsのフルパスをFileUtilsから取得する
		String strIdPassPath = csv.getIdPassFilePath(strFolderPath);

		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));

		String strId = aryIdPass[0];
		String strLoginPass = aryIdPass[1];

		InputStream geckoStream = this.getClass().getClassLoader().getResourceAsStream("lib/geckodriver.exe");

		if (geckoStream==null) {
			log.writelnLog("geckoDriverが取得できません。");
			return false;
		}

		ByteArrayOutputStream xxx = new ByteArrayOutputStream();
		byte[] buf = new byte[32768]; // この値は適当に変更してください
		int size = 0;

		try {
			while((size = geckoStream.read(buf, 0, buf.length)) != -1) {
				xxx.write(buf, 0, size);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			log.writelnLog(e.getMessage());
			return false;
		}

		//System.out.println("サイズ; " + xxx.size());

		System.setProperty("webdriver.gecko.driver", csv.getExePath(geckoStream, "geckodriver", ".exe"));

		//FirefoxとPhantomJSの選択設定を追加
		if (visible.equals("1")) {
			driver = new FirefoxDriver();
		} else {
			InputStream phantomStream = this.getClass().getClassLoader().getResourceAsStream("lib/phantomjs.exe");
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability(
					PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					csv.getExePath(phantomStream, "phantomJS", ".exe")
					);
			driver = new PhantomJSDriver(caps);
		}

		// 検索は8秒以内に終了して欲しい
		WebDriverWait waitForSearch = new WebDriverWait(driver, 8);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		WebElement element;

		driver.get("https://www.sbisec.co.jp/ETGate");

		element = driver.findElement(By.name("user_id"));

		element.sendKeys(new String[]{strId});

		element = driver.findElement(By.name("user_password"));

		element.sendKeys(new String[]{strLoginPass});

		element = driver.findElement(By.name("ACT_login"));

		element.click();

		try {
			waitForSearch.until(ExpectedConditions.presenceOfElementLocated(By.id("logout")));
		} catch (TimeoutException e) {
			log.writelnLog("ログインが失敗しました。インターネット接続、SBIサイトの状態をご確認ください。");
			return false;
		}

		log.writelnLog("SBIへのログインが完了しました。");
		System.out.println("Page title is: " + driver.getTitle());
		return true;
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
	 * @return 売却数入りTradeリスト
	 */
	public List<TradeDataBean> getSellData(List<TradeDataBean> beanList) {
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//beanList = removeUnusedWildcardBean(beanList);

		//wildcard(按分)レコード用リスト。最後にbeanListに追加。
		List<TradeDataBean> wildcardList = new ArrayList<>();

		for (int i = 0; i < beanList.size(); i++) {
			TradeDataBean bean = beanList.get(i);
			TradeDataBean tmpBean = db.getTradeViewOfCodeMethods(bean.getCode(), bean.getEntryMethod(), bean.getExitMethod());

			//Code、entryMethod、exitMethodが一致し、volumeが0じゃない場合、volumeを更新
			if (!bean.getRealEntryVolume().equals("0")) {
				bean.setRealEntryVolume(tmpBean.getRealEntryVolume());
				bean.setCorrectedEntryVolume(tmpBean.getCorrectedEntryVolume());
				bean.setMINI_CHECK_flg("2");
			} else {
				//volumeが0なら無視
				beanList.remove(i);
				i--;
			}

			//wildcardレコード捜索
			TradeDataBean wildBean = db.getTradeViewOfCodeMethods(bean.getCode(), "wildcard", "wildcard");
			if (!wildBean.getRealEntryVolume().equals("0")) {
				wildBean.setDayTime(bean.getDayTime());
				wildBean.setType(bean.getType());
				wildBean.setMINI_CHECK_flg("2");
				wildBean.setEntry_money(bean.getEntry_money());
				wildcardList.add(wildBean);
			}
			//wildcardListの重複排除。同コード別メソッドのレコードがある場合、上記捜索で複数同じものが入る
			wildcardList = wildcardList.stream().distinct().collect(Collectors.toList());

		}

		//wildcardをリストに追加
		beanList.addAll(wildcardList);

		//重複削除。entry,exitMethodがwildcardな売却指示がファイルにある場合、
		//上記の通常検索とwildcard検索で同じBeanが２つリストに含まれる
		beanList = beanList.stream().distinct().collect(Collectors.toList());


		db.closeStatement();

		return beanList;

	}

	/**
	 * 売却用Tradeメソッド
	 * @param beanList ViewOfCodeMethodsから取得したレコードリスト
	 * @param strIdFolderPath
	 * @return 取引失敗リスト
	 */
	public List<TradeDataBean> newSellStocks(List<TradeDataBean> beanList, String strIdFolderPath) {

		//取引失敗レコードリスト
		List<TradeDataBean> failedTradeList = new ArrayList<>();

		TradeDataBean sumBean;

		//各株レコードごとに処理
		for (int i = 0; i < beanList.size(); ) {

			TradeDataBean firstBean = beanList.get(i);

			beanList.remove(i);

			//同codeのレコードリスト作成
			List<TradeDataBean> sameCodeList = getSameCodeList(firstBean.getCode(), beanList);

			if (sameCodeList.size() != 0) {

				sameCodeList.add(firstBean);

				//売買それぞれの補正entryVolume計算
				calcCorrectedEntryVolume(sameCodeList);

				//株数の合計取得
				sumBean = getSumBean(sameCodeList);

				//同コードリストをひとつのメソッドにまとめる
				hurikaeMethods(sameCodeList);

			} else {
				sumBean = firstBean;
			}

			//sumBean分Trade
			//sumBeanを単元とミニに分割

			//端数計算
			int hasuu = calcHasuu(sumBean);

			//端数があるときは端数分の購入レコードを追加
			if (hasuu>0) {
				sumBean.minusRealEntryVolume(hasuu);
				hurikaeHasuu(hasuu, firstBean);
			}

			List<TradeDataBean> tradeList = getTangennAndS(sumBean);

			//それぞれをトレード
			failedTradeList.addAll(trade(tradeList, strIdFolderPath, false));
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
		int fracSellVolume;

		//端数計算は所有株数が100を超えてるときのみ
		if (stockVolume > 100) {

			//if 株数Mod < 売却数Mod
			if (fracStockVolume < fracTmpSellVolume) {

				fracSellVolume = fracStockVolume;
			} else {

				//	売却端数を売却数Modに設定
				fracSellVolume = fracTmpSellVolume;
			}

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

		for (int i = 1; i < list.size(); i++) {
			TradeDataBean plusBean = firstBean.clone();
			TradeDataBean minusBean = list.get(i).clone();

			boolean isEntryEquals = plusBean.getEntryMethod().equals(minusBean.getEntryMethod());
			boolean isExitEquals = plusBean.getExitMethod().equals(minusBean.getExitMethod());


			//振替前と振替後が同じメソッドである場合、スキップ
			if (isEntryEquals && isExitEquals) continue;

			plusBean.setRealEntryVolume(minusBean.getRealEntryVolume());
			plusBean.setCorrectedEntryVolume(minusBean.getCorrectedEntryVolume());

			minusBean.setRealEntryVolume("-" + minusBean.getRealEntryVolume());
			minusBean.setCorrectedEntryVolume("-" + minusBean.getCorrectedEntryVolume());

			db.insertTempTradeData(minusBean);
			db.insertTempTradeData(plusBean);

		}

		db.closeStatement();
	}

	//TODO：端数への振替をWildcardに
	private void hurikaeHasuu(int hasuu, TradeDataBean firstBean) {
		ConnectDB db = new ConnectDB();

		db.connectStatement();
		TradeDataBean plusBean = db.getHighestTradeViewOfCodeMethods(
				firstBean.getCode(), firstBean.getEntryMethod(), firstBean.getExitMethod());

		String strHasuu = String.valueOf(hasuu);
		plusBean.setRealEntryVolume(strHasuu);
		plusBean.setCorrectedEntryVolume(strHasuu);

		TradeDataBean minusBean = firstBean.clone();
		minusBean.setRealEntryVolume("-" + strHasuu);
		minusBean.setCorrectedEntryVolume("-" + strHasuu);

		db.insertTempTradeData(minusBean);
		db.insertTempTradeData(plusBean);

		db.closeStatement();

	}

	//TODO:売りメソッドのみ呼び出し？→引数isBuying削除
	private List<TradeDataBean> trade(List<TradeDataBean> beanList, String strIdFolderPath, boolean isBuying) {

		FileUtils csv = new FileUtils();

		//SBI取引パスワード取得
		String strIdPassPath = new FileUtils().getIdPassFilePath(strIdFolderPath);
		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));
		String strTorihPass = aryIdPass[2];
		List<TradeDataBean> failedTradeList = new ArrayList<>();

		for (TradeDataBean bean : beanList) {

			//売買それぞれの補正entryVolume計算
			calcCorrectedEntryVolume(bean);

			if (bean.getMINI_CHECK_flg().equals("1")) {
				tradeSmallStock(bean, strTorihPass, isBuying);
			} else if (bean.getMINI_CHECK_flg().equals("0")){
				tradeNormalStocks(bean, strTorihPass, isBuying);
			}
			String strResult = getTradeResult();


			if (strResult.contains("ご注文を受け付けました。")
					||strResult.contains("取引となります。") || strResult.contains("ご注文を受付いたします。")) {

				if (!isBuying) {
					bean.setCorrectedEntryVolume(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
					bean.setRealEntryVolume		(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
				}

				ConnectDB db = new ConnectDB();
				db.connectStatement();
				db.insertTempTradeData(bean);
				db.closeStatement();

			} else {
				failedTradeList.add(bean);
			}

			log.writelnLog(bean.getCode() + ":" + bean.getRealEntryVolume() + " " + strResult);
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

	private List<TradeDataBean> tradeStocks(List<TradeDataBean> beanList, String strIdFolderPath, boolean isBuying) {
		FileUtils csv = new FileUtils();
		String strIdPassPath = new FileUtils().getIdPassFilePath(strIdFolderPath);

		String[] aryIdPass = csv.csvToIdPass(new File(strIdPassPath));

		String strTorihPass = aryIdPass[2];

		List<TradeDataBean> failedTradeList = new ArrayList<>();

		for (TradeDataBean bean : beanList) {

			//売買それぞれの補正entryVolume計算
			calcCorrectedEntryVolume(bean);

			if (bean.getMINI_CHECK_flg().equals("1")) {
				tradeSmallStock(bean, strTorihPass, isBuying);
			} else if (bean.getMINI_CHECK_flg().equals("0")){
				tradeNormalStocks(bean, strTorihPass, isBuying);
			}
			String strResult = getTradeResult();


			if (strResult.contains("ご注文を受け付けました。")
					||strResult.contains("取引となります。") || strResult.contains("ご注文を受付いたします。")) {

				if (!isBuying) {
					bean.setCorrectedEntryVolume(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
					bean.setRealEntryVolume		(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
				}

				ConnectDB db = new ConnectDB();
				db.connectStatement();
				db.insertTempTradeData(bean);
				db.closeStatement();

			} else {

				failedTradeList.add(bean);
			}

			log.writelnLog(bean.getCode() + ":" + bean.getRealEntryVolume() + " " + strResult);
		}

		return failedTradeList;
	}

	private void calcCorrectedEntryVolume(List<TradeDataBean> list) {

		for (TradeDataBean bean : list) {
			calcCorrectedEntryVolume(bean);
		}
	}

	private void calcCorrectedEntryVolume(TradeDataBean bean) {
		bean.setCorrectedEntryVolume(bean.getRealEntryVolume());

	}

	private void tradeSmallStock(TradeDataBean bean, String strTorihPass, boolean isBuying) {
		//TODO：driver有効かチェック

		driver.findElement(By.cssSelector("img[alt=\"取引\"]")).click();

		driver.findElement(By.linkText("単元未満株")).click();
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


	private void tradeNormalStocks(TradeDataBean bean, String strTorihPass, boolean isBuying) {
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

	private String getTradeResult() {
		String strMsg;
		try{
			WebElement element = driver.findElement(By.name("FORM"));

			List<WebElement> elements = element.findElements(By.tagName("table"));

			element = elements.get(2);

			//if (strMiniFlg.equals("1")) element = element.findElement(By.tagName("font"));
			//else if (strMiniFlg.equals("0")) element = element.findElement(By.className("mtext"));

			element = element.findElement(By.tagName("b"));

			//WebElement element = driver.findElement(By.xpath("//form/table[3].0.0"));

			strMsg = element.getText();
		} catch (org.openqa.selenium.NoSuchElementException e) {
			strMsg = e.toString().split("\n")[0];
		}

		return strMsg;
	}

	public List<TradeDataBean> getSBIStock() {

		try {
			driver.findElement(By.cssSelector("img[alt=\"ポートフォリオ\"]")).click();
		} catch (NullPointerException e) {
			log.writelnLog("おそらくログインされていないかインターネットに接続されていません。");
			return new ArrayList<>();
		}

			//middleAreaM2
		WebElement element = driver.findElement(By.className("middleAreaM2")).findElements(By.tagName("table")).get(5);

		return getSBIStock(element);
	}

	public List<TradeDataBean> getSBIStock(WebElement element) {

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		if (!element.isEnabled()) {
			new LogMessage().writelnLog("ページ番号テーブル取得失敗");
		}
		element = element.findElement(By.className("mtext"));
		if (!element.isEnabled()) {
			new LogMessage().writelnLog("mtxt取得失敗");
		}
		element = element.findElement(By.tagName("b"));
		if (!element.isEnabled()) {
			new LogMessage().writelnLog("b取得失敗");
		}

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

			TradeDataBean bean = new TradeDataBean();
			bean.setCode(strCode);
			bean.setRealEntryVolume(strStockCount);

			tradeList.add(bean);
		}

		List<WebElement> elements = driver.findElements(By.linkText("次へ→"));
		if (elements.size()!=0) {

			//System.out.println("次へ");
			element = elements.get(0);
			element.click();

			element = driver.findElement(By.className("middleAreaM2")).findElements(By.tagName("table")).get(5);
			tradeList.addAll(getSBIStock(element));
		}

		//TODO:DBをリセット前にCSVファイル出力
		//TODO：DBをリセット
		//TODO;tradeListをDBに投入

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		return tradeList;


	}

	public List<TradeDataBean> getUnusedMethodStockList(IniBean iniBean) {

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		List<TradeDataBean> result = new ArrayList<>();

		for (String[] methods : iniBean.getMethodSet()) {
			if (methods[2].equals("0")) {
				result.addAll(db.getTradeViewOfCodeMethods_Unused(methods[0], methods[1]));
			}
		}

		db.closeStatement();

		return result;
	}

}
