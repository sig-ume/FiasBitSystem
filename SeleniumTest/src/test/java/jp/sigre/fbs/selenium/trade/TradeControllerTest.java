package jp.sigre.fbs.selenium.trade;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.sigre.fbs.bean.TradeSetBean;

public class TradeControllerTest extends TradeController {

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
//	public void testGetIniBean() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testTradeSetup() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testTrade() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testLogin() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testLogout() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testConsistStock() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testMakeBackupFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testAtoshimatsuDataFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testDeleteOtherFiles() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testMakeDigestFile() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testBackupDbFile() {
//		fail("まだ実装されていません");
//	}
//

	@Test
	public void testGetTradeSets_単元１つ() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "0", "200", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(1));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(200));
		assertThat(resultT.getBeanList(), is(sameCodeList));
		assertThat(resultT.getIsMini(), is("0"));

	}

	@Test
	public void testGetTradeSets_S株１つ() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "99", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(1));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(99));
		assertThat(resultT.getBeanList(), is(cloneList));
		assertThat(resultT.getIsMini(), is("1"));

	}

	@Test
	public void testGetTradeSets_単元複数() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "0", "1000", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry2", "exit2", "0", "1000", "", "");
		TradeDataBean bean3 = new TradeDataBean("0000", "", "", "entry3", "exit3", "0", "900", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);
		sameCodeList.add(bean3);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(1));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(2900));
		assertThat(resultT.getBeanList(), is(sameCodeList));
		assertThat(resultT.getIsMini(), is("0"));

	}

	@Test
	public void testGetTradeSets_S株複数から単元() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "20", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "31", "", "");
		TradeDataBean bean3 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "49", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);
		sameCodeList.add(bean3);

		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(1));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(100));
		assertThat(resultT.getBeanList(), is(cloneList));
		assertThat(resultT.getIsMini(), is("0"));

	}

	@Test
	public void testGetTradeSets_S株複数からS株() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "20", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "31", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);

		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(1));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(51));
		assertThat(resultT.getBeanList(), is(cloneList));
		assertThat(resultT.getIsMini(), is("1"));

	}


	@Test
	public void testGetTradeSets_S株複数から単S() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "20", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "31", "", "");
		TradeDataBean bean3 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "59", "", "");
		TradeDataBean bean3a = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "10", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);
		sameCodeList.add(bean3);

		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);
		cloneList.set(0, bean3a);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(2));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(100));
		assertThat(resultT.getBeanList(), is(cloneList));
		assertThat(resultT.getIsMini(), is("0"));

		TradeSetBean resultS = result.get(1);
		assertThat(resultS.getCode(), is("0000"));
		assertThat(resultS.getVolume(), is(10));
		assertThat(resultS.getBeanList(), is(cloneList.subList(0, 1)));
		assertThat(resultS.getIsMini(), is("1"));

	}

	@Test
	public void testGetTradeSets_S株複数から単S２() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "20", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "31", "", "");
		TradeDataBean bean3 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "99", "", "");
		TradeDataBean bean2a = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "30", "", "");
		TradeDataBean bean2b = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "1", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);
		sameCodeList.add(bean3);

		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);
		cloneList.set(1, bean2a);
		cloneList.remove(2);

		List<TradeDataBean> cloneList2 = new ArrayList<>(sameCodeList);
		cloneList2.remove(0);
		cloneList2.set(0, bean2b);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(2));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(100));
		assertThat(resultT.getBeanList(), is(cloneList2));
		assertThat(resultT.getIsMini(), is("0"));

		TradeSetBean resultS = result.get(1);
		assertThat(resultS.getCode(), is("0000"));
		assertThat(resultS.getVolume(), is(50));
		assertThat(resultS.getBeanList(), is(cloneList));
		assertThat(resultS.getIsMini(), is("1"));

	}


	@Test
	public void testGetTradeSets_単Sから単S() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "20", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry1", "exit1", "0", "100", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);

		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(2));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(100));
		assertThat(resultT.getBeanList(), is(cloneList.subList(1,2)));
		assertThat(resultT.getIsMini(), is("0"));

		TradeSetBean resultS = result.get(1);
		assertThat(resultS.getCode(), is("0000"));
		assertThat(resultS.getVolume(), is(20));
		assertThat(resultS.getBeanList(), is(cloneList.subList(0, 1)));
		assertThat(resultS.getIsMini(), is("1"));

	}

	@Test
	public void testGetTradeSets_S単複数から単S() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "80", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry2", "exit2", "1", "60", "", "");
		TradeDataBean bean3 = new TradeDataBean("0000", "", "", "entry3", "exit3", "1", "100", "", "");
		TradeDataBean bean4	 = new TradeDataBean("0000", "", "", "entry4", "exit4", "1", "200", "", "");
		TradeDataBean bean1a = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "40", "", "");

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);
		sameCodeList.add(bean3);
		sameCodeList.add(bean4);

		List<TradeDataBean> cloneList = new ArrayList<>(sameCodeList);
		cloneList.set(0, bean1a);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(2));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(400));
		assertThat(resultT.getBeanList(), is(cloneList));
		assertThat(resultT.getIsMini(), is("0"));

		TradeSetBean resultS = result.get(1);
		assertThat(resultS.getCode(), is("0000"));
		assertThat(resultS.getVolume(), is(40));
		assertThat(resultS.getBeanList(), is(cloneList.subList(0, 1)));
		assertThat(resultS.getIsMini(), is("1"));

	}

	@Test
	public void testGetTradeSets_S単複数から単() {
		TradeDataBean bean1 = new TradeDataBean("0000", "", "", "entry1", "exit1", "1", "80", "", "");
		TradeDataBean bean2 = new TradeDataBean("0000", "", "", "entry2", "exit2", "1", "100", "", "");
		TradeDataBean bean3 = new TradeDataBean("0000", "", "", "entry3", "exit3", "1", "200", "", "");
		TradeDataBean bean4	 = new TradeDataBean("0000", "", "", "entry4", "exit4", "1", "20", "", "");;

		List<TradeDataBean> sameCodeList = new ArrayList<>();
		sameCodeList.add(bean1);
		sameCodeList.add(bean2);
		sameCodeList.add(bean3);
		sameCodeList.add(bean4);

		List<TradeSetBean> result = getTradeSets(sameCodeList);

		assertThat(result.size(), is(1));
		TradeSetBean resultT = result.get(0);
		assertThat(resultT.getCode(), is("0000"));
		assertThat(resultT.getVolume(), is(400));
		assertThat(resultT.getBeanList(), is(sameCodeList));
		assertThat(resultT.getIsMini(), is("0"));

	}


}
