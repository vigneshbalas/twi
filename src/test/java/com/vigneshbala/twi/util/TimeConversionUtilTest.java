package com.vigneshbala.twi.util;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.cliftonlabs.json_simple.JsonException;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class TimeConversionUtilTest {

	private static final String DD_MM_YYYY_HH_MM_SS_A = "dd-MM-yyyy hh:mm:ss a";
	private static final String DD_MM_YYYY = "dd-MM-yyyy";
	// Assume Current date is July 17,2024 - Wednesday
	Clock JUL_17_2024 = Clock.fixed(Instant.parse("2024-07-17T00:00:00.00Z"), ZoneId.systemDefault());

	Clock Feb_01_2024 = Clock.fixed(Instant.parse("2024-02-01T00:00:00.00Z"), ZoneId.systemDefault());

	@BeforeClass
	public void loadReferenceData() {
		try {
			ReferenceDataUtil.loadCountryData();
		} catch (IOException | JsonException e) {

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
					"08-07-2024");
//
//			Assert.assertEquals(
//					TimeConversionUtil.convertDateTime("this Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
//					"22-07-2024");
//			Assert.assertEquals(
//					TimeConversionUtil.convertDateTime("coming Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
//					"22-07-2024");
//
//			Assert.assertEquals(
//					TimeConversionUtil.convertDateTime("gone Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
//					"15-07-2024");
//
//			Assert.assertEquals(
//					TimeConversionUtil.convertDateTime("this past Monday", DD_MM_YYYY, JUL_17_2024, null, null, null),
//					"15-07-2024");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	// @Test
	private void testMonths() {
		try {
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Next Month", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"01-08-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("July 19th", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"19-07-2024");
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("July 19", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"19-07-2024");
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
					"01-09-2024");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	// @Test
	private void testTime() {
		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, null), "23-07-2024 08:00:00 PM");

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

	// @Test
	private void testTZShortCode() {

		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					new String[] { "SGT" }, null, null), "SGT : 23-07-2024 10:30:00 PM");

			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
							new String[] { "SGT", "GMT" }, null, null),
					"GMT : 23-07-2024 02:30:00 PM,SGT : 23-07-2024 10:30:00 PM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	// @Test
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

	// @Test
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

	// @Test
	private void testOffsetDecimal() {

		try {
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, new String[] { "+8" }), "+8 :23-07-2024 10:30:00 PM");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, new String[] { "+8.5" }), "+8.5 :23-07-2024 11:00:00 PM");

			Assert.assertEquals(TimeConversionUtil.convertDateTime("Tuesday 8 PM", DD_MM_YYYY_HH_MM_SS_A, JUL_17_2024,
					null, null, new String[] { "-8" }), "-8 :23-07-2024 05:30:00 PM");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	// @Test
	public void failed_scenarios() {
		try {
			// Today's Weekday - gives next week instead of today's date
			Assert.assertEquals(TimeConversionUtil.convertDateTime("Wednesday", DD_MM_YYYY,
					Clock.fixed(Instant.parse("2024-07-17T00:00:00.00Z"), ZoneId.systemDefault()), null, null, null),
					"19-07-2024");
			// "19th July" works but not 19 July
			Assert.assertEquals(TimeConversionUtil.convertDateTime("19 July", DD_MM_YYYY,
					Clock.fixed(Instant.parse("2024-07-17T00:00:00.00Z"), ZoneId.systemDefault()), null, null, null),
					"19-07-2024");
			// following didn't work
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("two days before", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-07-2024");
			// following didn't work
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("two days back", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-07-2024");
			// expected 15th Aug but got 01st Aug
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("Next Month 15th", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"15-08-2024");
			// got 01-08-2024
			Assert.assertEquals(TimeConversionUtil.convertDateTime("First Sunday of August", DD_MM_YYYY, JUL_17_2024,
					null, null, null), "04-08-2024");
			// got 01-08-2024
			Assert.assertEquals(
					TimeConversionUtil.convertDateTime("last day of August", DD_MM_YYYY, JUL_17_2024, null, null, null),
					"31-08-2024");
			// Time is not recognized
			Assert.assertEquals(TimeConversionUtil.convertDateTime("day after tomorrow 7:30 AM", DD_MM_YYYY_HH_MM_SS_A,
					JUL_17_2024, null, null, null), "19-07-2024 07:30:00 AM");
			Assert.assertEquals(TimeConversionUtil.convertDateTime("7:30 AM day after tomorrow", DD_MM_YYYY_HH_MM_SS_A,
					JUL_17_2024, null, null, null), "19-07-2024 07:30:00 AM");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}
