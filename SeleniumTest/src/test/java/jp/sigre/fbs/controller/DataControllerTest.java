package jp.sigre.fbs.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	@Test
	public final void testGetSameCodeLists_bean1つ() {
		TradeDataBean bean0000 = new TradeDataBean();
		bean0000.setCode("0000");

		List<TradeDataBean> list = new ArrayList<>();
		list.add(bean0000);
		List<TradeDataBean> expected0 = new ArrayList<>(list);

		List<List<TradeDataBean>> result = getSameCodeLists(list);

		assertThat(result.size(), is(1));
		List<TradeDataBean> result0 = result.get(0);
		assertThat(result0, is(expected0));
		assertThat(list.size(), is(0));
	}

	@Test
	public final void testGetSameCodeLists_bean2つ_同コード() {
		TradeDataBean bean0000 = new TradeDataBean();
		bean0000.setCode("0000");

		List<TradeDataBean> list = new ArrayList<>();
		list.add(bean0000);
		list.add(bean0000);
		List<TradeDataBean> expected0 = new ArrayList<>(list);

		List<List<TradeDataBean>> result = getSameCodeLists(list);

		assertThat(result.size(), is(1));
		List<TradeDataBean> result0 = result.get(0);
		assertThat(result0, is(expected0));
		assertThat(list.size(), is(0));
	}

	@Test
	public final void testGetSameCodeLists_bean2つ_別コード() {
		TradeDataBean bean0000 = new TradeDataBean();
		bean0000.setCode("0000");
		TradeDataBean bean0001 = new TradeDataBean();
		bean0001.setCode("0001");

		List<TradeDataBean> list = new ArrayList<>();
		list.add(bean0000);
		list.add(bean0001);
		List<TradeDataBean> expected0 = new ArrayList<>(list.subList(0, 1));
		List<TradeDataBean> expected1 = new ArrayList<>(list.subList(1, 2));

		List<List<TradeDataBean>> result = getSameCodeLists(list);

		assertThat(result.size(), is(2));
		List<TradeDataBean> result0 = result.get(0);
		assertThat(result0, is(expected0));
		List<TradeDataBean> result1 = result.get(1);
		assertThat(result1, is(expected1));
		assertThat(list.size(), is(0));
	}

	@Test
	public final void testGetSameCodeLists_bean複数_同コード() {
		TradeDataBean bean0000 = new TradeDataBean();
		bean0000.setCode("0000");

		List<TradeDataBean> list = new ArrayList<>();
		list.add(bean0000);
		list.add(bean0000.clone());
		list.add(bean0000.clone());
		list.add(bean0000.clone());
		List<TradeDataBean> expected0 = new ArrayList<>(list);

		List<List<TradeDataBean>> result = getSameCodeLists(list);

		assertThat(result.size(), is(1));
		List<TradeDataBean> result0 = result.get(0);
		assertThat(result0, is(expected0));
		assertThat(result0.size(), is(4));
		assertThat(list.size(), is(0));
	}


	@Test
	public final void testGetSameCodeLists_bean複数_別コード() {
		TradeDataBean bean0000 = new TradeDataBean();
		bean0000.setCode("0000");
		TradeDataBean bean0001 = new TradeDataBean();
		bean0001.setCode("0001");
		TradeDataBean bean0002 = new TradeDataBean();
		bean0002.setCode("0002");
		TradeDataBean bean0003 = new TradeDataBean();
		bean0003.setCode("0003");
		TradeDataBean bean0004 = new TradeDataBean();
		bean0004.setCode("0004");


		List<TradeDataBean> list = new ArrayList<>();
		list.add(bean0000);	//0
		list.add(bean0001); //1
		list.add(bean0004); //2
		list.add(bean0000.clone()); //3
		list.add(bean0001.clone()); //4
		list.add(bean0002); //5
		list.add(bean0003); //6
		list.add(bean0003.clone()); //7
		list.add(bean0004.clone()); //8
		list.add(bean0003.clone()); //9

		int i = 0;
		for (TradeDataBean bean : list) bean.setRealEntryVolume(String.valueOf(i++));
		List<List<TradeDataBean>> result = getSameCodeLists(list);

		assertThat(result.size(), is(5));

		List<TradeDataBean> result0 = result.get(0);
		assertThat(result0.size(), is(2));
		for (TradeDataBean resultBean : result0) assertThat(resultBean.getCode(), is("0000"));
		assertThat(result0.get(0).getRealEntryVolume(), is("0"));
		assertThat(result0.get(1).getRealEntryVolume(), is("3"));

		List<TradeDataBean> result1 = result.get(1);
		assertThat(result1.size(), is(2));
		for (TradeDataBean resultBean : result1) assertThat(resultBean.getCode(), is("0001"));
		assertThat(result1.get(0).getRealEntryVolume(), is("1"));
		assertThat(result1.get(1).getRealEntryVolume(), is("4"));

		List<TradeDataBean> result4 = result.get(2);
		assertThat(result4.size(), is(2));
		for (TradeDataBean resultBean : result4) assertThat(resultBean.getCode(), is("0004"));
		assertThat(result4.get(0).getRealEntryVolume(), is("2"));
		assertThat(result4.get(1).getRealEntryVolume(), is("8"));

		List<TradeDataBean> result2 = result.get(3);
		assertThat(result2.size(), is(1));
		for (TradeDataBean resultBean : result2) assertThat(resultBean.getCode(), is("0002"));
		assertThat(result2.get(0).getRealEntryVolume(), is("5"));

		List<TradeDataBean> result3 = result.get(4);
		assertThat(result3.size(), is(3));
		for (TradeDataBean resultBean : result3) assertThat(resultBean.getCode(), is("0003"));
		assertThat(result3.get(0).getRealEntryVolume(), is("6"));
		assertThat(result3.get(1).getRealEntryVolume(), is("7"));
		assertThat(result3.get(2).getRealEntryVolume(), is("9"));


		assertThat(list.size(), is(0));
	}
}
