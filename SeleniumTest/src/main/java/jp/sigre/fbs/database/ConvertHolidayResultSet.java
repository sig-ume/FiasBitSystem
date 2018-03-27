package jp.sigre.fbs.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.sigre.fbs.log.LogMessage;
import jp.sigre.google.HolidayBean;

/**
 * @author sigre
 *
 */
@SuppressWarnings("unused")
public class ConvertHolidayResultSet{

	public HolidayBean rsToBean(ResultSet rs) throws SQLException {
		HolidayBean info = new HolidayBean();

		info.setDate	(rs.getString("date"));
		info.setSummary	(rs.getString("summary"));

		return info;
	}

	List<HolidayBean> convertHoliday(ResultSet rs) {
		List<HolidayBean> result = new ArrayList<>();

		try {
			while (rs.next()) {
				HolidayBean info = rsToBean(rs);
				result.add(info);
			}
			if(result.size()!=0) return result;
		} catch (SQLException e) {
			new LogMessage().writelnLog(e.toString());
		}
		return new ArrayList<>();
	}

}
