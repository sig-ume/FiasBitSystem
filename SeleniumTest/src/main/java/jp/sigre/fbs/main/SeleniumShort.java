package jp.sigre.fbs.main;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.IniBean;
import jp.sigre.fbs.selenium.trade.SeleniumTrade;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.fbs.selenium.trade.TradeMethodFilter;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class SeleniumShort {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String basePath = System.getProperty("user.dir");

		LogMessage log = new LogMessage(basePath);

		FileUtils csv = new FileUtils();
		IniBean iniBean = csv.iniToBean(new File("C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\fbs.ini"));

		//TODO:iniファイルチェックをメソッド化

		String strLsPath = iniBean.getLS_FilePath();
		String strIdPath = iniBean.getID_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();

		if (!new File(strLsPath).isDirectory() ) {
			log.writelnLog("fbs.iniに指定されたLSファイル格納フォルダが存在しません。");
			return;
		}

		if (!new File(strIdPath).isDirectory()) {
			log.writelnLog("fbs.iniに指定されたIDファイル格納フォルダが存在しません。");
			return;
		}

		//売買メソッド無選択チェック
		if (intMethodCount == 0) {
			log.writelnLog("売買メソッドが1つも選択されていません。");
			return;
		}

		String tradeVisible = iniBean.getTradeVisible();

		log.writelnLog("iniファイル読み込み完了");

		//テスト用にLファイルを対象にする→しない
		String strFilePath = new FileUtils().getSFilePath(strLsPath);

		File lFile = new File(strFilePath);

		//TODO:ファイルが無い場合の例外処理
		List<TradeDataBean> beanList = csv.csvToTorihikiData(lFile);//

		new TradeMethodFilter(iniBean).shortFilter(beanList);

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//TODO：処理終了後のファイル処理をFileUtilsでメソッド化

		String movedPath = new FileUtils().getMovedSFilePath(strLsPath);
		try {
			File oldDir = new File(strLsPath + File.separator + "old");

			if (oldDir.mkdirs()) {
				log.writelnLog(oldDir.getAbsolutePath() + "を作成しました。");
			}
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
				log.writelnLog("ファイルを" + movedPath + "に移動しました。");
			} else {
				if (new File(strFilePath).delete()) {
					log.writelnLog(strFilePath + "を削除しました。");
				}
			}
		} catch (SecurityException | IOException e) {
			log.writelnLog(e.toString());
		}

		SeleniumTrade trade =  new SeleniumTrade();

		trade.login(strLsPath, tradeVisible);

		List<TradeDataBean> failedList = new ArrayList<>();

		for (TradeDataBean bean : beanList) {

			//code view から株数取得 310
			TradeDataBean beanCode = db.getTradeViewOfCode(bean.getCode());

			if (beanCode.getRealEntryVolume().equals("0")) {
				log.writelnLog(beanCode.getCode() + " :もってないよ。");

				continue;
			}

			int stockVolume = Integer.parseInt(beanCode.getRealEntryVolume());

			//コード、Entry,exitのViewからコード、exitでList取得 230
			List<TradeDataBean> listCodeMethods = db.getTradeViewOfCodeMethods(bean.getCode(), bean.getExitMethod());

			if (listCodeMethods == null) {
				continue;
			} else if (listCodeMethods.size() == 0){
				continue;
			}

			//コードの売却株数取得
			int tmpSellVolume = 0;

			for (TradeDataBean beanCodeMethods : listCodeMethods ) {
				tmpSellVolume += Integer.parseInt(beanCodeMethods.getRealEntryVolume());
			}


			//端数計算は所有株数が100を超えてるときのみ
			if (stockVolume > 100) {


				//株数と売却数それぞれのMod 100を取得
				int fracTmpSellVolume = tmpSellVolume % 100;
				int fracStockVolume = stockVolume % 100;
				int fracSellVolume = 0;

				//if 株数Mod < 売却数Mod
				if (fracStockVolume < fracSellVolume) {
					//コードで一番株数が多いメソッド組を取得
					TradeDataBean highestBean = db.getHighestTradeViewOfCodeMethods(bean.getCode()).clone();
					//売却数Mod - 株数Modを上記メソッド組に加算
					highestBean.setRealEntryVolume(String.valueOf(fracSellVolume - fracStockVolume));

					//	売却端数を株数Modに設定
					fracSellVolume = fracStockVolume;
				} else {
					//	売却端数を売却数Modに設定
					fracSellVolume = fracTmpSellVolume;
				}
				//Listの末尾のレコードのvolumeから売却端数を減らす
				TradeDataBean tailBean = listCodeMethods.get(listCodeMethods.size()-1);
				tailBean.minusRealEntryVolume(fracSellVolume);
				tailBean.setMINI_CHECK_flg("0");

				//Listの末尾に上記と同じコード、entry,exitメソッドで売却端数分volumeのレコードを追加
				TradeDataBean cloneBean = tailBean.clone();
				cloneBean.setRealEntryVolume(String.valueOf(fracSellVolume));
				cloneBean.setMINI_CHECK_flg("1");
				listCodeMethods.add(cloneBean);

				//売却レコードList完成したのでレコードごとに売却
				failedList.addAll(trade.sellStocks(listCodeMethods, strLsPath));

			} else {
				System.out.println("今日はこっち");
				List<TradeDataBean> list = new ArrayList<>();
				TradeDataBean sBean = bean.clone();

				if ( tmpSellVolume > 100) {
					int sVolume = tmpSellVolume % 100;
					sBean.setRealEntryVolume(String.valueOf(sBean));
					sBean.setMINI_CHECK_flg("1");
					list.add(sBean);
					TradeDataBean tangenBean = bean.clone();
					tangenBean.setRealEntryVolume(String.valueOf(tmpSellVolume - sVolume));
					tangenBean.setMINI_CHECK_flg("0");
					list.add(tangenBean);
				} else {
					sBean.setRealEntryVolume(String.valueOf(tmpSellVolume));
					sBean.setMINI_CHECK_flg("1");
					list.add(sBean);
				}

				failedList.addAll(trade.sellStocks(list, strLsPath));
			}


		}

		trade.logout();



		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			new FileUtils().makeRemainsDataFile(failedList, strLsPath, false);
		} else {
			log.writelnLog("おわりだよー");
		}

	}

}
