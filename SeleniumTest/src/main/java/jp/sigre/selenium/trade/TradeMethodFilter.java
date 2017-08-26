/**
 *
 */
package jp.sigre.selenium.trade;

import java.util.ArrayList;
import java.util.List;

import jp.sigre.database.ConnectDB;

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
			System.out.println(tradeData);
			String entryMethod = tradeData.getEntryMethod();
			String exitMethod = tradeData.getExitMethod();

			for (String[] methodSet : iniBean.getMethodSet()) {

				System.out.println("フィルター前:" + list.get(0));

				if (entryMethod.equals(methodSet[0]) && exitMethod.equals(methodSet[1]) && !methodSet[2].equals("0")) {
					System.out.println("チェックTrue: " + i);
					checkbox.set(i, true);
				}
			}

			if(exitMethod.equals("wildcard")) {
				System.out.println("wildcardチェックTrue: " + i);
				checkbox.set(i, true);
			}
		}

		for (int i = 0; i<list.size(); i++) {
			if (!checkbox.get(i)) {
				System.out.println("削除");
				list.remove(i);
				checkbox.remove(i);
				i--;
			}
		}

		for (TradeDataBean tradeData : list ) {
			System.out.println(tradeData.toString());
		}

		System.out.println("-------------");
	}

	public void shortFilter(List<TradeDataBean> list, IniBean iniBean) {

		//sellUnusedMethodが0だった場合、使用するメソッドのみ売却処理→methodでのフィルターをかける
		if (iniBean.getSellUnusedMethod().equals("0")) {
			this.longFilter(list, iniBean);
		}


		for (int i = 0; i < list.size(); i++) {
			for (int j = i+1; j < list.size(); j++) {
				if (list.get(i).equals(list.get(j))) {
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
			if (dbBean.getRealEntryVolume()=="0") {
				list.remove(i);
				i--;
			}
			//System.out.println(tradeData.toString());
		}

	}

}
