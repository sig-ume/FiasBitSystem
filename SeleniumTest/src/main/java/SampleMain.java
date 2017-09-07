import java.io.IOException;

import jp.sigre.LogMessage;
import jp.sigre.selenium.trade.FileUtils;

/**
 * @author sigre
 *
 */
public class SampleMain {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		FileUtils file = new FileUtils();
		file.deleteKickFiles("C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\target\\deletekick");

		new LogMessage().writelnLog("test");

//		String basePath = System.getProperty("user.dir");
//
//		FileUtils file = new FileUtils();
//		File iniFile = new File(file.getIniPath(basePath));
//
//		//fbs.ini存在チェック
//		if (!iniFile.exists()) {
//			return;
//		}
//
//		IniBean iniBean = file.iniToBean(iniFile);
//
//		//File defFile = new File(basePath);
//		System.out.println(basePath 	 + File.separator + "target");
//
//		SeleniumTrade trade = new SeleniumTrade();
//
//		trade.login(iniBean.getID_FilePath(), "0");
//		//trade.getSBIStock(iniBean);
//		trade.logout();
//
//		ConnectDB db = new ConnectDB();
//		db.connectStatement();
//		for (TradeDataBean bean : db.getTradeViewOfCodeMethods_Unused("wildcard", "wildcard")) {
//			System.out.println(bean);
//		}
//		System.out.println();
//		SeleniumTrade trade = new SeleniumTrade();
//		for (TradeDataBean bean : trade.getUnusedMethodStockList(iniBean)) {
//			System.out.println(bean);
//		}
	}

}
