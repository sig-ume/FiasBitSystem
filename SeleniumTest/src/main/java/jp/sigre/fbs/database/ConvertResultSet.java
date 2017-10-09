package jp.sigre.fbs.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.TradeDataBean;

/**
 * @author sigre
 *
 */
//TODO:abstractいらない？
abstract interface ConvertResultSet {

	default List<TradeDataBean> convertTradeData(ResultSet rs) {
		List<TradeDataBean> result = new ArrayList<>();

		try {
			while (rs.next()) {
				TradeDataBean info = rsToBean(rs);
				result.add(info);
			}
			if(result.size()!=0) return result;
		} catch (SQLException e) {
			new LogMessage().writelnLog(e.toString());
		}
		return new ArrayList<>();
	}

	TradeDataBean rsToBean(ResultSet rs) throws SQLException;


}
