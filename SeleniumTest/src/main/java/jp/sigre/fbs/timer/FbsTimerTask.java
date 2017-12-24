package jp.sigre.fbs.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.TradeController;

/**
 * @author sigre
 *
 */

public class FbsTimerTask extends TimerTask {

	TradeController trade = null;

	public FbsTimerTask(TradeController controller) {
		this.trade = controller;
	}

	@Override
	public void run() {
		// ここに繰り返し処理を書く

		if (trade == null) {
			new LogMessage().writelnLog("FbsControllerが設定されていません。処理を終了します。");
			return;
		}

		System.out.println("action.");


		//trade.tradeLong();
		//selenium.tradeShort();

		//trade.newTradeShort();
		boolean resultTrade = trade.trade();

		if (resultTrade) {
			trade.makeBackupFile();
			trade.deleteOtherFiles();
		}

		Calendar cal2 = Calendar.getInstance();
		Date date = new Date();
		cal2.setTime(date);
		cal2.add(Calendar.MINUTE, 20);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");


		new LogMessage().writelnLog("一連の処理を完了しました。次の動作は" + sdf.format(cal2.getTime()) + "頃です。");

	}
}
