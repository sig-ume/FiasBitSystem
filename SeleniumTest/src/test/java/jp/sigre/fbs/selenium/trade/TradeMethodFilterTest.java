/**
 *
 */
package jp.sigre.fbs.selenium.trade;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.utils.FileUtils;

/**
 * @author sigre
 *
 */
public class TradeMethodFilterTest  {



	private final static FileUtils fileUtils = new FileUtils();
	private final static String basePath = System.getProperty("user.dir");

	private final static LogMessage log = new LogMessage(basePath);

	//TODO:以下のグローバル引数を削除

	private static IniBean iniBean = null;
	private static TradeMethodFilter target = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File iniFile = new File(fileUtils.getIniPath(basePath));

		log.writelnLog("iniファイル読み込み開始");

		//fbs.ini存在チェック
		if (!iniFile.exists()) {
			log.writelnLog("iniファイルが存在しません。");
		}

		//iniBeanの形式エラーチェック
		iniBean = fileUtils.iniToBean(iniFile);

		target = new TradeMethodFilter(iniBean);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	}

	/**
	 * {@link jp.sigre.fbs.selenium.trade.TradeMethodFilter#longFilter(java.util.List, jp.sigre.fbs.selenium.trade.IniBean)} のためのテスト・メソッド。
	 */
	@Test
	public void testLongFilter() {
	}

	/**
	 * {@link jp.sigre.fbs.selenium.trade.TradeMethodFilter#shortFilter(java.util.List, jp.sigre.fbs.selenium.trade.IniBean)} のためのテスト・メソッド。
	 */
	@Test
	public void testShortFilter() {

	}

	/**
	 * {@link jp.sigre.fbs.selenium.trade.TradeMethodFilter#skipCode(java.util.List, jp.sigre.fbs.selenium.trade.IniBean)} のためのテスト・メソッド。
	 */
	@Test
	public void testSkipCode_売買対象あり() {
		List<TradeDataBean> list = new ArrayList<>();

		TradeDataBean bean0 = new TradeDataBean();
		bean0.setCode("0000");
		list.add(bean0);

		TradeDataBean bean1 = new TradeDataBean();
		bean1.setCode("0001");
		list.add(bean1);

		TradeDataBean bean2 = new TradeDataBean();
		bean2.setCode("0002");
		list.add(bean2);

		TradeDataBean bean3 = new TradeDataBean();
		bean3.setCode("0003");
		list.add(bean3);

		TradeDataBean bean4 = new TradeDataBean();
		bean4.setCode("0004");
		list.add(bean4);

		TradeDataBean bean5 = new TradeDataBean();
		bean5.setCode("0005");
		list.add(bean5);

		TradeDataBean bean6 = new TradeDataBean();
		bean6.setCode("0006");
		list.add(bean6);

		TradeDataBean bean7 = new TradeDataBean();
		bean7.setCode("0007");
		list.add(bean7);

		TradeDataBean bean8 = new TradeDataBean();
		bean8.setCode("0008");
		list.add(bean8);

		TradeDataBean bean9 = new TradeDataBean();
		bean9.setCode("0009");
		list.add(bean9);

		target.skipCode(list);

		assertThat(list.size(), is(5));
		assertThat(list.get(0).getCode(), is("0000"));
		assertThat(list.get(1).getCode(), is("0001"));
		assertThat(list.get(2).getCode(), is("0003"));
		assertThat(list.get(3).getCode(), is("0005"));
		assertThat(list.get(4).getCode(), is("0009"));


	}


	/**
	 * {@link jp.sigre.fbs.selenium.trade.TradeMethodFilter#skipCode(java.util.List, jp.sigre.fbs.selenium.trade.IniBean)} のためのテスト・メソッド。
	 */
	@Test
	public void testSkipCode_全部スキップ() {
		List<TradeDataBean> list = new ArrayList<>();

		TradeDataBean bean2 = new TradeDataBean();
		bean2.setCode("0002");
		list.add(bean2);

		TradeDataBean bean4 = new TradeDataBean();
		bean4.setCode("0004");
		list.add(bean4);

		TradeDataBean bean6 = new TradeDataBean();
		bean6.setCode("0006");
		list.add(bean6);

		TradeDataBean bean7 = new TradeDataBean();
		bean7.setCode("0007");
		list.add(bean7);

		TradeDataBean bean8 = new TradeDataBean();
		bean8.setCode("0008");
		list.add(bean8);

		target.skipCode(list);

		assertThat(list.size(), is(0));

	}

	@Test
	public void testSetLongRatioedValue() {

		List<TradeDataBean> list = new ArrayList<>();

		TradeDataBean bean6 = new TradeDataBean();
		bean6.setCode("0006");
		bean6.setEntryMethod("ratioTest1");
		bean6.setExitMethod("ratioTest1");
		bean6.setRealEntryVolume("1");
		list.add(bean6);

		TradeDataBean bean7 = new TradeDataBean();
		bean7.setCode("0007");
		bean7.setEntryMethod("ratioTest2");
		bean7.setExitMethod("ratioTest2");
		bean7.setRealEntryVolume("1");
		list.add(bean7);

		TradeDataBean bean8 = new TradeDataBean();
		bean8.setCode("0008");
		bean8.setEntryMethod("ratioTest3");
		bean8.setExitMethod("ratioTest3");
		bean8.setRealEntryVolume("1");
		list.add(bean8);

		TradeDataBean bean9 = new TradeDataBean();
		bean9.setCode("0009");
		bean9.setEntryMethod("ratioTest4");
		bean9.setExitMethod("ratioTest4");
		bean9.setRealEntryVolume("1");
		list.add(bean9);

		TradeDataBean bean6x = new TradeDataBean();
		bean6x.setCode("0006");
		bean6x.setEntryMethod("ratioTest1");
		bean6x.setExitMethod("ratioTest1");
		bean6x.setRealEntryVolume("1");;

		TradeDataBean bean7x = new TradeDataBean();
		bean7x.setCode("0007");
		bean7x.setEntryMethod("ratioTest2");
		bean7x.setExitMethod("ratioTest2");
		bean7x.setRealEntryVolume("2.49");

		TradeDataBean bean8x = new TradeDataBean();
		bean8x.setCode("0008");
		bean8x.setEntryMethod("ratioTest3");
		bean8x.setExitMethod("ratioTest3");
		bean8x.setRealEntryVolume("1.5");

		TradeDataBean bean9x = new TradeDataBean();
		bean9x.setCode("0009");
		bean9x.setEntryMethod("ratioTest4");
		bean9x.setExitMethod("ratioTest4");
		bean9x.setRealEntryVolume("1");


		List<TradeDataBean> result = target.setLongRatioedValue(list);

		assertThat(result.get(0), is(bean6x));
		assertThat(result.get(1), is(bean7x));
		assertThat(result.get(2), is(bean8x));
		assertThat(result.get(3), is(bean9x));


	}


	@Test
	public void testSetVolumeLongToInt() {

		List<TradeDataBean> list = new ArrayList<>();

		TradeDataBean bean6 = new TradeDataBean();
		bean6.setCode("0006");
		bean6.setEntryMethod("ratioTest1");
		bean6.setExitMethod("ratioTest1");
		bean6.setRealEntryVolume("1");
		list.add(bean6);

		TradeDataBean bean7 = new TradeDataBean();
		bean7.setCode("0007");
		bean7.setEntryMethod("ratioTest2");
		bean7.setExitMethod("ratioTest2");
		bean7.setRealEntryVolume("1");
		list.add(bean7);

		TradeDataBean bean8 = new TradeDataBean();
		bean8.setCode("0008");
		bean8.setEntryMethod("ratioTest3");
		bean8.setExitMethod("ratioTest3");
		bean8.setRealEntryVolume("1");
		list.add(bean8);

		TradeDataBean bean9 = new TradeDataBean();
		bean9.setCode("0009");
		bean9.setEntryMethod("ratioTest4");
		bean9.setExitMethod("ratioTest4");
		bean9.setRealEntryVolume("1");
		list.add(bean9);

		TradeDataBean bean6x = new TradeDataBean();
		bean6x.setCode("0006");
		bean6x.setEntryMethod("ratioTest1");
		bean6x.setExitMethod("ratioTest1");
		bean6x.setRealEntryVolume("1");;

		TradeDataBean bean7x = new TradeDataBean();
		bean7x.setCode("0007");
		bean7x.setEntryMethod("ratioTest2");
		bean7x.setExitMethod("ratioTest2");
		bean7x.setRealEntryVolume("2");

		TradeDataBean bean8x = new TradeDataBean();
		bean8x.setCode("0008");
		bean8x.setEntryMethod("ratioTest3");
		bean8x.setExitMethod("ratioTest3");
		bean8x.setRealEntryVolume("2");

		TradeDataBean bean9x = new TradeDataBean();
		bean9x.setCode("0009");
		bean9x.setEntryMethod("ratioTest4");
		bean9x.setExitMethod("ratioTest4");
		bean9x.setRealEntryVolume("1");


		List<TradeDataBean> result = target.setLongRatioedValue(list);
		result = target.setVolumeLongToInt(result);

		assertThat(result.get(0), is(bean6x));
		assertThat(result.get(1), is(bean7x));
		assertThat(result.get(2), is(bean8x));
		assertThat(result.get(3), is(bean9x));


	}

}
