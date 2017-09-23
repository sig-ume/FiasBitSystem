import java.util.Timer;
import java.util.TimerTask;

import jp.sigre.LogMessage;
import jp.sigre.database.DbController;
import jp.sigre.selenium.trade.TradeController;

public class SeleniumMain  {

	static LogMessage log = new LogMessage();

	static TradeController trade = new TradeController();

	public static void main(String[] args) {


		//TODO:Firefoxインストールチェック
		//TODO:PCスリープ、休止モード状態チェック

		//TODO;Firebugでチェック→IntelliJでやる
		//TODO:SetupをログインとIniの読み込み別に
		//TODO:DBファイルのパス設定を可能にする
		//TODO:当日売買した取引を記録するDBを作成する
		//TODO:不整合確認時のみタイムアップ時間を短縮

		boolean resultSetup = trade.tradeSetup();

		if (!resultSetup) return;

		if (args.length != 0) {
			if (args[0].equals("supermode")) {

				trade.makeDigestFile();
				log.writelnLog("Kickファイル作成処理終了");

				trade.backupDbFile();
				log.writelnLog("DBファイルバックアップ処理終了");

			} else if (args[0].equals("recovery")) {
				new SeleniumMain().recovery();
				return;

			}
		}

		new SeleniumMain().loop(trade);

		//timer.cancel();

		return;

	}


	private void recovery() {
		new DbController().recovery();
		log.writelnLog("リカバリ終了");

	}


	private void loop(TradeController trade) {

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// ここに繰り返し処理を書く
				System.out.println("action.");


				//trade.tradeLong();
				//selenium.tradeShort();

				//trade.newTradeShort();
				boolean resultTrade = trade.trade();

				if (resultTrade) {
					trade.makeBackupFile();
					trade.deleteOtherFiles();
				}

			}
		}, 0, 20 * 60 * 1000);

		// 5秒待つ
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}