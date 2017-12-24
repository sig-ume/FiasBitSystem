/**
 *
 */
package jp.sigre.fbs.database;

import java.io.File;
import java.util.List;

import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class DbController {

	LogMessage log = new LogMessage();

	public void recovery() {
		//TODO; Tempテーブルの扱いを決める（時間帯によって処理というか、リカバリする前に通常の処理を挟む

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
		int count = db.deleteAllTradeData();

		log.writelnLog(count + "件のデータを削除しました。");

		count = 0;
		for (TradeDataBean bean : beanList) {
			bean.setCorrectedEntryVolume(bean.getRealEntryVolume());
			db.insertTradeData(bean);
			count++;
		}

		log.writelnLog(count + "件のデータを追加しました。");
	}

}
