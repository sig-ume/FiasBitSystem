import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

		//TODO:fbs.ini存在チェック

		FileUtils csv = new FileUtils();
		IniBean iniBean = csv.iniToBean(new File("C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\target\\fbs.ini"));

		//TODO:IniBean内のファイルパス存在チェック
		//TODO:売買メソッド無選択チェック

		//String strFolderPath = "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\target";
		String strFolderPath = iniBean.getlS_FilePath();

		new LogMessage().writeInLog("iniファイル読み込み完了", strFolderPath);

		String strFilePath = new FileUtils().getBuyDataFilePath(strFolderPath);

		File lFile = new File(strFilePath);

		List<TradeDataBean> beanList = csv.csvToTorihikiData(lFile);

		new TradeMethodFilter().longFilter(beanList, iniBean);

		//TODO:売買株の有無チェック
		//TODO:売買開始メッセージ


		SeleniumTrade trade =  new SeleniumTrade();

		trade.login(strFolderPath);

		List<TradeDataBean> failedList = trade.buyStocks(beanList, strFolderPath);

		trade.logout();

		String movedPath = new FileUtils().getMovedTradeDataPath(strFolderPath);
		try {
			new File(strFolderPath + File.separator + "old").mkdirs();
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
			} else {
				new File(strFilePath).delete();
			}
		} catch (SecurityException e) {
			new LogMessage().writeInLog(e.toString(), strFolderPath);
		} catch (IOException ioe) {
			new LogMessage().writeInLog(ioe.toString(), strFolderPath);
		}

		if (failedList.size()!=0) {
			new LogMessage().writeInLog("のこってるよー", strFolderPath);
			new FileUtils().makeTradeDataFile(failedList, strFolderPath);
		} else {
			new LogMessage().writeInLog("おわりだよー", strFolderPath);
		}

	}


}