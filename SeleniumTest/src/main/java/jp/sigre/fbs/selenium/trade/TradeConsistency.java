/**
 *
 */
package jp.sigre.fbs.selenium.trade;

import java.io.File;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class TradeConsistency {

	/**
	 * SBIの所有株とDBの整合性確認
	 * SBIログイン中のみ有効
	 */
	public void checkDbAndSbiStock(SeleniumTrade trade) {
		LogMessage log = new LogMessage();
		log.writelnLog("SBIとDB間で所有している株の齟齬がないか確認します。");

		ConnectDB db = new ConnectDB();
		db.connectStatement();
		List<TradeDataBean> dbList = db.getTradeViewOfCode();

		List<TradeDataBean> sbiList = trade.getSBIStock();

		if (!checkCodeConsistency(dbList, sbiList)) {
			if (dbList.size()!=0) {
				log.writelnLog("!!!!DBにSBIで所有していない株のレコードがあります。!!!!");
				for (TradeDataBean bean : dbList) {
					String message = "銘柄コード; " + bean.getCode() + ", 株数: " + bean.getRealEntryVolume();
					log.writelnLog(message);
				}
			}
			if (sbiList.size()!=0) {
				log.writelnLog("SBIにDBで所有していない株のレコードがあります。");
				for (TradeDataBean bean : sbiList) {
					String message = "銘柄コード; " + bean.getCode() + ", 株数: " + bean.getRealEntryVolume();
					log.writelnLog(message);
				}
			}
		} else log.writelnLog("SBIとDBの間で情報の齟齬はありませんでした。");
	}

	public void checkDbAndFiaKeep(String strLsFolderPath) {
		LogMessage log = new LogMessage();
		log.writelnLog("fiaとDB間で所有している株の齟齬がないか確認します。");

		ConnectDB db = new ConnectDB();
		db.connectStatement();
		List<TradeDataBean> dbList = db.getTradeViewOfCodeMethods();

		FileUtils file = new FileUtils();
		String strKeepFile = file.getFiaKeepFilePath(strLsFolderPath);

		if (!new File(strKeepFile).exists()) {
			log.writelnLog("keepファイルが存在しません。");

		}
		List<TradeDataBean> fiaList = file.csvToFiaKeep(strKeepFile);

		if (!checkCodeMethodsConsistency(dbList, fiaList)) {
			if (dbList.size()!=0) {
				log.writelnLog("!!!!DBにfiaで所有していない株のレコードがあります。!!!!!");
				for (TradeDataBean bean : dbList) {
					String message = "銘柄コード; " + bean.getCode() + ", entryMethod: " + bean.getEntryMethod() +
							", exitMethod: " + bean.getExitMethod();
					log.writelnLog(message);
				}
			}

		} else log.writelnLog("fiaとDBの間で情報の齟齬はありませんでした。");
	}

	private boolean checkCodeConsistency(List<TradeDataBean> list1, List<TradeDataBean> list2) {

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {

				TradeDataBean bean1 = list1.get(i);
				TradeDataBean bean2 = list2.get(j);
				if (bean1.equals(bean2)) {
					list1.remove(i);
					i--;
					list2.remove(j);
					j--;
					break;
				}

				String code1 = bean1.getCode();
				String code2 = bean2.getCode();
				boolean sameCode = code1.equals(code2);

				if (sameCode) {
					int int1 = Integer.parseInt(bean1.getRealEntryVolume());
					int int2 = Integer.parseInt(bean2.getRealEntryVolume());
					if (int1 > int2) {
						String volume = String.valueOf(int1 - int2);
						bean1.setRealEntryVolume(volume);
						bean1.setCorrectedEntryVolume(volume);
						list2.remove(bean2);
					} else {
						String volume = String.valueOf(int1 - int2);
						bean2.setRealEntryVolume(volume);
						bean2.setCorrectedEntryVolume(volume);
						list1.remove(bean1);
					}
				}
			}
		}

		int count1 = list1.size();
		int count2 = list2.size();

		if (count1==0 && count2==0) return true;

		return false;

	}

	private boolean checkCodeMethodsConsistency(List<TradeDataBean> list1, List<TradeDataBean> list2) {

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				TradeDataBean bean1 = list1.get(i);
				//System.out.println("bean1:"+ bean1);
				TradeDataBean bean2 = list2.get(j);
				//System.out.println("bean2:" + bean2);
				boolean isSameCode = bean1.getCode()!=null && bean2.getCode()!=null
						? bean1.getCode().equals(bean2.getCode().replace("_T", "")) : false;

				boolean isSameEntry = bean1.getEntryMethod()!=null&&bean2.getEntryMethod()!=null
						? bean1.getEntryMethod().equals(bean2.getEntryMethod()) : false;

				boolean isSameExit = bean1.getExitMethod()!=null&&bean2.getExitMethod()!=null
						? bean1.getExitMethod().equals(bean2.getExitMethod()) : false;

				boolean isWildEntry = bean1.getEntryMethod()!=null ? bean1.getEntryMethod().equals("wildcard") : false;
				boolean isWildExit = bean1.getExitMethod()!=null ? bean1.getExitMethod().equals("wildcard") : false;

				if (isSameCode && isSameEntry && isSameExit) {
					list1.remove(i);
					i--;
					list2.remove(j);
					j--;
					break;
				} else if (isSameCode && isWildEntry && isWildExit) {
					list1.remove(i);
					i--;
					list2.remove(j);
					j--;
					break;
				}
			}
		}

		int count1 = list1.size();

		if (count1==0) return true;

		return false;

	}
}
