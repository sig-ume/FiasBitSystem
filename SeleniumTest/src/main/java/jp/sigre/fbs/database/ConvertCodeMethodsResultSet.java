package jp.sigre.fbs.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.sigre.fbs.selenium.trade.TradeDataBean;

public class ConvertCodeMethodsResultSet implements ConvertResultSet {

	//TODO:日付、DD、MiniCheck、Moneyうめる

	@Override
	public TradeDataBean rsToBean(ResultSet rs) throws SQLException {
		TradeDataBean info = new TradeDataBean();

		info.setCode			(rs.getString("code"));
		info.setEntryMethod(rs.getString("entryMethod"));
		info.setExitMethod		(rs.getString("exitMethod"));
		info.setRealEntryVolume(rs.getString("realEntryVolume"));

		return info;
	}

}
