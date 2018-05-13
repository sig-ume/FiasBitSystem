package jp.sigre.fbs.selenium.trade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.sigre.fbs.bean.TradeSetBean;
import jp.sigre.fbs.controller.DataController;
import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.digest.Digest;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class TradeController {

	private final FileUtils fileUtils = new FileUtils();
	private final String basePath = System.getProperty("user.dir");

	private final LogMessage log = new LogMessage(basePath);

	//TODO:以下のグローバル引数を削除

	private IniBean iniBean = null;
	private String strLsPath;
	private String strIdPath;
	private String tradeVisible;

	private SeleniumTrade trade =  new SeleniumTrade();

	public IniBean getIniBean() {
		if (iniBean == null) log.writelnLog("Iniファイルが読み込まれていません。");

		return iniBean;
	}

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

		//idPath未指定時は直下（idPath指定が隠しパラメータとなったため）
		if (strIdPath.equals("")) {
			strIdPath = basePath;
			iniBean.setID_FilePath(strIdPath);
		}

		File idFolder = new File(strIdPath);

		if (!idFolder.isDirectory()) {
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

		String strBunkatsuFilePath = fileUtils.getSepaComFilePath(strLsPath);
		File bunkatsuFile = new File(strBunkatsuFilePath);

		Calendar cal = Calendar.getInstance();

		//休日テーブル更新
		new DataController().updateHolidayTable(cal.get(Calendar.YEAR) + "-"
											 + (cal.get(Calendar.MONTH)+1) + "-"
											 + cal.get(Calendar.DAY_OF_MONTH));

		//Temp取引テーブルを取引テーブルへ移動
		new DataController().moveTempTradeData(Calendar.getInstance());

		int hour = cal.get(Calendar.HOUR_OF_DAY);

		if (!bunkatsuFile.exists()) log.writelnLog("分割併合処理はありません。");
		else if(hour >= 18 || hour <= 10) {
			log.writelnLog("分割併合があります。株数を修正後に売買を開始します。");

			//分割併合処理
			if(new DataController().updateSepaCombine(strLsPath)) log.writelnLog("分割併合処理が成功しました。");
			else log.writelnLog("分割併合処理が失敗しました。");
		} else {
			log.writelnLog("分割併合銘柄があります。売買可能となる18時まで処理を制限します。");
			return false;
		}

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
		boolean isLogined = trade.login(strIdPath, tradeVisible);

		if (!isLogined) {
			log.writelnLog("ログインに失敗しました。");
			return false;
		}

		//データ齟齬の確認
		consistStock();

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

	/**
	 * TODO : tradeメソッドのログイン部分をこれで共通化
	 * @return
	 */
	public boolean login() {

		strLsPath = iniBean.getLS_FilePath();
		strIdPath = iniBean.getID_FilePath();
		tradeVisible = iniBean.getTradeVisible();
		//Login処理
		boolean isLogined = trade.login(strIdPath, tradeVisible);

		if (!isLogined) {
			log.writelnLog("ログインに失敗しました。");
			return false;
		}
		return true;
	}

	public boolean logout() {

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


	public void consistStock() {

		TradeConsistency cons = new TradeConsistency();
		DataController data = new DataController();
		String strLsPath = iniBean.getLS_FilePath();

		List<List<TradeDataBean>> stockLists = cons.checkDbAndSbiStock(trade);
		if(stockLists.size() > 0) data.updateDbAndSbiStock(stockLists);

		List<TradeDataBean> keepList = cons.checkDbAndFiaKeep(strLsPath);
		if(keepList.size() > 0)	{
			keepList = data.updateDbAndFia(keepList);
			fileUtils.makeRemainsDataFile(keepList, strLsPath, false);
			log.writelnLog("fias上で保有していない銘柄を保有しているため、自動的に売却を行います。");
			for (TradeDataBean bean : keepList) log.writelnLog("銘柄:" + bean.getCode() + ", entryMethod:"
					+ bean.getEntryMethod() + ", exitMethod:" + bean.getExitMethod());
		}

		List<TradeDataBean> eliteList = cons.checkDbAndFiaElite(strLsPath);
		if(eliteList.size() > 0) {
			eliteList = data.updateDbAndFia(eliteList);
			fileUtils.makeRemainsDataFile(eliteList, strLsPath, false);
			log.writelnLog("fias上で未登録な銘柄を保有しているため、自動的に売却を行います。");
			for (TradeDataBean bean : keepList) log.writelnLog("銘柄:" + bean.getCode() + ", entryMethod:"
					+ bean.getEntryMethod() + ", exitMethod:" + bean.getExitMethod());
		}
	}

	/**
	 * 未使用
	 */
	private void tradeLong(int i) {

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

		TradeMethodFilter filter = new TradeMethodFilter();
		filter.longFilter(beanList, iniBean);
		filter.skipCode(beanList, iniBean);

		log.writelnLog("LSファイルの読み込みが完了しました。");

		List<TradeDataBean> failedList = new ArrayList<>();

		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");

		} else {

			log.writelnLog("購入処理を開始します。");


			failedList = trade.buyStocks(beanList, strIdPath);

		}

		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			fileUtils.removeRemainDataFile(strLsPath, true);

			//バックアプファイル作成
			fileUtils.makeRemainsDataFile(failedList, strLsPath, true);

			log.writelnLog("売買失敗件数：" + failedList.size());
		} else {

			fileUtils.removeRemainDataFile(strLsPath, true);
			log.writelnLog("おわりだよー");
		}


	}

	/**
	 * まとめ買い用買いメソッド
	 */
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

		log.writelnLog("LSファイルの読み込みが完了しました。");


		TradeMethodFilter filter = new TradeMethodFilter();
		filter.longFilter(beanList, iniBean);
		filter.skipCode(beanList, iniBean);

		List<TradeDataBean> failedList = new ArrayList<>();

		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");

		} else {

			log.writelnLog("購入処理を開始します。");
			failedList = buyTyuumon(beanList);

		}

		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");
			fileUtils.removeRemainDataFile(strLsPath, true);

			//バックアプファイル作成
			fileUtils.makeRemainsDataFile(failedList, strLsPath, true);

			log.writelnLog("売買失敗件数：" + failedList.size());
		} else {

			fileUtils.removeRemainDataFile(strLsPath, true);
			log.writelnLog("おわりだよー");
		}

	}

	private List<TradeDataBean> buyTyuumon(List<TradeDataBean> beanList) {
		DataController data = new DataController();
		List<TradeDataBean> failedList = new ArrayList<>();

		List<List<TradeDataBean>> sameCodeLists = data.getSameCodeLists(beanList);
		List<TradeSetBean> setList = new ArrayList<>();

		for (List<TradeDataBean> sameCodeList : sameCodeLists) {
			setList.addAll(getTradeSets(sameCodeList));
		}

		for (TradeSetBean setBean : setList) {
			String strResult = trade.buy(setBean.getCode(), setBean.getVolume(), setBean.getIsMini(), strIdPath);
			List<TradeDataBean> listInSetBean = setBean.getBeanList();
			if (strResult.contains("ご注文を受け付けました。")
					||strResult.contains("取引となります。") || strResult.contains("ご注文を受付いたします。")) {
				//
				//				if (!isBuying) {
				//					bean.setCorrectedEntryVolume(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
				//					bean.setRealEntryVolume		(String.valueOf(Integer.parseInt(bean.getRealEntryVolume())	*-1));
				//				}
				insertTradedBeanToDB(listInSetBean);

			} else {
				failedList.addAll(listInSetBean);
			}

			for(TradeDataBean bean :  listInSetBean)log.writelnLog(bean.getCode() + ":" + bean.getRealEntryVolume() + " " + strResult);

		}

		return failedList;
	}

	private void insertTradedBeanToDB(List<TradeDataBean> beanList) {

		ConnectDB db = new ConnectDB();
		db.connectStatement();
		for (TradeDataBean bean :beanList)	db.insertTempTradeData(bean);
		db.closeStatement();
	}

	protected List<TradeSetBean> getTradeSets(List<TradeDataBean> sameCodeList) {

		int entrySum = getSumEntry(sameCodeList);
		int mod100 = entrySum % 100;
		int tangenSum = entrySum - mod100;
		int sCount = 0;
		String code = sameCodeList.get(0).getCode();

		List<TradeDataBean> hasuuList = new ArrayList<>();

		for (int i = 0; i < sameCodeList.size() && 0 < sameCodeList.size(); i++) {
			if (0 == mod100 - sCount) break;

			TradeDataBean bean = sameCodeList.get(i);
			double beanCount = Double.valueOf(bean.getRealEntryVolume());

			if (mod100 - sCount >= beanCount) {
				hasuuList.add(bean);
				sCount += Double.valueOf(bean.getRealEntryVolume());

				sameCodeList.remove(i);
				i--;
			} else {
				TradeDataBean cloneBean = bean.clone();
				cloneBean.setRealEntryVolume(String.valueOf(mod100 - sCount));
				bean.minusRealEntryVolume(mod100 - sCount);

				hasuuList.add(cloneBean);

				sCount = mod100;
			}
		}


		List<TradeSetBean> result = new ArrayList<>();

		if (0<tangenSum) {
			TradeSetBean tangenSet = new TradeSetBean(code, tangenSum, sameCodeList, "0");
			result.add(tangenSet);
		}
		if (0<mod100) {
			TradeSetBean hasuuSet = new TradeSetBean(code, mod100, hasuuList, "1");
			result.add(hasuuSet);
		}

		return result;
	}

	private int getSumEntry(List<TradeDataBean> beanList) {
		int result = 0;

		for (TradeDataBean bean : beanList) result += Integer.valueOf(bean.getRealEntryVolume());

		return result;
	}

	private void newTradeShort() {

		String strFilePath = fileUtils.getSFilePath(strLsPath);

		File lFile = new File(strFilePath);
		File lRemFile = new File(strLsPath + File.separator + "sell_remains.csv");

		List<TradeDataBean> beanList = new ArrayList<>();
		if (lFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lFile));

		if (lRemFile.exists()) beanList.addAll(fileUtils.csvToTorihikiData(lRemFile));

		TradeMethodFilter filter = new TradeMethodFilter();
		filter.shortFilter(beanList, iniBean);

		//sellUnusedMethodが1の場合、使用していないメソッドの所有銘柄をすべて売却リストに追加
		if (iniBean.getSellUnusedMethod().equals("1")) {
			beanList.addAll(trade.getUnusedMethodStockList(iniBean));
		}

		log.writelnLog("LSファイルの読み込みが完了しました。");

		//除外リストに記載された銘柄を削除
		filter.skipCode(beanList, iniBean);

		List<TradeDataBean> failedList = new ArrayList<>();
		//売買株の有無チェック
		if (beanList.size() == 0) {
			log.writelnLog("売買対象の株がありません。");

		} else {

			log.writelnLog("売却処理を開始します。");


			List<TradeDataBean> tradeList = trade.getSellData(beanList);

			failedList = trade.newSellStocks(tradeList, strIdPath);

		}

		fileUtils.removeRemainDataFile(strLsPath, false);

		if (failedList.size()!=0) {
			log.writelnLog("のこってるよー");

			fileUtils.makeRemainsDataFile(failedList, strLsPath, false);

			log.writelnLog("売買失敗件数：" + failedList.size());
		} else {

			log.writelnLog("おわりだよー");
		}


	}

	public void makeBackupFile() {
		log.writelnLog("バックアップ開始");
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		//TODO:不足データを追加する。
		List<TradeDataBean> tradeList = db.getTradeViewOfCodeMethods();
		String strDate = fileUtils.getTodayDate();

		for (TradeDataBean bean : tradeList) {
			bean.setDayTime(strDate);
			bean.setType("DD");
			bean.setMINI_CHECK_flg("1");
			bean.setEntry_money("0");
		}


		fileUtils.makeBackupDataFile(tradeList, basePath);
		log.writelnLog("バックアップ完了");
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

	public void deleteOtherFiles() {
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

		log.writelnLog("Keepファイルを削除します");
		fileUtils.deleteKeepFiles(iniBean.getLS_FilePath());

		log.writelnLog("StockListファイルを削除します");
		fileUtils.deleteEliteFiles(iniBean.getLS_FilePath());

		if (canBuy || canSell) {
			log.writelnLog("LSファイル、remainsファイルが残っています。");
			return ;
		}
		log.writelnLog("キックファイルを削除します");
		fileUtils.deleteKickFiles(iniBean.getLS_FilePath());

		log.writelnLog("キックファイルを削除しました。");
	}

	public void makeDigestFile() {
		String kickPath = new FileUtils().getKeyPath(iniBean.getLS_FilePath());
		if (new File(kickPath).exists()) return;
		Digest digest = new Digest();
		digest.makeDigestFile(kickPath, 0);

		return;
	}

	public void backupDbFile() {

		new FileUtils().backupDbFile(basePath);

	}

}
