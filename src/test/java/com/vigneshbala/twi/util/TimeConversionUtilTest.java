package com.vigneshbala.twi.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class TimeConversionUtilTest {

	private static final String DD_MM_YYYY_HH_MM_SS_A = "dd-MM-yyyy hh:mm:ss a";
	private static final String DD_MM_YYYY = "dd-MM-yyyy";

	// Test Dates injected as current dates for repeatable tests
	ZonedDateTime JUL_17_2024 = null;

	ZonedDateTime Feb_01_2024 = null;

	@BeforeClass
	public void loadReferenceData() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			JUL_17_2024 = LocalDate.parse("17/07/2024 00:00:00", formatter).atStartOfDay(ZoneId.systemDefault());

			Feb_01_2024 = LocalDate.parse("01/02/2024 00:00:00", formatter).atStartOfDay(ZoneId.systemDefault());
			ReferenceDataUtil.loadCountryData();
		} catch (IOException | URISyntaxException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void testWeekDays() {
		try {

			// weekday of past
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Tuesday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"23-07-2024");

			// weekday of future
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Thursday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"18-07-2024");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	private void test_Relative_Dates() {
		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Today", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"17-07-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Tomorrow", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"18-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("yesterday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"16-07-2024");

			Assert.assertEquals(TimeConversionUtil.convertDateTime("day before yesterday", DD_MM_YYYY, JUL_17_2024,
					null, null, null), "15-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("day after tomorrow", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"19-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("last Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("this Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"22-07-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("coming Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"22-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("past Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("this past Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-07-2024");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	@Test
	private void testMonths() {
		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("August", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"17-08-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("January", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"17-01-2025");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("July", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"17-07-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("July 19th", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"19-07-2024");
//			Assert.assertEquals(
//					TimeConversionUtil.convertDateTime("July 19", DD_MM_YYYY, JUL_17_2024, null, null, null),
//					"19-07-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("19th July", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"19-07-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("August 15th", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-08-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("15th August", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-08-2024");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("September", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"17-09-2024");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	private void testTime() {
		try {
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 08:00:00 PM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("5:30 AM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 05:30:00 AM");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("5 Hours", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 05:00:00 AM");

			Assert.assertEquals(TimeConversionUtil.convertDateTime("21 Hours", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null,
					null, null), "17-07-2024 09:00:00 PM");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("23rd July 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, null), "23-07-2024 08:00:00 PM");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("23rd July 8 AM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, null), "23-07-2024 08:00:00 AM");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("23rd July 7:30 AM", DD_MM_YYYY_HH_MM_SS_A,
					JUL_17_2024, null, null, null), "23-07-2024 07:30:00 AM");

			Assert.assertEquals(TimeConversionUtil.convertDateTime("3rd July 3:30 PM", DD_MM_YYYY_HH_MM_SS_A,
					JUL_17_2024, null, null, null), "03-07-2024 03:30:00 PM");
			// weekday of future

			Assert.assertEquals(TimeConversionUtil.convertDateTime("Thursday 8 AM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, null), "18-07-2024 08:00:00 AM");

			Assert.assertEquals(TimeConversionUtil.convertDateTime("15th August 7:30 PM", DD_MM_YYYY_HH_MM_SS_A,
					JUL_17_2024, null, null, null), "15-08-2024 07:30:00 PM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	@Test
	private void testYear() {
		try {
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("2025", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"17-07-2025");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("August 15th 2025", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-08-2025");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("August 15th 2024", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-08-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("August 15th 2024 3:30 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"15-08-2024 03:30:00 PM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("August 15th 2026 3:30 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"15-08-2026 03:30:00 PM");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}

	@Test
	private void testTZShortCode() {

		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					new String[] { "SGT" }, null, null), "SGT : 23-07-2024 10:30:00 PM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	private void testTZId() {

		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					new String[] { "Asia/Singapore" }, null, null), "Asia/Singapore : 23-07-2024 10:30:00 PM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
							new String[] { "Asia/Singapore", "Europe/London" }, null, null),
					"Europe/London : 23-07-2024 03:30:00 PM,Asia/Singapore : 23-07-2024 10:30:00 PM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	private void testTZDaylightSavings() {

		try {
			// July in DST
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					new String[] { "Europe/London" }, null, null), "Europe/London : 23-07-2024 03:30:00 PM");
			// Feb Not in DST
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, Feb_01_2024,
					new String[] { "Europe/London" }, null, null), "Europe/London : 06-02-2024 02:30:00 PM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	private void testRelative() {
		try {
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("+2h", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 02:00:00 AM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("+2.5h", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 02:30:00 AM");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("+2hours", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 02:00:00 AM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("+2hour", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 02:00:00 AM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("+2hr", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 02:00:00 AM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("+2hrs", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"17-07-2024 02:00:00 AM");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("-10d", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024, null, null, null),
					"07-07-2024 12:00:00 AM");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	private void testOffsetDecimal() {

		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, new String[] { "+8" }), "+8 :23-07-2024 10:30:00 PM");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, new String[] { "+8.5" }), "+8.5 :23-07-2024 11:00:00 PM");

			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, new String[] { "-8" }), "-8 :23-07-2024 06:30:00 AM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
