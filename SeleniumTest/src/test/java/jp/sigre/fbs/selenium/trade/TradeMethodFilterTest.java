/**
 *
 */
package jp.sigre.fbs.selenium.trade;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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

		target.skipCode(list, iniBean);

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

		target.skipCode(list, iniBean);

		assertThat(list.size(), is(0));

	}
}
