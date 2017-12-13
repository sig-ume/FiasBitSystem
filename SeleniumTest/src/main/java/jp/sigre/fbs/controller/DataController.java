/**
 *
 */
package jp.sigre.fbs.controller;

import java.io.File;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class DataController {

	public DataController() {
	}

	public boolean updateSepaCombine(String lsFilePath) {

		//TODO;分割銘柄が割り切れない場合は？

		final String WILDCARD = "wildcard";

		LogMessage log = new LogMessage();

		FileUtils file = new FileUtils();
		String strSepaComFilePath = file.getSepaCombineFilePath(lsFilePath);
		File sepaComFile = new File(strSepaComFilePath);

		if (!sepaComFile.exists()) {
			log.writelnLog("分割併合ファイルがありません。");
			return false;
		}

		List<SepaCombineBean> sepaComList = new FileUtils().csvToSepaCombine(strSepaComFilePath);

		if (sepaComList == null) {
			log.writelnLog("おそらく分割併合ファイルの形式が不正です。");
			return false;
		}

		if (sepaComList.size()==0) {
			log.writelnLog("分割併合銘柄がありません。");
			return false;
		}

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		for (SepaCombineBean bean : sepaComList) {
			TradeDataBean tradeBean = db.getTradeViewOfCode(bean.getCode());
			System.out.println(tradeBean.getRealEntryVolume());

			if (tradeBean.getRealEntryVolume().equals("0")) continue;

			tradeBean.setDayTime(file.getTodayDate());
			tradeBean.setEntry_money("0");
			tradeBean.setEntryMethod(WILDCARD);
			tradeBean.setExitMethod(WILDCARD);
			tradeBean.setMINI_CHECK_flg("2");

			System.out.println(tradeBean.getCode() + " :" + tradeBean.getRealEntryVolume());
			int realEntryVolume = Integer.parseInt(tradeBean.getRealEntryVolume());

			int flag = Integer.parseInt(bean.getChecksepa_combine());
			double ratio = Double.parseDouble(bean.getAjustRate());

			//0:combine, 1:separate
			double sepaComVolume = 0;
			if (flag==1) sepaComVolume = realEntryVolume * (ratio - 1);
			else if (flag==0) sepaComVolume = -1 * (realEntryVolume - (realEntryVolume % ratio))  * (ratio - 1) / ratio - realEntryVolume % ratio;
			else continue;

			String strSepaComVolume = "";
			if (sepaComVolume % 1 == 0)  strSepaComVolume = String.valueOf((int)sepaComVolume);
			else strSepaComVolume = String.valueOf(sepaComVolume);
			tradeBean.setRealEntryVolume(strSepaComVolume);

			db.insertTradeData(tradeBean);

		}

		return true;
	}

	public boolean updateDbAndSbiStock(List<List<TradeDataBean>> beanLists) {
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		List<TradeDataBean> dbList = beanLists.get(0);

		//DBに過剰にキープされている銘柄の株数を減らす
		for (TradeDataBean dbBean : dbList) {
			int intStock = Integer.parseInt(dbBean.getRealEntryVolume());

			//指定の銘柄のデータを取得
			List<TradeDataBean> ascList = db.getTradeViewOfCodeMethodsStockOrderAsc(dbBean.getCode());

			for (TradeDataBean ascBean : ascList) {

				int intAscStock = Integer.parseInt(ascBean.getRealEntryVolume());

				if (intStock <= intAscStock) {
					ascBean.setRealEntryVolume("-" + String.valueOf(intStock));
					ascBean.setCorrectedEntryVolume("-" + String.valueOf(intStock));

					//取得した銘柄、メソッドから株数をマイナス
					db.insertTradeData(ascBean);
					break;
				} else {
					ascBean.setRealEntryVolume("-" + String.valueOf(intAscStock));
					ascBean.setCorrectedEntryVolume("-" + String.valueOf(intAscStock));

					//取得した銘柄、メソッドから株数をマイナス
					db.insertTradeData(ascBean);

					intStock -= intAscStock;
				}
			}

		}

		List<TradeDataBean> sbiList = beanLists.get(1);

		String strToday = new FileUtils().getTodayDate();

		for (TradeDataBean sbiBean : sbiList) {
			//Beanの不足情報を追加。codeとRealEntryVoume以外。
			sbiBean.setCorrectedEntryVolume(sbiBean.getRealEntryVolume());
			sbiBean.setDayTime(strToday);
			sbiBean.setEntry_money("0");
			sbiBean.setEntryMethod("wildcard");
			sbiBean.setExitMethod("wildcard");
			sbiBean.setMINI_CHECK_flg("2");
			sbiBean.setType("DD");

			//DBに挿入
			db.insertTradeData(sbiBean);
		}

		return true;

	}

	/**
	 * FiaElite,Keepの齟齬（DBに過剰なデータがある）を修正する。
	 * DBから特定の銘柄、メソッドの残高を0にする処理。
	 *
	 * @param beanList
	 * @return
	 */
	public boolean updateDbAndFia(List<TradeDataBean> beanList) {

		for (TradeDataBean bean : beanList) {
			//株数をマイナスにして
			bean.setRealEntryVolume("-" + bean.getRealEntryVolume());
			bean.setCorrectedEntryVolume("-" + bean.getCorrectedEntryVolume());

			//DBに挿入
			ConnectDB db = new ConnectDB();
			db.connectStatement();
			db.insertTradeData(bean);
			db.closeStatement();
		}

		return true;
	}
}
