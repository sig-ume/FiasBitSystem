package jp.sigre.fbs.utils;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.sigre.fbs.controller.SepaCombineBean;
import jp.sigre.fbs.selenium.trade.TradeDataBean;

public class FileUtilsTest {

	FileUtils target = new FileUtils();
	String basePath = System.getProperty("user.dir");
	String folderPath = basePath + File.separator + "test\\jp.sigre.fbs.controller";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCsvToFiaElite_正常系() {
		List<TradeDataBean> list = target.csvToFiaElite(folderPath + File.separator + "yyyy-mm-dd_order_STOCK_LIST_正常系.csv");

		assertThat(list.size(), is(5));
		TradeDataBean bean = list.get(0);

		assertThat(bean.getCode(), is("1400_T"));
		assertThat(bean.getType(), is("DD"));
		assertThat(bean.getEntryMethod(), is("technique.Technique06.IDO_HEKIN_3_S"));
		assertThat(bean.getExitMethod(), is("technique.Technique04.MACD_M_S_OVER0"));
		assertThat(bean.getRealEntryVolume(), is(nullValue()));

		bean = list.get(4);

		assertThat(bean.getCode(), is("1716_T"));
		assertThat(bean.getType(), is("DD"));
		assertThat(bean.getEntryMethod(), is("technique.Technique06.IDO_HEKIN_3_S"));
		assertThat(bean.getExitMethod(), is("technique.Technique04.MACD_M_S_OVER0"));
		assertThat(bean.getRealEntryVolume(), is(nullValue()));

	}

	@Test
	public void testCsvToFiaElite_データが空() {
		List<TradeDataBean> list = target.csvToFiaElite(folderPath + File.separator + "yyyy-mm-dd_order_STOCK_LIST_データが空.csv");

		assertThat(list.size(), is(0));
	}

	@Test
	public void testCsvToFiaElite_ファイルが空() {
		List<TradeDataBean> list = target.csvToFiaElite(folderPath + File.separator + "yyyy-mm-dd_order_STOCK_LIST_ファイルが空.csv");

		assertThat(list.size(), is(0));
	}

//	@Test
//	public void testCsvToTorihikiData() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testCsvToIdPass() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testCsvToFiaKeep() {
//		fail("まだ実装されていません");
//	}
//
	@Test
	public void testCsvToSepaCombine_正常系() {
		List<SepaCombineBean> list = target.csvToSepaCombine(folderPath + File.separator + "FBSsepaCombine_正常系.csv");

		assertThat(list.size(), is(39));
		SepaCombineBean bean = list.get(38);

		//"9967","0","5"
		assertThat(bean.getCode(), is("2415"));
		assertThat(bean.getChecksepa_combine(), is("1"));
		assertThat(bean.getAjustRate(), is("2"));
	}

	@Test
	public void testCsvToSepaCombine_データが空() {
		List<SepaCombineBean> list = target.csvToSepaCombine(folderPath + File.separator + "FBSsepaCombine_データが空.csv");

		assertThat(list.size(), is(0));
	}

	@Test
	public void testCsvToSepaCombine_ファイルが空() {
		List<SepaCombineBean> list = target.csvToSepaCombine(folderPath + File.separator + "FBSsepaCombine_ファイルが空.csv");

		assertThat(list.size(), is(0));
	}

//	@Test
//	public void testIniToBean() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testMakeTradeDataFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testMakeBackupDataFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testWriteFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetIdPassFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetLFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetSFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetMovedLFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetMovedSFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetBuyRemainsFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetSellRemainsFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetIniPath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetKeyPath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetTodayDate() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testRemoveTradeDataFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetExePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testDeleteKickFiles() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testDeleteFileString() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testDeleteFileFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetFiaKeepFilePath() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testDeleteKeepFiles() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testBackupDbFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testGetSepaCombineFilePath() {
//		fail("まだ実装されていません");
//	}

}
