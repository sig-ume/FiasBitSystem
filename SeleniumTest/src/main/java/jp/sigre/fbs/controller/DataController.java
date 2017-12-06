/**
 *
 */
package jp.sigre.fbs.controller;

import java.io.File;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.IniBean;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class DataController {

	private IniBean iniBean = null;

	public DataController(IniBean iniBean) {
		this.iniBean = iniBean;

	}

	public boolean updateSepaCombine() {

		//TODO;分割銘柄が割り切れない場合は？

		final String WILDCARD = "wildcard";

		LogMessage log = new LogMessage();

		if (iniBean==null) {
			log.writelnLog("Iniファイルが設定されていません。");
			return false;
		}

		FileUtils file = new FileUtils();
		String strSepaComFilePath = file.getSepaCombineFilePath(iniBean.getLS_FilePath());
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
}
