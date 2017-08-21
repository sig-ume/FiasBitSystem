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
		//TODO:PhantomJSｄｒｉｖｅｒの正常終了処理を確認（logがずっと掴まれている
		//TODO：https://www.google.co.jp/search?q=geckodriver+%E3%83%97%E3%83%AD%E3%82%BB%E3%82%B9+%E6%AE%8B%E3%82%8B&ie=utf-8&oe=utf-8&client=firefox-b&gfe_rd=cr&ei=URebWZnZMOPd8AeOirG4Cw

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

        		trade.tradeSetup();
        		trade.tradeLong();
        		//selenium.tradeShort();
        		trade.newTradeShort();
        		//trade.makeBackupFile();
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