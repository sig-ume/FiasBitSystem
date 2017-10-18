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

		assertThat(list.size(), is(410));
		SepaCombineBean bean = list.get(409);

		//"9967","0","5"
		assertThat(bean.getCode(), is("9967"));
		assertThat(bean.getChecksepa_combine(), is("0"));
		assertThat(bean.getAjustRate(), is("5"));
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
