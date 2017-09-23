/**
 *
 */
package jp.sigre.database;

import java.io.File;
import java.util.List;

import jp.sigre.LogMessage;
import jp.sigre.selenium.trade.FileUtils;
import jp.sigre.selenium.trade.TradeDataBean;

/**
 * @author sigre
 *
 */
public class DbController {

	LogMessage log = new LogMessage();

	public void recovery() {

		File backupFile = new File("backup.csv");
		if (!backupFile.exists()) {
			log.writelnLog("バックアップファイルがありません。");
			return;
		}

		File dbFile = new File("db\\TradeInfo.sqlite");
		if (!dbFile.exists()) {
			log.writelnLog("DBファイルがありません。");
			return;
		}
		List<TradeDataBean> beanList = new FileUtils().csvToTorihikiData(backupFile);

		ConnectDB db = new ConnectDB();
		db.connectStatement();
		int count = db.deleteAllData();

		log.writelnLog(count + "件のデータを削除しました。");

		count = 0;
		for (TradeDataBean bean : beanList) {
			db.insertTradeData(bean);
			count++;
		}

		log.writelnLog(count + "件のデータを追加しました。");
	}

}
