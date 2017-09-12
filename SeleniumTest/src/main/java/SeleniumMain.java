import java.util.Timer;
import java.util.TimerTask;

import jp.sigre.selenium.trade.TradeController;

public class SeleniumMain  {



	public static void main(String[] args) {


		//TODO:Firefoxインストールチェック
		//TODO:PCスリープ、休止モード状態チェック

		//TODO;Firebugでチェック→IntelliJでやる
		//TODO:SetupをログインとIniの読み込み別に
		//TODO:DBファイルのパス設定を可能にする

		TradeController trade = new TradeController();
		boolean resultSetup = trade.tradeSetup();

		if (!resultSetup) return;

		new SeleniumMain().loop(trade);

        //timer.cancel();

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
        			trade.deleteKickFiles();
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