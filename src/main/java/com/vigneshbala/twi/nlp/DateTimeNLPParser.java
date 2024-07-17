package com.vigneshbala.twi.nlp;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vigneshbala.twi.model.CountryRecord;
import com.vigneshbala.twi.model.ParserResult;
import com.vigneshbala.twi.util.ReferenceDataUtil;

/**
 * A Custom NLP Parser for date and time strings written in Java. Currently only
 * English is supported and only a select grammars are supported for date and
 * time conversion CLI 
 * 
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class DateTimeNLPParser {

	private static final int TOTAL_WEEKDAYS = 7;
	private static final int TOTAL_MONTHS = 12;
	private static final String CONTAIN_MORE_DATE_TIME = "String contain more date/time.. currently only one is supported..";

	private static List<WEEKDAYS> weekDaysInInput = new ArrayList<WEEKDAYS>();
	private static List<MONTHS> monthsInInput = new ArrayList<MONTHS>();
	private static Logger log = LoggerFactory.getLogger(DateTimeNLPParser.class);
	private static final List<DateTimeZone> availableTimeZones = DateTimeZone.getAvailableIDs().stream()
			.map(DateTimeZone::forID).collect(Collectors.toList());

	private static final String TIME_PATTERN = "([0-9]{1,2})(:[0-9]{1,2})?";
	private static CountryRecord countryRecord = null;
	private static DateTimeZone timeZone = null;

	private static final List<String> SPECIFIERS = Arrays.asList("standard", "std", "time", "timezone", "zone", "day",
			"light", "daylight", "savings", "rd", "st", "nd");

	private static Map<String, Integer> RELATIVE_DAYS = new HashMap<>();

	public DateTimeNLPParser() {
		populateRelativeDays();

	}

	private void populateRelativeDays() {
		RELATIVE_DAYS.put("today", 0);
		RELATIVE_DAYS.put("now", 0);
		RELATIVE_DAYS.put("tomorrow", 1);
		RELATIVE_DAYS.put("yesterday", -1);
		RELATIVE_DAYS.put("day after tomorrow", 2);
		RELATIVE_DAYS.put("day before yesterday", -2);
	}

	public static ParserResult parse(String input, Clock clock) {
		ParserResult result = new ParserResult();
		DateTime baseTime = new DateTime(clock.instant());
		result.setFromDateTime(baseTime);
		boolean day = false;
		boolean before = false;
		boolean yesterday = false;
		boolean tomorrow = false;
		boolean after = false;
		try {
			input = extractandCleanInput(input);
			List<String> tokens = Arrays.asList(StringUtils.splitByWholeSeparator(input, StringUtils.SPACE));
			for (String token : tokens) {
				if (WEEKDAYS.valueOf(token) != null) {
					weekDaysInInput.add(WEEKDAYS.valueOf(token));
				}
				if (MONTHS.valueOf(token) != null) {
					monthsInInput.add(MONTHS.valueOf(token));
				}
				if (RELATIVE_DAYS.containsKey(token)) {
					if (token.equals("day")) {
						day = true;
					} else if (token.equals("before")) {
						before = true;
					} else if (token.equals("after")) {
						after = true;
					} else if (token.equals("tomorrow")) {
						if (day && after) {

						} else {

						}
					} else if (token.equals("yesterday")) {
						if (day && before) {

						} else {

						}
					}

				}
			}
			if (weekDaysInInput.size() > 1 || monthsInInput.size() > 1
					|| (weekDaysInInput.size() == 1 && monthsInInput.size() == 1)) {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

			if (weekDaysInInput.size() > 0) {

				int deltaDays = getDeltaDays(baseTime, weekDaysInInput.get(0));
				DateTime newTime = baseTime.plusDays(deltaDays);
				result.put(weekDaysInInput.get(0).getKey() + " : ", newTime);

			} else if (monthsInInput.size() > 0) {
				int deltaMonths = getDeltaMonths(baseTime, monthsInInput.get(0));
				DateTime newTime = baseTime.plusMonths(deltaMonths);
				result.put(monthsInInput.get(0).getKey() + " : ", newTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Hawking Parser is not recognizing certain time zones, so extracting the time
	 * zones from the input string, so that the time zone conversions can be handled
	 * separately.
	 * 
	 * @param input input string
	 * @throws Exception Exceptions while parsing and processing the String
	 */
	public static String extractandCleanInput(String input) throws Exception {
		input = input.toLowerCase();

		timeZone = extractTimeZone(input);
		if (timeZone != null) {
			input = stripTimeZone(input);
		}

		countryRecord = extractCountry(input);
		if (countryRecord != null) {
			input = stripCountry(input);
		}
		input = stripTimeZoneSpecifiers(input);
		input = stripAccentsAndSpecialCharacters(input);
		log.debug("Cleaned Input string ==>" + input);
		return input;
	}

	/**
	 * Helper Method to extract time zone component from string. This method will
	 * extract time zone abbreviation (e.g. IST), Time Zone Id (e.g. Asia/Kolkata)
	 * 
	 * @param input Original string
	 * @return String with all time zone text stripped
	 * @throws Exception Exception thrown when string contains more than one time
	 *                   zone
	 */
	private static DateTimeZone extractTimeZone(String input) throws Exception {

		List<String> tokens = Arrays.asList(StringUtils.splitByWholeSeparator(input, StringUtils.SPACE));
		TreeMap<String, DateTimeZone> timeZones = new TreeMap<String, DateTimeZone>();
		for (DateTimeZone zone : availableTimeZones) {

			if (tokenMatchesTZCodeOrName(tokens, zone)) {
				timeZones.put(zone.getShortName(DateTimeUtils.currentTimeMillis()), zone);
			}
		}
		if (timeZones.size() > 1) {
			throw new Exception("String contain more time zone.. only one is supported..");
		}
		return timeZones.size() > 0 ? timeZones.firstEntry().getValue() : null;
	}

	/**
	 * Helper Method to extract Country component from string. This method will
	 * extract country name or 2 digit ISO country code or 3 digit ISO country Code
	 * 
	 * @param input Original string
	 * @return String with country text stripped
	 * @throws Exception thrown when string contains more than one country
	 */
	private static CountryRecord extractCountry(String input) throws Exception {

		List<String> tokens = tokenizeInput(input);
		TreeMap<String, CountryRecord> countries = new TreeMap<String, CountryRecord>();
		for (Entry<String, CountryRecord> entry : ReferenceDataUtil.getCountryMap().entrySet()) {
			if (tokenMatchesCountryCodeOrName(tokens, entry)) {
				countries.put(entry.getValue().getAlpha2Code(), entry.getValue());
			}
		}
		if (countries.size() > 1) {
			throw new Exception("String contain more Countries.. only one is supported..");
		}
		return countries.size() > 0 ? countries.firstEntry().getValue() : null;
	}

	private static List<String> tokenizeInput(String input) {
		List<String> tokens = Arrays.asList(StringUtils.splitByWholeSeparator(input, StringUtils.SPACE));

		for (int i = 0; i < tokens.size(); i++) {
			if (isTokenPartofTimeString(tokens, i)) {
				tokens.set(i, tokens.get(i) + "!");
			}
		}
		return tokens;
	}

	/**
	 * Helper Method to Strip any word that is related to timezone. Following words
	 * will be stripped:
	 * 
	 * @param input input Original string
	 * @return String with relevant text stripped
	 * 
	 */
	private static String stripTimeZoneSpecifiers(String input) {

		for (String specifier : SPECIFIERS) {
			input = RegExUtils.removePattern(input, " " + specifier);
		}
		input = StringUtils.trim(input);
		return input;
	}

	private static boolean isTokenPartofTimeString(List<String> tokens, int i) {
		return i > 0 && (tokens.get(i).equals("am") || tokens.get(i).equals("pm"))
				&& tokens.get(i - 1).matches(TIME_PATTERN);
	}

	private static boolean tokenMatchesCountryCodeOrName(List<String> tokens, Entry<String, CountryRecord> entry) {
		return tokens.contains(entry.getValue().getAlpha2Code()) || tokens.contains(entry.getValue().getAlpha3Code())
				|| tokens.contains(entry.getValue().getCountryName());
	}

	private static String stripTimeZone(String input) throws Exception {

		input = RegExUtils.removePattern(input, timeZone.getShortName(DateTimeUtils.currentTimeMillis()));
		input = RegExUtils.removePattern(input, timeZone.getName(DateTimeUtils.currentTimeMillis()));
		input = RegExUtils.removePattern(input, timeZone.getID());
		return input;
	}

	private static String stripCountry(String input) throws Exception {

		input = RegExUtils.removePattern(input, countryRecord.getAlpha2Code());
		input = RegExUtils.removePattern(input, countryRecord.getAlpha3Code());
		input = RegExUtils.removePattern(input, countryRecord.getCountryName());
		return input;
	}

	private static String stripAccentsAndSpecialCharacters(String input) throws Exception {

		input = StringUtils.stripAccents(input);
		input = RegExUtils.removePattern(input, "[^A-Za-z0-9\\s:]");
		return input;
	}

	private static boolean tokenMatchesTZCodeOrName(List<String> tokens, DateTimeZone zone) {
		return tokens.contains(zone.getShortName(DateTimeUtils.currentTimeMillis()))
				|| tokens.contains(zone.getName(DateTimeUtils.currentTimeMillis())) || tokens.contains(zone.getID());
	}

	public static void main(String[] args) {
		DateTime baseTime = new DateTime();
		System.out.println(baseTime);
		MONTHS months = MONTHS.AUG;
		int deltaMonths = getDeltaMonths(baseTime, months);
		DateTime newTime = baseTime.plusMonths(deltaMonths);
		System.out.println(newTime);
	}

	private static int getDeltaDays(DateTime baseTime, WEEKDAYS wkDay) {
		int deltaDays = wkDay.getValue() - baseTime.getDayOfWeek();
		deltaDays = deltaDays >= 0 ? deltaDays : (TOTAL_WEEKDAYS + deltaDays);
		return deltaDays;
	}

	private static int getDeltaMonths(DateTime baseTime, MONTHS month) {
		int deltaMonths = month.getValue() - baseTime.getMonthOfYear();
		deltaMonths = deltaMonths >= 0 ? deltaMonths : (TOTAL_MONTHS + deltaMonths);
		return deltaMonths;
	}

}
