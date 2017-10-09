package jp.sigre.fbs.main;
import java.io.File;
import java.io.IOException;

import jp.sigre.fbs.selenium.trade.FileUtils;
import jp.sigre.fbs.selenium.trade.IniBean;
import jp.sigre.fbs.selenium.trade.SeleniumTrade;
import jp.sigre.fbs.selenium.trade.TradeConsistency;

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


		String basePath = System.getProperty("user.dir");

		FileUtils file = new FileUtils();
		File iniFile = new File(file.getIniPath(basePath));

		//fbs.ini存在チェック
		if (!iniFile.exists()) {
			return;
		}

		IniBean iniBean = file.iniToBean(iniFile);

		//File defFile = new File(basePath);
		System.out.println(basePath 	 + File.separator + "target");

		SeleniumTrade trade = new SeleniumTrade();

		trade.login(iniBean.getID_FilePath(), "0");

		TradeConsistency cons = new TradeConsistency();
		cons.checkDbAndSbiStock(trade);
		cons.checkDbAndFiaKeep(iniBean.getLS_FilePath());

		//trade.getSBIStock(iniBean);
		trade.logout();


	}

}
