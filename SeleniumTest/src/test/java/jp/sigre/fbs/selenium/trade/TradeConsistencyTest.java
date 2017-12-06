package jp.sigre.fbs.selenium.trade;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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

public class TradeConsistencyTest extends TradeConsistency {

	String strLsFolderPath = "C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\test\\jp.sigre.fbs.selenium.trade";
	String strDbPath = "C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\db";
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	String toName = sdf.format(cal.getTime()) + "_order_STOCK_LIST.csv";

	File to = new File(strLsFolderPath + File.separator + toName);
	File db = new File(strDbPath + File.separator + "TradeInfo.sqlite");

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
	public void testCheckDbAndSbiStock() {

	}

	@Test
	public void testCheckDbAndFiaKeep() {

	}

	@Test
	public void testCheckDbAndFiaElite_齟齬なし() throws IOException {
		String fromName = "yyyy-mm-dd_order_STOCK_LIST_正常系.csv";
		String fromDbName = "TradeInfo_齟齬なし.sqlite";

		File from = new File(strLsFolderPath + File.separator + fromName);
		File fromDb = new File(strLsFolderPath + File.separator + fromDbName);

		Files.copy(from, to);
		Files.copy(fromDb, db);

		List<TradeDataBean> result = checkDbAndFiaElite(strLsFolderPath);

		assertThat(result.size(), is(0));

	}

	@Test
	public void testCheckDbAndFiaElite_齟齬あり() throws IOException {
		String fromName = "yyyy-mm-dd_order_STOCK_LIST_正常系.csv";
		String fromDbName = "TradeInfo_齟齬あり.sqlite";

		File from = new File(strLsFolderPath + File.separator + fromName);
		File fromDb = new File(strLsFolderPath + File.separator + fromDbName);

		Files.copy(from, to);
		Files.copy(fromDb, db);

		List<TradeDataBean> result = checkDbAndFiaElite(strLsFolderPath);

		assertThat(result.size(), is(3));
		TradeDataBean bean0 = result.get(0);
		TradeDataBean bean1 = result.get(1);
		TradeDataBean bean2 = result.get(2);

		TradeDataBean actu0 = new TradeDataBean();
		actu0.setCode("1400");
		actu0.setEntryMethod("technique.Technique06.IDO_HEKIN_3_S");
		actu0.setExitMethod("test");
		actu0.setRealEntryVolume("49");
		assertThat(bean0, is(actu0));

		TradeDataBean actu1 = new TradeDataBean();
		actu1.setCode("1400");
		actu1.setEntryMethod("test");
		actu1.setExitMethod("technique.Technique06.IDO_HEKIN_2_L");
		actu1.setRealEntryVolume("30");
		assertThat(bean1, is(actu1));


		TradeDataBean actu2 = new TradeDataBean();
		actu2.setCode("3660");
		actu2.setEntryMethod("technique.Technique08.MACD_IDOHEIKIN_L");
		actu2.setExitMethod("technique.Technique06.IDO_HEKIN_2_L");
		actu2.setRealEntryVolume("13");
		assertThat(bean2, is(actu2));

	}

	@Test
	public void testCheckDbAndFiaElite_異常系_パス設定() throws IOException {

		List<TradeDataBean> result = checkDbAndFiaElite("C:\\abbbbbb");

		assertThat(result.size(), is(0));

	}


}
