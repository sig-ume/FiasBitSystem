import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import jp.sigre.LogMessage;
import jp.sigre.selenium.trade.FileUtils;
import jp.sigre.selenium.trade.IniBean;
import jp.sigre.selenium.trade.SeleniumTrade;
import jp.sigre.selenium.trade.TradeDataBean;
import jp.sigre.selenium.trade.TradeMethodFilter;

public class SeleniumMain  {
	public static void main(String[] args) throws InterruptedException {

		//MEMO；売りの際は複数の特定株の複数メソッドのレコードをまとめる
		//まとめる際は合計からMod 100を取得、その値の売りレコードを追加するとともに、レコードの一つから追加分を減らす

		//TODO:Firefoxインストールチェック
		//TODO:PCスリープ、休止モード状態チェック
		//TODO:Selenium IDEインストールチェック
		//TODO:ログ出力先はiniファイルの配置先＝実行ファイルの配置先

		String basePath = System.getProperty("user.dir");

		LogMessage log = new LogMessage(basePath);

		FileUtils file = new FileUtils();
		File iniFile = new File(file.getIniPath(basePath));

		//fbs.ini存在チェック
		if (!iniFile.exists()) {
			return;
		}

		IniBean iniBean = file.iniToBean(iniFile);

		String strLsPath = iniBean.getlS_FilePath();
		String strIdPath = iniBean.getiD_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();

		if (!new File(strLsPath).isDirectory() ) {
			log.writelnLog("fbs.iniに指定されたLSファイル格納フォルダが存在しません。");
			return;
		}

		if (!new File(strIdPath).isDirectory()) {
			log.writelnLog("fbs.iniに指定されたIDファイル格納フォルダが存在しません。");
			return;
		}

		//売買メソッド無選択チェック
		if (intMethodCount == 0) {
			log.writelnLog("売買メソッドが1つも選択されていません。");
			return;
		}

		//String strFolderPath = "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\target";


		log.writelnLog("iniファイル読み込み開始");

		String strFilePath = new FileUtils().getBuyDataFilePath(strLsPath);

		File lFile = new File(strFilePath);

		File lRemFile = new File(strLsPath + File.separator + "buy_remains.csv");

		if (!lFile.exists() && !lRemFile.exists()) {
			log.writelnLog("LSファイル等の売買対象データファイルが存在しません。");
			return;
		}

		log.writelnLog("Iniファイルの内容を確認しました。");


		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(file.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(file.csvToTorihikiData(lRemFile));

		new TradeMethodFilter().longFilter(beanList, iniBean);

		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");
			return;
		}

		log.writelnLog("LSファイルの読み込みが完了しました。");

		log.writelnLog("LSファイルの移動、削除を開始します。");

		//ファイル削除は売買の前
		String movedPath = new FileUtils().getMovedBuyDataPath(strLsPath);
		try {
			new File(strLsPath + File.separator + "old").mkdirs();
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
			} else {
				new File(strFilePath).delete();
			}
			//remains削除
			lRemFile.delete();
		} catch (SecurityException e) {
			log.writelnLog(e.toString());
		} catch (IOException ioe) {
			log.writelnLog(ioe.toString());
		}

		log.writelnLog("LSファイルの移動、削除を行いました。");

		log.writelnLog("購入処理を開始します。");

		SeleniumTrade trade =  new SeleniumTrade(basePath);

		trade.login(strLsPath);

		List<TradeDataBean> failedList = trade.buyStocks(beanList, strLsPath);

		trade.logout();


		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			new FileUtils().removeTradeDataFile(strLsPath, true);
			//TradeData開業を追加
			new FileUtils().makeTradeDataFile(failedList, basePath, true);

			log.writelnLog("売買失敗件数：" + failedList.size());
		} else {
			log.writelnLog("おわりだよー");
		}

	}


}