/**
 *
 */
package jp.sigre.selenium.trade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import jp.sigre.LogMessage;
import jp.sigre.database.ConnectDB;

/**
 * @author sigre
 *
 */
public class TradeController {

	FileUtils csv = new FileUtils();
	String basePath = System.getProperty("user.dir");

	LogMessage log = new LogMessage(basePath);

	FileUtils file = new FileUtils();

	IniBean iniBean = null;

	public void tradeSetup() {
		File iniFile = new File(file.getIniPath(basePath));

		log.writelnLog("iniファイル読み込み開始");

		//fbs.ini存在チェック
		if (!iniFile.exists()) {
			return;
		}

		//iniBeanの形式エラーチェック
		iniBean = file.iniToBean(iniFile);

		//TODO:iniファイルチェックをメソッド化

		String strLsPath = iniBean.getlS_FilePath();
		String strIdPath = iniBean.getiD_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();

		if (!new File(strLsPath).isDirectory() ) {
			log.writelnLog("fbs.iniに指定されたLSファイル格納フォルダが存在しません。");
			System.exit(1);
		}

		if (!new File(strIdPath).isDirectory()) {
			log.writelnLog("fbs.iniに指定されたIDファイル格納フォルダが存在しません。");
			System.exit(1);
		}

		//売買メソッド無選択チェック
		if (intMethodCount == 0) {
			log.writelnLog("売買メソッドが1つも選択されていません。");
			System.exit(1);
		}

		String tradeVisible = iniBean.getTradeVisible();
		if (tradeVisible.equals("")) {
			log.writelnLog("売買処理可視が選択されていません。");
			System.exit(1);
		}


		log.writelnLog("Iniファイルの内容を確認しました。");
	}

	public void newTradeLong() {

	}
	
	public void tradeLogin() {
		
	}

	public void tradeLong() {

		String strLsPath = iniBean.getlS_FilePath();
		String strIdPath = iniBean.getiD_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();
		String tradeVisible = iniBean.getTradeVisible();

		String strFilePath = new FileUtils().getBuyDataFilePath(strLsPath);

		File lFile = new File(strFilePath);

		File lRemFile = new File(strLsPath + File.separator + "buy_remains.csv");

		if (!lFile.exists() && !lRemFile.exists()) {
			log.writelnLog("LSファイル等の売買対象データファイルが存在しません。");
			return;
		}



		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(file.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(file.csvToTorihikiData(lRemFile));

		new TradeMethodFilter().longFilter(beanList, iniBean);

		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");
			return;
		}


		log.writelnLog("LSファイルの読み込みが完了しました。");

		log.writelnLog("LSファイルの移動、削除を開始します。");

		//ファイル削除は売買の前
		String movedPath = new FileUtils().getMovedBuyDataPath(strLsPath);
		try {
			new File(strLsPath + File.separator + "old").mkdirs();
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
			} else {
				new File(strFilePath).delete();
			}
			//remains削除
			lRemFile.delete();
		} catch (SecurityException e) {
			log.writelnLog(e.toString());
		} catch (IOException ioe) {
			log.writelnLog(ioe.toString());
		}

		log.writelnLog("LSファイルの移動、削除を行いました。");

		log.writelnLog("購入処理を開始します。");

		SeleniumTrade trade =  new SeleniumTrade();

		trade.login(strLsPath, tradeVisible);

		List<TradeDataBean> failedList = trade.buyStocks(beanList, strLsPath);

		trade.logout();


		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			new FileUtils().removeTradeDataFile(strLsPath, true);

			//バックアプファイル作成
			new FileUtils().makeTradeDataFile(failedList, strLsPath, true);

			log.writelnLog("売買失敗件数：" + failedList.size());
		} else {
			log.writelnLog("おわりだよー");
		}

	}

	public void newTradeShort() {
		String strLsPath = iniBean.getlS_FilePath();
		String strIdPath = iniBean.getiD_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();
		String tradeVisible = iniBean.getTradeVisible();


		//テスト用にLファイルを対象にする→しない
		String strFilePath = new FileUtils().getSellDataFilePath(strLsPath);

		File lFile = new File(strFilePath);
		File lRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(file.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(file.csvToTorihikiData(lRemFile));


		new TradeMethodFilter().shortFilter(beanList, iniBean);



		SeleniumTrade trade =  new SeleniumTrade();

		trade.login(strLsPath, tradeVisible);

		List<TradeDataBean> failedList = trade.newSellStocks(trade.getSellData(beanList), strLsPath);


		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			new FileUtils().removeTradeDataFile(strLsPath, false);

			new FileUtils().makeTradeDataFile(failedList, strLsPath, false);

		} else {
			log.writelnLog("おわりだよー");
		}

		//TODO：処理終了後のファイル処理をFileUtilsでメソッド化

		String movedPath = new FileUtils().getMovedSellDataPath(strLsPath);
		try {
			new File(strLsPath + File.separator + "old").mkdirs();
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
			} else {
				new File(strFilePath).delete();
			}
			//remains削除
			lRemFile.delete();
		} catch (SecurityException e) {
			log.writelnLog(e.toString());
		} catch (IOException ioe) {
			log.writelnLog(ioe.toString());
		}

		trade.logout();
	}

	public void tradeShort() {

		String strLsPath = iniBean.getlS_FilePath();
		String strIdPath = iniBean.getiD_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();
		String tradeVisible = iniBean.getTradeVisible();


		//テスト用にLファイルを対象にする→しない
		String strFilePath = new FileUtils().getSellDataFilePath(strLsPath);

		File lFile = new File(strFilePath);
		File lRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(file.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(file.csvToTorihikiData(lRemFile));


		new TradeMethodFilter().shortFilter(beanList, iniBean);

		//		System.out.println(list.size());
		//
		//		LogMessage log = new LogMessage();
		//
		//		log.writeInLog("test", iniBean.getlS_FilePath());

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//TODO：処理終了後のファイル処理をFileUtilsでメソッド化

		String movedPath = new FileUtils().getMovedSellDataPath(strLsPath);
		try {
			new File(strLsPath + File.separator + "old").mkdirs();
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
			} else {
				new File(strFilePath).delete();
			}
		} catch (SecurityException e) {
			log.writelnLog(e.toString());
		} catch (IOException ioe) {
			log.writelnLog(ioe.toString());
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
			System.out.println("コードの売却株数取得;" + tmpSellVolume);

			//端数計算は所有株数が100を超えてるときのみ
			if (stockVolume > 100) {


				//株数と売却数それぞれのMod 100を取得
				int fracTmpSellVolume = tmpSellVolume % 100;
				int fracStockVolume = stockVolume % 100;
				int fracSellVolume = 0;

				System.out.println("株数と売却数:" + fracStockVolume + " " + fracTmpSellVolume);

				//if 株数Mod < 売却数Mod
				if (fracStockVolume < fracTmpSellVolume) {
					System.out.println("株数Mod < 売却数Mod");
					//コードで一番株数が多いメソッド組を取得
					TradeDataBean highestBean = db.getHighestTradeViewOfCodeMethods(bean.getCode()).clone();
					//売却数Mod - 株数Modを上記メソッド組に加算
					highestBean.setRealEntryVolume(String.valueOf(fracSellVolume - fracTmpSellVolume));

					//	売却端数を株数Modに設定
					fracSellVolume = fracStockVolume;
				} else {
					System.out.println("株数Mod > 売却数Mod");
					//	売却端数を売却数Modに設定
					fracSellVolume = fracTmpSellVolume;
				}
				System.out.println("売却端数:" + fracSellVolume);
				//Listの末尾のレコードのvolumeから売却端数を減らす
				TradeDataBean tailBean = listCodeMethods.get(listCodeMethods.size()-1);
				tailBean.minusRealEntryVolume(fracTmpSellVolume);
				tailBean.setMINI_CHECK_flg("0");

				//Listの末尾に上記と同じコード、entry,exitメソッドで売却端数分volumeのレコードを追加
				TradeDataBean cloneBean = tailBean.clone();
				cloneBean.setRealEntryVolume(String.valueOf(fracSellVolume));
				cloneBean.setMINI_CHECK_flg("1");
				listCodeMethods.add(cloneBean);

				//test
				for (TradeDataBean bean1 : listCodeMethods) {
					System.out.println(bean1);
				}

				//売却レコードList完成したのでレコードごとに売却
				failedList.addAll(trade.sellStocks(listCodeMethods, strLsPath));
				//TODO:highestBeanをDBに登録
				//TODO:同じ株数を売ったやつから削除
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
			new FileUtils().removeTradeDataFile(strLsPath, false);

			new FileUtils().makeTradeDataFile(failedList, strLsPath, false);

		} else {
			log.writelnLog("おわりだよー");
		}
	}

	public void makeBackupFile() {
		log.writelnLog("バックアップ開始");
		String strFolderPath = iniBean.getlS_FilePath();
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//TODO:不足データを追加する。
		List<TradeDataBean> tradeList = db.getTradeViewOfCodeMethods();
		new FileUtils().makeBackupDataFile(tradeList, strFolderPath);
		log.writelnLog("バックアップ完了");
	}
}