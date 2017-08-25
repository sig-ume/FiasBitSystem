import java.util.Timer;
import java.util.TimerTask;

import jp.sigre.selenium.trade.TradeController;

public class SeleniumMain  {


    private volatile boolean isFirst = true;
    private volatile int taskNum = 1;

	public static void main(String[] args) {

		//MEMO；売りの際は複数の特定株の複数メソッドのレコードをまとめる
		//まとめる際は合計からMod 100を取得、その値の売りレコードを追加するとともに、レコードの一つから追加分を減らす

		//TODO:Firefoxインストールチェック
		//TODO:PCスリープ、休止モード状態チェック
		//TODO:Selenium IDEインストールチェック

		//TODO：fbs key認証

		//TODO:引数によって動作するメソッドを変更する

		//TODO：現行のLファイルをすべてDBに登録してみる

		//TODO:LongとShortの共通部分を抜き出す
		//TODO;Firebugでチェク
		//TODO:売る際の端数計算で残る株数はそれ専用のentry,exitMethodを指定する
		//TODO:SetupをログインとIniの読み込み別に
		//TODO;ログイン→買い→売り→ログアウトという順の処理にする

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
        		if (resultSetup) trade.trade();
        		trade.makeBackupFile();
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