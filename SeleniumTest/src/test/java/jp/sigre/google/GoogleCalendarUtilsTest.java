/**
 *
 */
package jp.sigre.google;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author sigre
 *
 */
public class GoogleCalendarUtilsTest extends GoogleCalendarUtils {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * {@link jp.sigre.google.GoogleCalendarUtils#getHolydayList(java.lang.String)} のためのテスト・メソッド。
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException
	 */
	@Test
	public void testGetHolydayList() throws IOException, ParseException {
		List<HolidayBean> result = getHolidayBeans("2018-01-01", 10);

		assertThat(result.size(), is(10));
		assertThat(result.get(0), is(new HolidayBean("2018-01-01", "元日")));

		for (HolidayBean bean :result) {
			assertThat(bean.getDate(), is(not("")));
			assertThat(bean.getSummary(), is(not("")));
		}

	}


	/**
	 * {@link jp.sigre.google.GoogleCalendarUtils#getHolydayList(java.lang.String)} のためのテスト・メソッド。
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException
	 */
	@Test(expected=ParseException.class)
	public void testGetHolydayList_ParseException() throws IOException, ParseException {
		List<HolidayBean> result = getHolidayBeans("2018-101-test", 10);


	}

}
