/**
 *
 */
package jp.sigre.fbs.controller;

import java.io.File;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.FileUtils;
import jp.sigre.fbs.selenium.trade.IniBean;
import jp.sigre.fbs.selenium.trade.TradeDataBean;

/**
 * @author sigre
 *
 */
public class DataController {

	private IniBean iniBean = null;

	public DataController(IniBean iniBean) {
		this.iniBean = iniBean;

	}

	public void setIniBean(IniBean iniBean) {
		this.iniBean = iniBean;
	}

	public void updateSepaCombine() {

		//TODO;分割銘柄が割り切れない場合は？

		final String WILDCARD = "wildcard";

		LogMessage log = new LogMessage();

		if (iniBean==null) {
			log.writelnLog("Iniファイルが設定されていません。");
			return;
		}

		FileUtils file = new FileUtils();
		String strSepaComFilePath = file.getSepaCombineFilePath(iniBean.getLS_FilePath());
		File sepaComFile = new File(strSepaComFilePath);

		if (!sepaComFile.exists()) {
			log.writelnLog("分割併合ファイルがありません。");
			return;
		}

		List<SepaCombineBean> sepaComList = new FileUtils().csvToSepaCombine(strSepaComFilePath);

		if (sepaComList.size()==0) {
			log.writelnLog("分割併合銘柄がありません。");
			return;
		}

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		for (SepaCombineBean bean : sepaComList) {
			TradeDataBean tradeBean = db.getTradeViewOfCode(String.valueOf(bean.getCode()));
			tradeBean.setDayTime(file.getTodayDate());
			tradeBean.setEntry_money("0");
			tradeBean.setEntryMethod(WILDCARD);
			tradeBean.setExitMethod(WILDCARD);
			tradeBean.setMINI_CHECK_flg("2");

			int realEntryVolume = Integer.parseInt(tradeBean.getRealEntryVolume());

			int flag = bean.getChecksepa_combine();

			//int sepaComVolume = bean.getChecksepa_combine()==1? realEntryVolume * ()

			//tradeBean.setRealEntryVolume(realEntryVolume);

		}
	}
}
