package jp.sigre.fbs.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.selenium.trade.IniBean;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.fbs.utils.FileUtils;
import jp.sigre.google.HolidayBean;

/**
 * @author sigre
 *
 */
public class DataControllerTest extends DataController{

	private final static String basePath = System.getProperty("user.dir");

	static FileUtils fileUtils = new FileUtils();

	File iniFile = new File(fileUtils.getIniPath(basePath));
	IniBean iniBean = fileUtils.iniToBean(iniFile);

	String lsFolderPath = iniBean.getLS_FilePath();

	static ConnectDB db = new ConnectDB();

	File spFile = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine.csv");

	File dbFile = new File(basePath + "\\db\\TradeInfo.sqlite");
	File dbOrig = new File(basePath + "\\db\\TradeInfo_orig.sqlite");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db.connectStatement();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.closeStatement();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Files.copy(dbOrig, dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testUpdateSepaCombine_ファイルが空() throws IOException {

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_ファイルが空.csv");
		Files.copy(file, spFile);

		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(false));
	}

	@Test
	public final void testUpdateSepaCombine_データが空() throws IOException {

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_データが空.csv");
		Files.copy(file, spFile);

		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(false));
	}

	@Test
	public final void testUpdateSepaCombine_正常系_併合() throws IOException {

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_正常系.csv");
		Files.copy(file, spFile);

		String bef = db.getTradeViewOfCode("2303").getRealEntryVolume();
		assertThat(bef, is("203"));


		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(true));
		String aft = db.getTradeViewOfCode("2303").getRealEntryVolume();
		assertThat(aft, is("40"));

		String wildAft = db.getTradeViewOfCodeMethods("2303", "wildcard", "wildcard").getRealEntryVolume();
		assertThat(wildAft, is("-163"));

	}

	@Test
	public final void testUpdateSepaCombine_正常系_分割() throws IOException {

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_正常系.csv");
		Files.copy(file, spFile);

		String bef = db.getTradeViewOfCode("2415").getRealEntryVolume();
		assertThat(bef, is("40"));
		System.out.println("test" + bef);

		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(true));
		String aft = db.getTradeViewOfCode("2415").getRealEntryVolume();
		assertThat(aft, is("800"));

		String wildAft = db.getTradeViewOfCodeMethods("2415", "wildcard", "wildcard").getRealEntryVolume();
		assertThat(wildAft, is("760"));


	}

	@Test
	public final void testUpdateSepaCombine_正常系_フラグ不正() throws IOException {

		String code = "9984";

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_正常系.csv");
		Files.copy(file, spFile);

		String bef = db.getTradeViewOfCode(code).getRealEntryVolume();
		assertThat(bef, is("12"));
		System.out.println("test" + bef);

		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(true));
		String aft = db.getTradeViewOfCode(code).getRealEntryVolume();
		assertThat(aft, is("12"));

		String wildAft = db.getTradeViewOfCodeMethods(code, "wildcard", "wildcard").getRealEntryVolume();
		assertThat(wildAft, is("0"));


	}

	@Test
	public final void testUpdateSepaCombine_正常系_小数点分割() throws IOException {

		String code = "9758";

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_正常系.csv");
		Files.copy(file, spFile);

		String bef = db.getTradeViewOfCode(code).getRealEntryVolume();
		assertThat(bef, is("206"));
		System.out.println("test" + bef);

		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(true));
		String aft = db.getTradeViewOfCode(code).getRealEntryVolume();
		assertThat(aft, is("309"));

		String wildAft = db.getTradeViewOfCodeMethods(code, "wildcard", "wildcard").getRealEntryVolume();
		assertThat(wildAft, is("103"));


	}

	@Test
	public final void testUpdateSepaCombine_正常系_小数点併合() throws IOException {

		String code = "8783";

		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_正常系.csv");
		Files.copy(file, spFile);

		String bef = db.getTradeViewOfCode(code).getRealEntryVolume();
		assertThat(bef, is("70"));
		System.out.println("test" + bef);

		DataController target = new DataController();
		boolean result = target.updateSepaCombine(lsFolderPath);

		assertThat(result, is(true));
		String aft = db.getTradeViewOfCode(code).getRealEntryVolume();
		assertThat(aft, is("46"));

		String wildAft = db.getTradeViewOfCodeMethods(code, "wildcard", "wildcard").getRealEntryVolume();
		assertThat(wildAft, is("-24"));


	}

	@Test
	public final void TestMoveTempTradeData_7時() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 20, 7, 0);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(569));
		assertThat(list22.size(), is(30));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_11時50分() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 20, 11, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(586));
		assertThat(list22.size(), is(13));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_14時50分() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 20, 14, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(588));
		assertThat(list22.size(), is(11));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_16時() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 20, 16, 00);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(576));
		assertThat(list22.size(), is(23));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}


	@Test
	public final void TestMoveTempTradeData_土曜日16時() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 21, 16, 00);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		//取引時間外は単元株が過去分のみテーブル移動
		assertThat(list12.size(), is(576));
		assertThat(list22.size(), is(23));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String expected = "2018-07-20"; // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_日曜日16時() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 22, 16, 00);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		//取引時間外は単元株が過去分のみテーブル移動
		assertThat(list12.size(), is(576));
		assertThat(list22.size(), is(23));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String expected = "2018-07-20"; // + " 7:00";
		assertThat(actual, is(expected));
	}


	@Test
	public final void TestMoveTempTradeData_月曜7時() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 23, 7, 0);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(576));
		assertThat(list22.size(), is(23));



		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_月曜11時50分() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 23, 11, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(589));
		assertThat(list22.size(), is(10));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_月曜14時50分() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 6, 23, 14, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(596));
		assertThat(list22.size(), is(3));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_祝日一日目() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 7, 11, 14, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(593));
		assertThat(list22.size(), is(6));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_祝日二日目() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 7, 12, 14, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(593));
		assertThat(list22.size(), is(6));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}


	@Test
	public final void TestMoveTempTradeData_祝日後() {
		DataController target = new DataController();

		target.updateHolidayTable("2018-07-19");

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(34));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(2018, 7, 13, 14, 50);

		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(599));
		assertThat(list22.size(), is(0));


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()); // + " 7:00";
		assertThat(actual, is(expected));
	}



	@Test
	public final void TestUpdateHolidayTable_正常系_データなし() {

		db.deleteHolidays();

		DataController target = new DataController();

		target.updateHolidayTable("2018-02-21");

		List<HolidayBean> holidays =  db.getHolidays();

		assertThat(holidays.size(), is(30));

		HolidayBean firstHoliday = holidays.get(0);

		assertThat(firstHoliday.getDate(), is("2018-03-21"));
	}

	@Test
	public final void TestUpdateHolidayTable_正常系_古いデータあり_更新なし() {

		db.deleteHolidays();

		DataController target = new DataController();

		target.updateHolidayTable("2015-02-21");

		List<HolidayBean> holidays =  db.getHolidays();

		HolidayBean firstHoliday = holidays.get(0);

		assertThat(firstHoliday.getDate(), is("2017-01-01"));

		target.updateHolidayTable("2018-01-01");

		holidays =  db.getHolidays();

		assertThat(holidays.size(), is(30));

		firstHoliday = holidays.get(0);

		assertThat(firstHoliday.getDate(), is("2017-01-01"));
	}


	@Test
	public final void TestUpdateHolidayTable_正常系_古いデータあり_更新あり() {

		db.deleteHolidays();

		DataController target = new DataController();

		target.updateHolidayTable("2015-02-21");

		List<HolidayBean> holidays =  db.getHolidays();

		HolidayBean firstHoliday = holidays.get(0);

		assertThat(firstHoliday.getDate(), is("2017-01-01"));

		target.updateHolidayTable("2018-11-01");

		holidays =  db.getHolidays();

		firstHoliday = holidays.get(0);

		assertThat(firstHoliday.getDate(), is("2018-11-03"));
	}

	@Test
	public final void TestGetUsualCal_DB内データあり_平日() throws ParseException {

		db.deleteHolidays();

		String nowStr = "2018-02-28";

		updateHolidayTable(nowStr);

		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");

		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(sdf.parse(nowStr));

		Calendar usualCal = getUsualCal(nowCal);

		assertThat(usualCal, is(nowCal));
	}

	@Test
	public final void TestGetUsualCal_DB内データあり_土曜() throws ParseException {

		db.deleteHolidays();

		String nowStr = "2018-02-28";

		updateHolidayTable(nowStr);

		nowStr = "2018-03-03";

		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");

		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(sdf.parse(nowStr));

		Calendar usualCal = getUsualCal(nowCal);

		Calendar expectedCal = Calendar.getInstance();
		expectedCal.setTime(sdf.parse("2018-03-02"));
		assertThat(usualCal, is(expectedCal));
	}

	@Test
	public final void TestGetUsualCal_DB内データあり_日曜() throws ParseException {

		db.deleteHolidays();

		String nowStr = "2018-02-28";

		updateHolidayTable(nowStr);

		nowStr = "2018-03-04";

		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");

		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(sdf.parse(nowStr));

		Calendar usualCal = getUsualCal(nowCal);

		Calendar expectedCal = Calendar.getInstance();
		expectedCal.setTime(sdf.parse("2018-03-02"));
		assertThat(usualCal, is(expectedCal));
	}

	@Test
	public final void TestGetUsualCal_DB内データあり_祝日() throws ParseException {

		db.deleteHolidays();

		String nowStr = "2018-02-28";

		updateHolidayTable(nowStr);

		nowStr = "2018-05-05";

		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");

		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(sdf.parse(nowStr));

		Calendar usualCal = getUsualCal(nowCal);

		Calendar expectedCal = Calendar.getInstance();
		expectedCal.setTime(sdf.parse("2018-05-02"));
		assertThat(usualCal, is(expectedCal));
	}

}
