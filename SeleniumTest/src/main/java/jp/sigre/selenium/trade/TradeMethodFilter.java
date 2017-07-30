/**
 *
 */
package jp.sigre.selenium.trade;

import java.util.ArrayList;
import java.util.List;

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

				if (entryMethod.equals(methodSet[0]) && exitMethod.equals(methodSet[1]) && methodSet[2].equals("1")) {
					checkbox.set(i, true);
				}
			}
		}

		for (int i = 0; i<list.size(); i++) {
			if (!checkbox.get(i)) {
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
		this.longFilter(list, iniBean);

		for (int i = 0; i < list.size(); i++) {
			for (int j = i+1; j < list.size(); j++) {
				if (list.get(i).equals(list.get(j))) {
					list.remove(j);
					j--;
				}
			}
		}


		for (TradeDataBean tradeData : list ) {
			System.out.println(tradeData.toString());
		}

	}

}
