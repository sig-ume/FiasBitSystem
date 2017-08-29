import java.util.Timer;
import java.util.TimerTask;

import jp.sigre.selenium.trade.TradeController;

public class SeleniumMain  {


    private volatile boolean isFirst = true;
    private volatile int taskNum = 1;

	public static void main(String[] args) {


		//TODO:Firefoxインストールチェック
		//TODO:PCスリープ、休止モード状態チェック

		//TODO：現行のLファイルをすべてDBに登録してみる

		//TODO:LongとShortの共通部分を抜き出す
		//TODO;Firebugでチェック→IntelliJでやる
		//TODO:SetupをログインとIniの読み込み別に
		//TODO;ログイン→買い→売り→ログアウトという順の処理にする
		//TODO:無駄に出力されるコメントを削除

		new SeleniumMain().loop();

        //timer.cancel();


	}

	public void loop() {
		TradeController trade = new TradeController();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // ここに繰り返し処理を書く
                System.out.println("action.");

        		boolean resultSetup = trade.tradeSetup();
        		//trade.tradeLong();
        		//selenium.tradeShort();

        		//trade.newTradeShort();
        		boolean resultTrade = false;
        		if (resultSetup) resultTrade = trade.trade();

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