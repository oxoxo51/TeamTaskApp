package util;

import constant.Constant;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created on 16/09/14.
 */
public class DateUtilTest {

	/* テスト用日付：2016-01-01 */
	private final Date testDate = new Date(116,0,1);

	/* テスト用日付文字列：20160101 */
	private final String testDateStr = "20160101";

	/* テスト用日付文字列：2016/01/01 */
	private final String testDateStrWithSla = "2016/01/01";

	/**
	 * [01-01]getDate:正しい日付指定.
	 * @throws Exception
	 */
	@Test
	public void testGetDateWithValidDate() throws Exception {
		assertEquals(DateUtil.getDate(testDateStr, Constant.DATE_FORMAT_yMd), testDate);
	}

	/**
	 * [02-01]getDateStr:正しい日付文字列指定.
	 * @throws Exception
	 */
	@Test
	public void testGetDateStrWithValidDate() throws Exception {
		assertEquals(DateUtil.getDateStr(testDate, Constant.DATE_FORMAT_yMd), testDateStr);
	}

	/**
	 * [02-02]getDateStr:NULL指定.
	 * @throws Exception
	 */
	@Test
	public void testGetDateStrWithNull() throws Exception {
		assertEquals(DateUtil.getDateStr(null, Constant.DATE_FORMAT_yMd), null);
	}

	/**
	 * [03-01]getDateStrFromStr:yyyyMMdd→yyyy/MM/dd
	 * @throws Exception
	 */
	@Test
	public void testGetDateStrFromStrYmdToYmdSla() throws Exception {
		assertEquals(
				DateUtil.getDateStrFromStr(
						testDateStr,
						Constant.DATE_FORMAT_yMd,
						Constant.DATE_FORMAT_yMd_SLASH)
				, testDateStrWithSla);
	}

	/**
	 * [04-01]getThatDateStr:N日後
	 * @throws Exception
	 */
	@Test
	public void testGetThatDateStrPlus() throws Exception {
		assertEquals(DateUtil.getThatDateStr(testDateStr, Constant.DATE_FORMAT_yMd, 2), "20160103");
	}

	/**
	 * [04-02]getThatDateStr:N日前
	 * @throws Exception
	 */
	@Test
	public void testGetThatDateStrMinus() throws Exception {
		assertEquals(DateUtil.getThatDateStr(testDateStr, Constant.DATE_FORMAT_yMd, -2), "20151230");
	}

	/**
	 * [05-01]getDateWithoutTime
	 * @throws Exception
	 */
	@Test
	public void testGetDateWithoutTime() throws Exception {
		// 1970-01-01 9:00:01 の日付部分
		assertEquals(DateUtil.getDateWithoutTime(new Date(1000L)), new Date(70,0,1));
	}

	/**
	 * [06-01]getThatDate:N日後
	 * @throws Exception
	 */
	@Test
	public void testGetThatDatePlus() throws Exception {
		assertEquals(DateUtil.getThatDate(testDate, 5), new Date(116,0,6));
	}

	/**
	 * [06-02]getThatDate:N日前
	 * @throws Exception
	 */
	@Test
	public void testGetThatDateMinus() throws Exception {
		assertEquals(DateUtil.getThatDate(testDate, -5), new Date(115,11,27));
	}

}
