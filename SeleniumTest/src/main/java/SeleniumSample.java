import java.io.File;
import java.util.List;

import jp.sigre.LogMessage;
import jp.sigre.database.ConnectDB;
import jp.sigre.selenium.trade.FileUtils;
import jp.sigre.selenium.trade.IniBean;
import jp.sigre.selenium.trade.TradeDataBean;
import jp.sigre.selenium.trade.TradeMethodFilter;

/**
 *
 */

/**
 * @author sigre
 *
 */
public class SeleniumSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//FileUtils csv = new FileUtils();

		//String strFolderPath = "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\target";
		//String strFolderPath = "C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\target";

		//		SeleniumTrade trade = new SeleniumTrade();
		//		WebDriver driver = trade.login(strFolderPath);
		//
		//		driver.findElement(By.cssSelector("img[alt=\"取引\"]")).click();
		//
		//		driver.findElement(By.linkText("単元未満株（S株）")).click();
		//
		//
		//		driver.findElement(By.name("ACT_place")).click();
		//
		//		//boolean strError = driver.findElement(By.name("FORM")).isEnabled();
		//
		//		System.out.println(trade.getTradeErrorResult());
		//
		//		driver.quit();
		//
		//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//		Date dateToday = new Date();
		//
		//		String strToday = sdf.format(dateToday);
		//
		//		String strFilePath = strFolderPath + "\\" + strToday + "_S.csv";
		//
		//		List<TradeDataBean> beanList = csv.csvToTorihikiData(new File(strFilePath));
		//
		//
		//		trade.sellStocks(beanList, strFolderPath);
		//
		//		trade.logout();

//		ConnectDB connect = new ConnectDB();
//		connect.connectStatement();
//		List<TradeDataBean> list = connect.getTradeViewOfCode();
//
//		for (TradeDataBean bean : list) {
//			System.out.println(bean.toString());
//		}
//		System.out.println();
//
//		list = connect.getTradeViewOfCodeExit();
//
//		for (TradeDataBean bean : list) {
//			System.out.println(bean.toString());
//		}
//		System.out.println();
//
//		list = connect.getTradeDataList();
//
//		for (TradeDataBean bean : list) {
//			System.out.println(bean.toString());
//		}

		FileUtils csv = new FileUtils();
		IniBean iniBean = csv.iniToBean(new File("C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\target\\fbs.ini"));

		//TODO:IniBean内のファイルパス存在チェック
		//TODO:売買メソッド無選択チェック

		//String strFolderPath = "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\target";
		String strFolderPath = iniBean.getlS_FilePath();

		new LogMessage().writeInLog("iniファイル読み込み完了", strFolderPath);

		//テスト用にLファイルを対象にする
		String strFilePath = new FileUtils().getBuyDataFilePath(strFolderPath);

		File lFile = new File(strFilePath);

		List<TradeDataBean> beanList = csv.csvToTorihikiData(lFile);//

		new TradeMethodFilter().longFilter(beanList, iniBean);

//		System.out.println(list.size());
//
//		LogMessage log = new LogMessage();
//
//		log.writeInLog("test", iniBean.getlS_FilePath());

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		for (TradeDataBean bean : beanList) {
			//TODO；下記実装

			//コード、entry, exitのViewからコード、exitでList取得 230
			//コードの売却株数取得
			//code view から株数取得 310
			//株数と売却数それぞれのMod 100を取得
			//if 株数Mod < 売却数Mod
			//	コードで一番株数が多いメソッド組を取得
			//	売却数Mod - 株数Modを上記メソッド組に加算
			//	売却端数を株数Modに設定
			//else
			//	売却端数を売却数Modに設定
			//Listの末尾のレコードのvolumeから売却端数を減らす
			//Listの末尾に上記と同じコード、entry,exitメソッドで売却端数分volumeのレコードを追加
			//売却レコードList完成したのでレコードごとに売却
			TradeDataBean bean1 = db.getTradeViewOfCodeMethods(bean.getCode() ,bean.getEntryMethod(), bean.getExitMethod());

			if (bean1 != null) {

			}
		}




	}

}
