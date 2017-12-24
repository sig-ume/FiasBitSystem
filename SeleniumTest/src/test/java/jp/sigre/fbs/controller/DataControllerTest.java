package jp.sigre.fbs.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.IOException;
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

/**
 * @author sigre
 *
 */
public class DataControllerTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Files.copy(dbOrig, dbFile);
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
	public final void TestMoveTempTradeData_13時() {
		DataController target = new DataController();

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(3));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(Calendar.HOUR_OF_DAY, 13);
		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(568));
		assertThat(list22.size(), is(0));

		assertThat(list21, is(list12.subList(list12.size()-3, list12.size())));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()) + " 10:30";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_10時() {
		DataController target = new DataController();

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(3));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(Calendar.HOUR_OF_DAY, 10);
		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(568));
		assertThat(list22.size(), is(0));

		assertThat(list21, is(list12.subList(list12.size()-3, list12.size())));

		nowCal.add(Calendar.DAY_OF_MONTH, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String expected = sdf.format(nowCal.getTime()) + " 21:30";
		assertThat(actual, is(expected));
	}

	@Test
	public final void TestMoveTempTradeData_9時() {
		DataController target = new DataController();

		List<TradeDataBean> list11 = db.getTradeData();
		List<TradeDataBean> list21 = db.getTempTradeData();
		assertThat(list11.size(), is(565));
		assertThat(list21.size(), is(3));

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(Calendar.HOUR_OF_DAY, 9);
		String actual = target.moveTempTradeData(nowCal);

		List<TradeDataBean> list12 = db.getTradeData();
		List<TradeDataBean> list22 = db.getTempTradeData();
		assertThat(list12.size(), is(565));
		assertThat(list22.size(), is(3));

		assertThat(list11, is(list12));
		assertThat(list21, is(list22));

		assertThat(actual, is(""));
	}
}
