package jp.sigre.fbs.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.google.HolidayBean;


/**
 * @author sigre
 * TradeDataTableに接続、やり取りを行う
 */
public class ConnectDB {
	private Connection con;
	private Statement stmt;
	private final String TRADE_DATA_TABLE = "TradeData";
	private final String TRADE_TEMP_TABLE = "TempTradeData";

	/**
	 * 実質、接続テスト用
	 * @return
	 */
	public void connectStatement() {
		try {
			con = getConnection();
			stmt = con.createStatement();

		} catch (Exception e) {
			new LogMessage().writelnLog(e.toString());
			try {
				if (stmt != null) stmt.close();
				if (con  != null) con.close();
			} catch (SQLException e1) {
				new LogMessage().writelnLog(e1.toString());
			}
		}
	}

	private Connection getConnection() throws SQLException{
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			new LogMessage().writelnLog(e.toString());
		}
		String strPath = "db/TradeInfo.sqlite";
		con = DriverManager
				.getConnection("jdbc:sqlite:" + strPath);
		return con;
	}

	/**
	 * DB切断。
	 * @return
	 */
	public void closeStatement() {
		try {
			if (stmt != null) stmt.close();
			if (con  != null) con.close();
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}
	}

	public List<TradeDataBean> getTradeData() {
		return getTradeData_(TRADE_DATA_TABLE);
	}

	public List<TradeDataBean> getTempTradeData() {
		return getTradeData_(TRADE_TEMP_TABLE);
	}

	private List<TradeDataBean> getTradeData_(String tableName) {
		try {
			con = getConnection();
			String sql = "Select * From " + tableName + ";";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			return new ConvertCodeResultSet().convertTradeData(rs);

		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}
		return null;

	}

	/**
	 * TradeViewOfCodeテーブルから全レコードを取得
	 * @param code
	 * @return
	 */
	public List<TradeDataBean> getTradeViewOfCode() {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCode;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			return new ConvertCodeResultSet().convertTradeData(rs);

		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}
		return null;
	}

	/**
	 * TradeViewOfCodeテーブルから特定コードのレコードを取得
	 * （特定レコードの合計所有株数）
	 * レコードが存在しない場合、realEntryVolume=0の空Beanを返す。
	 * @param code
	 * @return
	 */
	public TradeDataBean getTradeViewOfCode(String code) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCode WHERE code = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();

			List<TradeDataBean> list = new ConvertCodeResultSet().convertTradeData(rs);
			//Listのサイズ0の場合の処理
			if (list.size()==0) {
				TradeDataBean noDataBean = new TradeDataBean();
				noDataBean.setRealEntryVolume("0");
				noDataBean.setCode(code);
				return noDataBean;
			}
			return list.get(0);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}
		return null;
	}


	/**
	 * TradeViewOfCodeMethodビューから特定コード,売却メソッドのレコードリストを取得
	 * （特定レコードの合計所有株数）
	 * @param code
	 * @return
	 */
	public List<TradeDataBean> getTradeViewOfCodeMethods(String code, String exitMethod) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods WHERE code = ? AND exitMethod = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, exitMethod);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeMethodsResultSet().convertTradeData(rs);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}
		return null;
	}


	/**
	 * TradeViewOfCodeMethodビューから特定コード,売却メソッドのレコードリストを取得
	 * （特定レコードの合計所有株数）
	 * @param code
	 * @return
	 */
	public List<TradeDataBean> getTradeViewOfCodeMethodsStockOrderAsc(String code) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods WHERE code = ? Order By realEntryVolume asc;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeMethodsResultSet().convertTradeData(rs);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}
		return null;
	}


	/**
	 * TradeViewOfCodeMethodビューから特定コード,購入/売却メソッドのレコードを取得
	 * （特定レコードの合計所有株数）
	 * @param code
	 * @return
	 */
	public TradeDataBean getTradeViewOfCodeMethods(String code, String entryMethod, String exitMethod) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods WHERE code = ? AND entryMethod = ? AND exitMethod = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, entryMethod);
			pstmt.setString(3, exitMethod);
			ResultSet rs = pstmt.executeQuery();

			List<TradeDataBean> list = new ConvertCodeMethodsResultSet().convertTradeData(rs);
			//Listのサイズ0の場合の処理
			if (list.size()==0) {
				TradeDataBean noDataBean = new TradeDataBean();
				noDataBean.setRealEntryVolume("0");
				noDataBean.setCode(code);
				return noDataBean;
			}
			return list.get(0);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}

	public List<TradeDataBean> getTradeViewOfCodeMethods_Unused(String entryMethod, String exitMethod) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods WHERE entryMethod = ? AND exitMethod = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, entryMethod);
			pstmt.setString(2, exitMethod);

			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeMethodsResultSet().convertTradeData(rs);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}

	/**
	 * TradeViewOfCodeMethodビューから特定コードのレコードリストを取得
	 * （特定レコードの合計所有株数）
	 * @param
	 * @return
	 */
	public List<TradeDataBean> getTradeViewOfCodeMethods() {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods;";
			PreparedStatement pstmt = con.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeMethodsResultSet().convertTradeData(rs);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}

	/**
	 * TradeViewOfCodeMethodから特定コード,売却メソッドのレコードリストを取得
	 * （特定レコードの合計所有株数）
	 * レコードが存在しない場合、realEntryVolume=0の空Beanを返す。
	 * TODO:説明と内容が違う
	 * @param code
	 * @return
	 */
	public TradeDataBean getHighestTradeViewOfCodeMethods(String code) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods WHERE code = ? ORDER BY realEntryVolume DESC;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();

			List<TradeDataBean> list = new ConvertCodeMethodsResultSet().convertTradeData(rs);
			//Listのサイズ0の場合の処理
			if (list.size()==0) {
				TradeDataBean noDataBean = new TradeDataBean();
				noDataBean.setRealEntryVolume("0");
				noDataBean.setCode(code);
				return noDataBean;
			}
			return list.get(0);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}

	/**
	 * TradeViewOfCodeMethodから特定コードかつ,entryMethod,exitMethodが指定されたものでないレコードリストを取得
	 * レコードが存在しない場合、realEntryVolume=0の空Beanを返す。
	 * @param code
	 * @return
	 */
	public TradeDataBean getHighestTradeViewOfCodeMethods(String code, String entryMethod, String exitMethod) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeMethods WHERE code = ? AND entryMethod != ? "
					+ "AND exitMethod != ? ORDER BY realEntryVolume DESC;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, entryMethod);
			pstmt.setString(3, exitMethod);
			ResultSet rs = pstmt.executeQuery();

			List<TradeDataBean> list = new ConvertCodeMethodsResultSet().convertTradeData(rs);
			//Listのサイズ0の場合の処理
			if (list.size()==0) {
				TradeDataBean noDataBean = new TradeDataBean();
				noDataBean.setRealEntryVolume("0");
				noDataBean.setCode(code);
				return noDataBean;
			}
			return list.get(0);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}

	public List<HolidayBean> getHolidays() {
		try {
			con = getConnection();
			String sql = "Select * From JapanHoliday Order By date asc;";
			PreparedStatement pstmt = con.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			return new ConvertHolidayResultSet().convertHoliday(rs);
		} catch (SQLException e1) {
			closeStatement();
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}


	public List<HolidayBean> getHolidaysPastEqualToday(String strDate) {
		try {
			con = getConnection();
			String sql = "Select * From JapanHoliday where date <= ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, strDate);

			ResultSet rs = pstmt.executeQuery();
			return new ConvertHolidayResultSet().convertHoliday(rs);
		} catch (SQLException e1) {
			closeStatement();
			//System.out.println("Test");
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return null;
	}

	public int moveTempTradeData(String borderDate) {

		String sql = "INSERT INTO " + TRADE_DATA_TABLE + "(code, dayTime, type, entryMethod, exitMethod, "
				+ "MINI_CHECK_flg, realEntryVolume, entry_money, correctedEntryVolume, timeStamp) "
				+ "SELECT code, dayTime, type, entryMethod, exitMethod, MINI_CHECK_flg, realEntryVolume, entry_money, "
				+ "correctedEntryVolume, timeStamp FROM TempTradeData WHERE timeStamp <= ? ;";
		//System.out.println(sql);

		int result = 0;

		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, borderDate);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			new LogMessage().writelnLog(e.toString());
			return 0;
		} finally {
			closeStatement();
		}

		return result;

	}


	public int moveTempTradeTangenData() {

		String sql = "INSERT INTO " + TRADE_DATA_TABLE + "(code, dayTime, type, entryMethod, exitMethod, "
				+ "MINI_CHECK_flg, realEntryVolume, entry_money, correctedEntryVolume, timeStamp) "
				+ "SELECT code, dayTime, type, entryMethod, exitMethod, MINI_CHECK_flg, realEntryVolume, entry_money, "
				+ "correctedEntryVolume, timeStamp FROM TempTradeData WHERE MINI_CHECK_flg = '0';";
		//System.out.println(sql);

		int result = 0;

		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			new LogMessage().writelnLog(e.toString());
			return 0;
		} finally {
			closeStatement();
		}

		return result;
	}


	public int moveTempTradeData_(String date, int miniCheckFlg) {

		String sql = "INSERT INTO " + TRADE_DATA_TABLE + "(code, dayTime, type, entryMethod, exitMethod, "
				+ "MINI_CHECK_flg, realEntryVolume, entry_money, correctedEntryVolume, timeStamp) "
				+ "SELECT code, dayTime, type, entryMethod, exitMethod, MINI_CHECK_flg, realEntryVolume, entry_money, "
				+ "correctedEntryVolume, timeStamp FROM TempTradeData WHERE timeStamp <= ? "
				+ "AND MINI_CHECK_flg = ?;";
		//System.out.println(sql);

		new LogMessage(sql + ": " + date + ": " + miniCheckFlg);
		int result = 0;

		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, date);
			pstmt.setInt(2, miniCheckFlg);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			new LogMessage().writelnLog(e.toString());
			return 0;
		} finally {
			closeStatement();
		}

		return result;
	}

	public int moveTempTradeTangenData(String date) {
		return moveTempTradeData_(date, 0);
	}

	public int moveTempTradeSData(String date) {
		return moveTempTradeData_(date, 1);
	}


	public int moveTempTradeFurikaeData(String date) {
		return moveTempTradeData_(date, 2);
	}

	/**
	 * 商品を1件DBにInsertする
	 * @param info
	 * @return 更新件数
	 */
	public int insertTradeData(TradeDataBean info) {

		return insertTrade(info, TRADE_DATA_TABLE);

	}

	/**
	 * 商品を1件TempDBにInsertする
	 * @param info
	 * @return 更新件数
	 */
	public int insertTempTradeData(TradeDataBean info) {

		return insertTrade(info, TRADE_TEMP_TABLE);

	}

	private int insertTrade(TradeDataBean info, String tableName) {
		int result = 0;

		try {
			con = getConnection();
			String sql =  "INSERT INTO " + tableName + " (code, dayTime, type, entryMethod, exitMethod, "
					+ "MINI_CHECK_flg, realEntryVolume, "
					+ "entry_money, correctedEntryVolume) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = getPrepStatementOfAllData(sql, info);

			result = pstmt.executeUpdate();
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return result;
	}

	/**
	 * 休日テーブルにレコードを追加
	 * @param bean
	 */
	public int insertHolidays(HolidayBean bean) {
		int result = 0;

		try {
			con = getConnection();
			String sql =  "INSERT INTO JapanHoliday (date, summary) VALUES (?, ?);";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getDate());
			pstmt.setString(2, bean.getSummary());

			result = pstmt.executeUpdate();
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}

		return result;
	}

	private PreparedStatement getPrepStatementOfAllData(String sql, TradeDataBean info) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, info.getCode());
		pstmt.setString(2, info.getDayTime());
		pstmt.setString(3, info.getType());
		pstmt.setString(4, info.getEntryMethod());
		pstmt.setString(5, info.getExitMethod());
		pstmt.setString(6, info.getMINI_CHECK_flg());
		pstmt.setString(7, info.getRealEntryVolume());
		pstmt.setString(8, info.getEntry_money());
		pstmt.setString(9, info.getCorrectedEntryVolume());
		return pstmt;
	}

	/**
	 * TradeDataTableの列名一覧を取得
	 * @return
	 */
	@SuppressWarnings("unused")
	public String[] getColumns() {

		try {
			con = getConnection();
			String sql = "SELECT * FROM TradeData;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			String result[] = new String[columnCount];
			for (int iLoop = 0 ;iLoop < columnCount ; iLoop ++){
				//カラム名取得
				result[iLoop] = metaData.getColumnName(iLoop + 1);
			}
			return result;
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		} finally {
			closeStatement();
		}
		return null;

	}

	public int deleteAllTradeData() {
		return deleteAllData(TRADE_DATA_TABLE);
	}

	public int deleteAllTempTradeData() {
		return deleteAllData(TRADE_TEMP_TABLE);
	}

	private int deleteAllData(String tableName) {

		try {
			con = getConnection();
			String sql = "DELETE FROM " + tableName + ";";
			PreparedStatement pstmt = con.prepareStatement(sql);
			//DELETE文を実行する
            return pstmt.executeUpdate();

		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;
	}

	public int deleteTempTradeTangenData() {
		return deleteTradeTangenData_(TRADE_TEMP_TABLE);
	}

	public int deleteTempTradeTangenData(String date) {

		return deleteTradeTangenData_(TRADE_TEMP_TABLE, date);
	}

	public int deleteTempTradeSData(String date) {
		return deleteTradeData_(TRADE_TEMP_TABLE, date, 1);
	}

	public int deleteTempTradeData(String borderDate) {
		return deleteTradeData_(TRADE_TEMP_TABLE, borderDate);
	}

	public int deleteTempTradeFurikaeData(String date) {
		return deleteTradeData_(TRADE_TEMP_TABLE, date, 2);
	}


	private int deleteTradeData_(String tableName, String borderDate) {

		try {
			con = getConnection();
			String sql = "DELETE FROM " + tableName + " WHERE timeStamp <= '" + borderDate + ";";
			PreparedStatement pstmt = con.prepareStatement(sql);
			//DELETE文を実行する
            return pstmt.executeUpdate();

		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;

	}

	private int deleteTradeData_(String tableName, String borderDate, int miniCheckFlg) {

		try {
			con = getConnection();
			String sql = "DELETE FROM " + tableName + " WHERE timeStamp <= ? AND MINI_CHECK_flg = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, borderDate);
			pstmt.setInt(2, miniCheckFlg);
			//DELETE文を実行する
            return pstmt.executeUpdate();

		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;

	}

	private int deleteTradeTangenData_(String tableName, String borderDate) {
		return deleteTradeData_(tableName, borderDate, 0);
	}


	private int deleteTradeTangenData_(String tableName) {

		try {
			con = getConnection();
			String sql = "DELETE FROM " + tableName + " WHERE MINI_CHECK_flg = '0';";
			PreparedStatement pstmt = con.prepareStatement(sql);
			//DELETE文を実行する
            return pstmt.executeUpdate();

		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;
	}

	public int deletePastHoliday(String strDate) {
		try {
			con = getConnection();
			String sql = "DELETE FROM JapanHoliday WHERE date < ?;";

			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, strDate);

			return pstmt.executeUpdate();
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;
	}

	public int deleteHolidays() {
		try {
			con = getConnection();
			String sql = "DELETE FROM JapanHoliday;";

			PreparedStatement pstmt = con.prepareStatement(sql);

			return pstmt.executeUpdate();
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;
	}


	/**
	 * 商品を1件DBからDeleteする
	 * @param info
	 * @return 更新件数
	 */
	@SuppressWarnings("unused")
    public int deleteTradeData(TradeDataBean info) {
		try {
			con = getConnection();
			String sql =  "DELETE FROM TradeData "
					+ "WHERE	id = ?"
					+ "AND		title = ?"
					+ "AND		author = ?"
					+ "AND		url = ?"
					+ "AND		releasedate = ?"
					+ "AND		flg = ?;"
					;
			PreparedStatement pstmt = getPrepStatementOfAllData(sql, info);

			return pstmt.executeUpdate();
		} catch (SQLException e1) {
			new LogMessage().writelnLog(e1.toString());
		}  finally {
			closeStatement();
		}

		return 0;
	}


}

