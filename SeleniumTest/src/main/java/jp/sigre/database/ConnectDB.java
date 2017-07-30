/**
 *
 */
package jp.sigre.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.sigre.selenium.trade.TradeDataBean;


/**
 * @author sigre
 * TradeDataTableに接続、やり取りを行う
 */
public class ConnectDB {
	Connection con;
	Statement stmt;

	/**
	 * 実質、接続テスト用
	 * @return
	 */
	public Statement connectStatement() {
		try {
			con = getConnection();
			stmt = con.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (stmt != null) stmt.close();
				if (con  != null) con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return stmt;
	}

	private Connection getConnection() throws SQLException{
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		con = DriverManager
				.getConnection("jdbc:sqlite:db/TradeInfo.sqlite");
		return con;
	}

	/**
	 * DB切断。
	 * @return
	 */
	public boolean closeStatement() {
		try {
			if (stmt != null) stmt.close();
			if (con  != null) con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * TradeDataTableのデータをBeanに格納してリスト化
	 * @return
	 */
	public TradeDataBean getTradeDataList() {
		try {
			con = getConnection();
			String sql = "SELECT * FROM TradeData;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertNormalResultSet().convertTradeData(rs).get(0);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}  finally {
			closeStatement();
		}
		return null;
	}

	public TradeDataBean getTradeViewOfCode() {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCode;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeResultSet().convertTradeData(rs).get(0);
		} catch (SQLException e1) {
			closeStatement();
			e1.printStackTrace();
		}
		return null;
	}

	public List<TradeDataBean> getTradeViewOfCode(String code) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCode WHERE code = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeResultSet().convertTradeData(rs);
		} catch (SQLException e1) {
			closeStatement();
			e1.printStackTrace();
		}
		return null;
	}

	public TradeDataBean getTradeViewOfCodeMethods(String code, String entryMethod, String exitMethod) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeExit WHERE code = ? AND entryMethod = ? AND exitMethod = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, entryMethod);
			pstmt.setString(3, exitMethod);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeMethodsResultSet().convertTradeData(rs).get(0);
		} catch (SQLException e1) {
			closeStatement();
			e1.printStackTrace();
		}
		return null;
	}

	public TradeDataBean getTradeViewOfCodeExit() {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeExit;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeExitResultSet().convertTradeData(rs).get(0);
		} catch (SQLException e1) {
			closeStatement();
			e1.printStackTrace();
		}
		return null;
	}

	public TradeDataBean getTradeViewOfCodeExit(String code, String exitMethod) {
		try {
			con = getConnection();
			String sql = "Select * From TradeViewOfCodeExit WHERE code = ? AND exitMethod = ?;";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, exitMethod);
			ResultSet rs = pstmt.executeQuery();
			return new ConvertCodeExitResultSet().convertTradeData(rs).get(0);
		} catch (SQLException e1) {
			closeStatement();
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 商品を1件DBにInsertする
	 * @param info
	 * @return 更新件数
	 */
	public int insertTradeData(TradeDataBean info) {
		try {
			con = getConnection();
			String sql =  "INSERT INTO TradeData (code, dayTime, type, entryMethod, exitMethod, MINI_CHECK_flg, realEntryVolume, "
					+ "entry_money, correctedEntryVolume) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = getPrepStatementOfAllData(sql, info);

			return pstmt.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return 0;
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
			e1.printStackTrace();
		} finally {
			closeStatement();
		}
		return null;
	}


	/**
	 * 商品を1件DBからDeleteする
	 * @param info
	 * @return 更新件数
	 */
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
			e1.printStackTrace();
		}  finally {
			closeStatement();
		}
		return 0;
	}

	public int updateTradeDataFlg(int id, int updateFlg) {
		try {
			con = getConnection();
			String sql =  "UPDATE  TradeData "
					+ "SET 		flg = ?"
					+ "WHERE	id = ?";
			;
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt   (1, updateFlg);
			pstmt.setInt   (2, id);

			return pstmt.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}  finally {
			closeStatement();
		}
		return 0;
	}

	public boolean isExist(String title) {
		boolean result = false;

		try {
			con = getConnection();
			String sql =  "SELECT * FROM TradeData "
					+ "WHERE title = ?;";

			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);

			result = pstmt.executeQuery().next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}  finally {
			closeStatement();
		}
		return result;
	}


}
