package jp.sigre.LinkDownloader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DownloadControllerTest {

	DownloadController target = new DownloadController();
	String id = "trial";
	String pass = "PW@20170129";
	static String tomorrowDate = "";

	String strStockHead = "\"SC\",\"名称\",\"市場\",\"業種\",\"日時\",\"株価\",\"前日比\",\"前日比（％）\",\"前日終値\",\"始値\",\"高値\",\"安値\",\"出来高\",\"売買代金（千円）\",\"時価総額（百万円）\",\"値幅下限\",\"値幅上限\"";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//現在日時を取得する
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);

		//フォーマットパターンを指定して表示する
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		tomorrowDate = sdf.format(c.getTime());
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetData_正常系() throws IOException, WebAccessException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";

		List<String> result = testGetData(strUrl, id, pass);

		for (String str : result) {
			System.out.println(str);
		}

		System.out.println("size; " + result.size());

		assertThat(result.size(), is(3677));
	}

	@Test
	public void testGetData_正常系100連() throws IOException, WebAccessException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";
		List<String> result = new ArrayList<>();
		for(int i=0; i<100; i++) {
			result = testGetData(strUrl, id, pass);
			assertThat(result.size(), is(3677));
		}

		assertThat(result.size(), is(3677));
	}


	@Test(expected = UnknownHostException.class)
	public void testGetData_存在しないURL() throws IOException, WebAccessException {
		String strUrl = "https://sonzaishinai.com";

		testGetData(strUrl, id, pass);

	}

	@Test
	public void testGetData_存在しないファイル() throws IOException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_" + tomorrowDate + ".csv";

		try {
			testGetData(strUrl, id, pass);
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(404));
		}

	}

	@Test
	public void testGetData_認証失敗() throws IOException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";

		try {
			testGetData(strUrl, "test", pass);
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(401));
		}

	}

	@Test(expected = MalformedURLException.class)
	public void testGetData_URL設定なし() throws IOException {
		String strUrl = "";

		try {
			testGetData(strUrl, id, pass);
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(401));
		}

	}

	@Test(expected = MalformedURLException.class)
	public void testGetData_URLがNull() throws IOException, WebAccessException {
		String strUrl = null;

		testGetData(strUrl, id, pass);

	}

	@Test
	public void testGetData_IDが空白() throws IOException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";

		try {
			testGetData(strUrl, "", pass);
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(401));
		}

	}


	@Test
	public void testGetData_Passが空白() throws IOException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";

		try {
			testGetData(strUrl, id, "");
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(401));
		}

	}
	@Test
	public void testGetData_IDがNull() throws IOException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";

		try {
			testGetData(strUrl, null, pass);
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(401));
		}

	}


	@Test
	public void testGetData_PassがNull() throws IOException {
		String strUrl = "https://hesonogoma.com/stocks/download/csv/japan-all-stock-prices/daily/japan-all-stock-prices_20171117.csv";

		try {
			testGetData(strUrl, id, null);
		} catch (WebAccessException e) {
			assertThat(e.getCode(), is(401));
		}

	}

	private List<String> testGetData(String strUrl, String id, String pass) throws IOException, WebAccessException {
		List<String> result = target.getData(strUrl, id, pass);

		assertThat(result.get(0), is(strStockHead));
		//assertThat(result.size(), is(greaterThanOrEqualTo(2)));

		return result;
	}
}
