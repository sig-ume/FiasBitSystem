///**
// *
// */
//package jp.sigre.fbs.controller;
//
//import static org.hamcrest.CoreMatchers.*;
//import static org.hamcrest.MatcherAssert.*;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.google.common.io.Files;
//
//import jp.sigre.fbs.database.ConnectDB;
//import jp.sigre.fbs.selenium.trade.IniBean;
//import jp.sigre.fbs.utils.FileUtils;
//
///**
// * @author sigre
// *
// */
//public class DataControllerTest {
//
//	private final static String basePath = System.getProperty("user.dir");
//
//	static FileUtils fileUtils = new FileUtils();
//
//	File iniFile = new File(fileUtils.getIniPath(basePath));
//	IniBean iniBean = fileUtils.iniToBean(iniFile);
//	DataController target;
//
//	static ConnectDB db = new ConnectDB();
//
//	File spFile = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine.csv");
//
//	File dbFile = new File(basePath + "\\db\\TradeInfo.sqlite");
//	File dbOrig = new File(basePath + "\\db\\TradeInfo_orig.sqlite");
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		db.connectStatement();
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		db.closeStatement();
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//		Files.copy(dbOrig, dbFile);
//	}
//
//	/**
//	 * {@link jp.sigre.fbs.controller.DataController#updateSepaCombine()} のためのテスト・メソッド。
//	 */
//	@Test
//	public final void testUpdateSepaCombine_iniBeanがNull() {
//		target = new DataController(null);
//		boolean result = target.updateSepaCombine();
//
//		assertThat(result, is(false));
//	}
//
//	@Test
//	public final void testUpdateSepaCombine_ファイルが空() throws IOException {
//
//		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_ファイルが空.csv");
//		Files.copy(file, spFile);
//
//		target = new DataController(iniBean);
//		boolean result = target.updateSepaCombine();
//
//		assertThat(result, is(false));
//	}
//
//	@Test
//	public final void testUpdateSepaCombine_データが空() throws IOException {
//
//		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_データが空.csv");
//		Files.copy(file, spFile);
//
//		target = new DataController(iniBean);
//		boolean result = target.updateSepaCombine();
//
//		assertThat(result, is(false));
//	}
//
//	@Test
//	public final void testUpdateSepaCombine_正常系() throws IOException {
//
//		File file = new File(basePath + "\\test\\jp.sigre.fbs.controller\\FBSsepaCombine_正常系.csv");
//		Files.copy(file, spFile);
//
//		String bef = db.getTradeViewOfCode("2303").getRealEntryVolume();
//		assertThat(bef, is("203"));
//
//		target = new DataController(iniBean);
//		boolean result = target.updateSepaCombine();
//
//		assertThat(result, is(true));
//		String aft = db.getTradeViewOfCode("2303").getRealEntryVolume();
//
//
//	}
//
//}
