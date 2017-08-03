/**
 *
 */
package jp.sigre.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.sigre.LogMessage;
import jp.sigre.selenium.trade.TradeDataBean;

/**
 * @author sigre
 *
 */
public abstract interface ConvertResultSet {

	public default List<TradeDataBean> convertTradeData(ResultSet rs) {
		List<TradeDataBean> result = new ArrayList<TradeDataBean>();

		try {
			while (rs.next()) {
				TradeDataBean info = rsToBean(rs);
				result.add(info);
			}
			if(result.size()!=0) return result;
		} catch (SQLException e) {
			new LogMessage().writelnLog(e.toString());
		}
		return new ArrayList<TradeDataBean>();
	}

	TradeDataBean rsToBean(ResultSet rs) throws SQLException;


}
