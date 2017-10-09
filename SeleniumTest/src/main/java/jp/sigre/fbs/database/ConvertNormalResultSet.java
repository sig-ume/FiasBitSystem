package jp.sigre.fbs.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.sigre.fbs.selenium.trade.TradeDataBean;

/**
 * @author sigre
 *
 */
@SuppressWarnings("unused")
public class ConvertNormalResultSet implements ConvertResultSet {

	/* (非 Javadoc)
	 * @see jp.sigre.database.ConvertResultSet#rsToBean(java.sql.ResultSet)
	 */
	@Override
	public TradeDataBean rsToBean(ResultSet rs) throws SQLException {
		TradeDataBean info = new TradeDataBean();

		info.setCode				(rs.getString("code"));
		info.setDayTime				(rs.getString("dayTime"));
		info.setType				(rs.getString("type"));
		info.setEntryMethod			(rs.getString("entryMethod"));
		info.setExitMethod			(rs.getString("exitMethod"));
		info.setMINI_CHECK_flg		(rs.getString("MINI_CHECK_flg"));
		info.setRealEntryVolume		(rs.getString("realEntryVolume"));
		info.setEntry_money			(rs.getString("entry_money"));
		info.setCorrectedEntryVolume(rs.getString("correctedEntryVolume"));

		return info;
	}

}
