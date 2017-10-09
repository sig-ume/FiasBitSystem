package jp.sigre.fbs.selenium.trade;

import java.util.ArrayList;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;

/**
 * @author sigre
 *
 */
public class TradeMethodFilter {

	public void longFilter(List<TradeDataBean> list, IniBean iniBean) {
		List<Boolean> checkbox = new ArrayList<>();
		for (int i = 0; i<list.size(); i++) {
			checkbox.add(false);
		}

		for (int i = 0; i<list.size(); i++) {
			TradeDataBean tradeData = list.get(i);
			String entryMethod = tradeData.getEntryMethod();
			String exitMethod = tradeData.getExitMethod();

			for (String[] methodSet : iniBean.getMethodSet()) {

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
	}

	public void shortFilter(List<TradeDataBean> list, IniBean iniBean) {

		//sellUnusedMethodが0だった場合、使用するメソッドのみ売却処理→methodでのフィルターをかける
		if (iniBean.getSellUnusedMethod().equals("0")) {
			this.longFilter(list, iniBean);
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

}
