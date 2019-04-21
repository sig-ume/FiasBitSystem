package jp.sigre.fbs.selenium.trade;

import java.util.ArrayList;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;

/**
 * @author sigre
 *
 */
public class TradeMethodFilter {


	LogMessage log = new LogMessage();
	List<String[]> methodSets = new ArrayList<>();
	IniBean iniBean = null;

	public TradeMethodFilter(IniBean iniBean)  {
		this.iniBean = iniBean;
		methodSets = iniBean.getMethodSet();
	}

	public void longFilter(List<TradeDataBean> list) {
		List<Boolean> checkbox = new ArrayList<>();
		methodSets = iniBean.getMethodSet();
		for (int i = 0; i<list.size(); i++) {
			checkbox.add(false);
		}

		for (int i = 0; i<list.size(); i++) {
			TradeDataBean tradeData = list.get(i);
			String entryMethod = tradeData.getEntryMethod();
			String exitMethod = tradeData.getExitMethod();

			for (String[] methodSet : methodSets) {

				if (entryMethod.equals(methodSet[0]) && exitMethod.equals(methodSet[1]) && !methodSet[2].equals("0")) {
					checkbox.set(i, true);
				}
			}

			if(exitMethod.equals("wildcard")) {
				checkbox.set(i, true);
			}
		}

		//チェックボックスでFalse＝選択されてないものを削除
		for (int i = 0; i<list.size(); i++) {
			if (!checkbox.get(i)) {
				list.remove(i);
				checkbox.remove(i);
				i--;
			}
		}

		//株数が0なら削除
		for (int i = 0; i<list.size(); i++) {
			if (list.get(i).getRealEntryVolume().equals("0")) {
				list.remove(i);
				i--;
			}
		}

	}

	public void shortFilter(List<TradeDataBean> list) {

		//sellUnusedMethodが0だった場合、使用するメソッドのみ売却処理→methodでのフィルターをかける
		if (iniBean.getSellUnusedMethod().equals("0")) {
			this.longFilter(list);
		} else {

			//株数が0なら削除
			for (int i = 0; i<list.size(); i++) {
				if (list.get(i).getRealEntryVolume().equals("0")) {
					list.remove(i);
					i--;
				}
			}
		}


		for (int i = 0; i < list.size(); i++) {
			for (int j = i+1; j < list.size(); j++) {
				if (list.get(i).equalsCodeMethods(list.get(j))) {
					list.remove(j);
					j--;
				}
			}
		}

		ConnectDB db = new ConnectDB();

		for (int i = 0; i < list.size(); i++ ) {
			TradeDataBean tradeData = list.get(i);
			//処理予定の株を持ってるかチェックをフィルターに
			TradeDataBean dbBean = db.getTradeViewOfCodeMethods(tradeData.getCode(), tradeData.getEntryMethod(), tradeData.getExitMethod());
			if (dbBean.getRealEntryVolume().equals("0")) {
				list.remove(i);
				i--;
			}
		}

	}

	public void skipCode(List<TradeDataBean> list) {
		List<Integer> skipList = iniBean.getSkipList();

		for (int j = 0; j<skipList.size(); j++) {
			for (int i = 0; i<list.size(); i++) {

				int skipNumber = skipList.get(j);
				TradeDataBean bean = list.get(i);
				int code = Integer.parseInt(bean.getCode());
				if (code == skipNumber) {
					list.remove(i);
					i--;
					log.writelnLog(skipNumber + "は設定に従い売買を行いません。");
					continue;
				}
			}
		}
	}

	public List<TradeDataBean> setLongRatioedValue(List<TradeDataBean> beanList) {

		methodSets = iniBean.getMethodSet();
		//entry&exit=0 or 1のメソッドを削除
		for (int i = 0; i < methodSets.size(); i++) {
			String[] methodSet = methodSets.get(i);
			if (methodSet[2].equals("0") || methodSet[2].equals("1")) {
				methodSets.remove(i);
				i--;
			}
		}

		for (String[] methodSet : methodSets) {
			double ratio = Double.valueOf(methodSet[2]);
			if (ratio < 1) {
				log.writelnLog(methodSet[0] + ":" + methodSet[1] + "はレシオ1未満なので1扱いとします。");
				continue;
			}
			for (TradeDataBean tradeData : beanList) {
				String strEntryMethod = tradeData.getEntryMethod();
				String strExitMethod = tradeData.getExitMethod();
				if (!(strEntryMethod.equals(methodSet[0]) && strExitMethod.equals(methodSet[1]))) continue;
				Double realEntryVolume = Double.valueOf(tradeData.getRealEntryVolume());
				//String ratioedVolume = String.valueOf((int)Math.round(realEntryVolume * ratio));
				String ratioedVolume = String.valueOf(realEntryVolume * ratio);
				tradeData.setRealEntryVolume(ratioedVolume);
			}
		}

		return beanList;
	}

	public List<TradeDataBean> setVolumeLongToInt(List<TradeDataBean> beanList) {
		for (TradeDataBean tradeData : beanList) {
			double volume = Double.parseDouble(tradeData.getRealEntryVolume());
			String ratioedVolume = String.valueOf((int)Math.ceil(volume));

			tradeData.setRealEntryVolume(ratioedVolume);
		}

		return beanList;
	}
}
