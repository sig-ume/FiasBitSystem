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
import jp.sigre.digest.Digest;

/**
 * @author sigre
 *
 */
public class TradeController {

	private final FileUtils fileUtils = new FileUtils();
	private final String basePath = System.getProperty("user.dir");

	private final LogMessage log = new LogMessage(basePath);

	private IniBean iniBean = null;
	private String strLsPath;
	private String strIdPath;
	private String tradeVisible;

	private SeleniumTrade trade =  new SeleniumTrade();

	public boolean tradeSetup() {
		File iniFile = new File(fileUtils.getIniPath(basePath));

		log.writelnLog("iniファイル読み込み開始");

		//fbs.ini存在チェック
		if (!iniFile.exists()) {
			log.writelnLog("iniファイルが存在しません。");
			return false;
		}

		//iniBeanの形式エラーチェック
		iniBean = fileUtils.iniToBean(iniFile);

		//TODO:iniファイルチェックをメソッド化

		String strLsPath = iniBean.getLS_FilePath();
		String strIdPath = iniBean.getID_FilePath();
		int intMethodCount = iniBean.getMethodSet().size();

		if (!new File(strLsPath).isDirectory() ) {
			log.writelnLog("fbs.iniに指定されたLSファイル格納フォルダが存在しません。");
			return false;
		}

		if (!new File(strIdPath).isDirectory()) {
			log.writelnLog("fbs.iniに指定されたIDファイル格納フォルダが存在しません。");
			return false;
		}

		//売買メソッド無選択チェック
		if (intMethodCount == 0) {
			log.writelnLog("売買メソッドが1つも選択されていません。");
			return false;
		}

		String tradeVisible = iniBean.getTradeVisible();
		if (tradeVisible.equals("")) {
			log.writelnLog("売買処理を可視状態にするかどうかが選択されていません。");
			return false;
		}

		String sellUnusedMethod = iniBean.getSellUnusedMethod();
		if (sellUnusedMethod.equals("")) {
			log.writelnLog("使用していないメソッドで所有している株をどのように売却するかが選択されていません。");
			return false;
		}

		log.writelnLog("Iniファイルの内容を確認しました。");
		return true;
	}

	public boolean trade() {

		strLsPath = iniBean.getLS_FilePath();
		strIdPath = iniBean.getID_FilePath();
		tradeVisible = iniBean.getTradeVisible();

		String strFilePath = fileUtils.getLFilePath(strLsPath);

		File lFile = new File(strFilePath);

		File lRemFile = new File(strLsPath + File.separator + "buy_remains.csv");

		boolean canBuy = lFile.exists() || lRemFile.exists();

		String strSFilePath = fileUtils.getSFilePath(strLsPath);

		File sFile = new File(strSFilePath);
		File sRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		boolean canSell = sFile.exists() || sRemFile.exists();

		if (!canBuy && !canSell) {
			log.writelnLog("LSファイルが存在しません。売買処理は行われません。");
			return false;
		}

		Digest dig = new Digest();
		int count = 0;
		String keyPath = fileUtils.getKeyPath(strLsPath);
		if (!dig.checkDigestFile(keyPath)) {

			log.writelnLog("KICKファイルが存在しないか不正です。");
			return false;
		}

		dig.makeDigestFile(keyPath, ++count);
		log.writelnLog("KICKファイルを確認しました。");

		//Login処理
		trade.login(strIdPath, tradeVisible);

		//データ齟齬の確認
		if (sFile.exists() && lFile.exists()) consistStock();

		if (canBuy) {
			tradeLong();
			atoshimatsuLong();
		} else {
			log.writelnLog("Lファイル等の購買対象データファイルが存在しません。");
		}

		if (canSell) {
			newTradeShort();
			atoshimatsuShort();
		} else {
			log.writelnLog("Sファイル等の売却対象データファイルが存在しません。");
		}

		trade.logout();

		return true;
	}

	private void atoshimatsuShort() {

		String strFilePath = fileUtils.getSFilePath(strLsPath);

		atoshimatsu(strFilePath, false);
	}

	private void atoshimatsuLong() {

		String strFilePath = fileUtils.getLFilePath(strLsPath);
		atoshimatsu(strFilePath, true);
	}

	private void atoshimatsu(String strFilePath, boolean isBuying) {

		log.writelnLog("LSファイルの移動、削除を開始します。");

		try {
			atoshimatsuDataFile(strLsPath, strFilePath, isBuying);
			//remains削除
			//fileUtils.deleteFile(lRemFile);
		} catch (SecurityException | IOException e) {
			log.writelnLog(e.toString());
		}

		log.writelnLog("LSファイルの移動、削除を行いました。");

	}


	private void consistStock() {

		TradeConsistency cons = new TradeConsistency();
		cons.checkDbAndSbiStock(trade);
		cons.checkDbAndFiaKeep(iniBean.getLS_FilePath());
	}

	private void tradeLong() {

		String strFilePath = fileUtils.getLFilePath(strLsPath);

		File lFile = new File(strFilePath);

		File lRemFile = new File(strLsPath + File.separator + "buy_remains.csv");

		if (!lFile.exists() && !lRemFile.exists()) {
			log.writelnLog("LSファイル等の売買対象データファイルが存在しません。");
			return;
		}

		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lRemFile));

		new TradeMethodFilter().longFilter(beanList, iniBean);

		log.writelnLog("LSファイルの読み込みが完了しました。");

		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");
			return;
		}

		log.writelnLog("購入処理を開始します。");


		List<TradeDataBean> failedList = trade.buyStocks(beanList, strIdPath);

		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			fileUtils.removeTradeDataFile(strLsPath, true);

			//バックアプファイル作成
			fileUtils.makeTradeDataFile(failedList, strLsPath, true);

			log.writelnLog("売買失敗件数：" + failedList.size());
		} else {

			fileUtils.removeTradeDataFile(strLsPath, true);
			log.writelnLog("おわりだよー");
		}


	}

	private void newTradeShort() {

		String strFilePath = fileUtils.getSFilePath(strLsPath);

		File lFile = new File(strFilePath);
		File lRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lRemFile));


		new TradeMethodFilter().shortFilter(beanList, iniBean);


		//sellUnusedMethodが1の場合、使用していないメソッドの所有銘柄をすべて売却リストに追加
		if (iniBean.getSellUnusedMethod().equals("1")) {
			beanList.addAll(trade.getUnusedMethodStockList(iniBean));
		}

		log.writelnLog("LSファイルの読み込みが完了しました。");

		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");
			return;
		}

		log.writelnLog("売却処理を開始します。");


		List<TradeDataBean> tradeList = trade.getSellData(beanList);

		List<TradeDataBean> failedList = trade.newSellStocks(tradeList, strIdPath);

		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			fileUtils.removeTradeDataFile(strLsPath, false);

			fileUtils.makeTradeDataFile(failedList, strLsPath, false);

		} else {
			log.writelnLog("おわりだよー");
		}


	}


	@SuppressWarnings("unused")
	public void tradeShort() {

		String strLsPath = iniBean.getLS_FilePath();
		String strIdPath = iniBean.getID_FilePath();
		String tradeVisible = iniBean.getTradeVisible();


		String strFilePath = fileUtils.getSFilePath(strLsPath);

		File lFile = new File(strFilePath);
		File lRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lRemFile));


		new TradeMethodFilter().shortFilter(beanList, iniBean);

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//TODO：処理終了後のファイル処理をFileUtilsでメソッド化

		String movedPath = fileUtils.getMovedSFilePath(strLsPath);
		try {
			if (!new File(strLsPath + File.separator + "old").mkdirs()) {
				log.writelnLog(strLsPath + "の削除に失敗しました。");
			}
			if (!new File(movedPath).exists()) {
				Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
				log.writelnLog(movedPath + "を移動しました。");
			} else {
				if (!new File(strFilePath).delete()) {
					log.writelnLog(strFilePath + "の削除に失敗しました。");
				}
			}
		} catch (SecurityException | IOException e) {
			log.writelnLog(e.toString());
		}


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
				if (fracStockVolume < fracTmpSellVolume) {
					//コードで一番株数が多いメソッド組を取得
					TradeDataBean highestBean = db.getHighestTradeViewOfCodeMethods(bean.getCode()).clone();
					//売却数Mod - 株数Modを上記メソッド組に加算
					highestBean.setRealEntryVolume(String.valueOf(fracSellVolume - fracTmpSellVolume));

					//	売却端数を株数Modに設定
					fracSellVolume = fracStockVolume;
				} else {
					//	売却端数を売却数Modに設定
					fracSellVolume = fracTmpSellVolume;
				}
				//Listの末尾のレコードのvolumeから売却端数を減らす
				TradeDataBean tailBean = listCodeMethods.get(listCodeMethods.size()-1);
				tailBean.minusRealEntryVolume(fracTmpSellVolume);
				tailBean.setMINI_CHECK_flg("0");

				//Listの末尾に上記と同じコード、entry,exitメソッドで売却端数分volumeのレコードを追加
				TradeDataBean cloneBean = tailBean.clone();
				cloneBean.setRealEntryVolume(String.valueOf(fracSellVolume));
				cloneBean.setMINI_CHECK_flg("1");
				listCodeMethods.add(cloneBean);

				//売却レコードList完成したのでレコードごとに売却
				failedList.addAll(trade.sellStocks(listCodeMethods, strIdPath));
				//TODO:highestBeanをDBに登録
				//TODO:同じ株数を売ったやつから削除
			} else {
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

				failedList.addAll(trade.sellStocks(list, strIdPath));
			}


		}


		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			fileUtils.removeTradeDataFile(strLsPath, false);

			fileUtils.makeTradeDataFile(failedList, strLsPath, false);

		} else {
			fileUtils.removeTradeDataFile(strLsPath, true);
			log.writelnLog("おわりだよー");
		}
	}

	public void makeBackupFile() {
		log.writelnLog("バックアップ開始");
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//TODO:不足データを追加する。
		List<TradeDataBean> tradeList = db.getTradeViewOfCodeMethods();
		fileUtils.makeBackupDataFile(tradeList, basePath);
		log.writelnLog("バックアップ完了");
	}

	public void deleteKickFiles() {
		strLsPath = iniBean.getLS_FilePath();
		strIdPath = iniBean.getID_FilePath();
		tradeVisible = iniBean.getTradeVisible();

		String strFilePath = fileUtils.getLFilePath(strLsPath);

		File lFile = new File(strFilePath);

		File lRemFile = new File(strLsPath + File.separator + "buy_remains.csv");

		boolean canBuy = lFile.exists() || lRemFile.exists();

		String strSFilePath = fileUtils.getSFilePath(strLsPath);

		File sFile = new File(strSFilePath);
		File sRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		boolean canSell = sFile.exists() || sRemFile.exists();

		if (canBuy || canSell) {
			log.writelnLog("LSファイル、remainsファイルが残っています。");
			return ;
		}
		log.writelnLog("キックファイルを削除します");
		fileUtils.deleteKickFiles(iniBean.getLS_FilePath());

		log.writelnLog("キックファイルを削除しました。");
	}

	/**
	 * LSファイルの移動を行う
	 * @param strLsPath
	 * @param strFilePath 移動ファイル
	 * @return
	 * @throws IOException
	 */
	public void atoshimatsuDataFile(String strLsPath, String strFilePath, boolean isBuying) throws IOException {
		String movedPath = isBuying ? fileUtils.getMovedLFilePath(strLsPath) : fileUtils.getMovedSFilePath(strLsPath);

		File oldFolder = new File(strLsPath + File.separator + "old");
		if (!oldFolder.exists()) {
			if (!oldFolder.mkdirs()) {
				new LogMessage().writelnLog(strLsPath + "\\oldフォルダの作成に失敗しました。");
			} else {
				new LogMessage().writelnLog(strLsPath + "\\oldフォルダを作成しました。");
			}
		}

		if (!new File(movedPath).exists()) {
			Files.move(Paths.get(strFilePath), Paths.get(movedPath), StandardCopyOption.ATOMIC_MOVE);
			new LogMessage().writelnLog(movedPath + "へのファイルを移動しました。");
		} else {
			fileUtils.deleteFile(strFilePath);
		}

		return;
	}
}
