import java.util.Timer;
import java.util.TimerTask;

import jp.sigre.selenium.trade.TradeController;

public class SeleniumMain  {



	public static void main(String[] args) {


		//TODO:Firefoxインストールチェック
		//TODO:PCスリープ、休止モード状態チェック

		//TODO：現行のLファイルをすべてDBに登録してみる

		//TODO:LongとShortの共通部分を抜き出す
		//TODO;Firebugでチェック→IntelliJでやる
		//TODO:SetupをログインとIniの読み込み別に
		//TODO;ログイン→買い→売り→ログアウトという順の処理にする

		TradeController trade = new TradeController();
		boolean resultSetup = trade.tradeSetup();

		if (!resultSetup) return;

		new SeleniumMain().loop(trade);

        //timer.cancel();


	}

	public void loop(TradeController trade) {

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

        		if (resultTrade) trade.makeBackupFile();

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