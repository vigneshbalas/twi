package com.vigneshbala.twi.nlp;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vigneshbala.twi.model.CountryRecord;
import com.vigneshbala.twi.model.DateTimeComponent;
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

	private static final String INVALID_INPUT = "Invalid Input";
	private static final String NEXT = "next";
	private static final String PAST = "past";
	private static final String LAST = "last";
	private static final String TOMORROW = "tomorrow";
	private static final String YESTERDAY = "yesterday";
	private static Logger log = LoggerFactory.getLogger(DateTimeNLPParser.class);
	private static final String CONTAIN_MORE_DATE_TIME = "String contain more date/time.. currently only one is supported..";
	private static final String DOES_NOT_CONTAIN_ANY_DATES_OR_TIME = "String does not contain any dates or time..";

	private static final String MONTH_REGEX = "jan(?:uary)|feb(?:raury)|mar(?:ch)|apr(?:il)|may|jun(?:e)|jul(?:y)|aug(?:ust)|sep(?:tember)|oct(?:ober)|nov(?:ember)|dec(?:ember)";

	private static final String DATE_REGEX = "\\d{1,2}(st|rd|th|nd|\\s)?";

	private static final List<DateTimeZone> availableTimeZones = DateTimeZone.getAvailableIDs().stream()
			.map(DateTimeZone::forID).collect(Collectors.toList());

	private static final String TIME_PATTERN = "([0-9]{1,2})(:[0-9]{1,2})?";
	private static CountryRecord countryRecord = null;
	private static DateTimeZone timeZone = null;

	private static final List<String> SPECIFIERS = Arrays.asList("standard", "std", "time", "timezone", "zone", "day",
			"light", "daylight", "savings");
	private static final String YEAR_REGEX = "\\d{4}";

	private DateTimeComponent delta = null;
	private String input = null;

	public ParserResult parse(String input, Clock clock, String format) throws Exception {
		ParserResult result = new ParserResult(format);
		DateTime baseTime = new DateTime(clock.millis());
		result.setFromDateTime(baseTime);
		boolean past = false;
		boolean next = false;

		try {
			this.input = extractandCleanInput(input);
			past = inputHasLastorPast(input);
			next = inputHasNext(input);

			if (next && past) {
				throw new Exception(INVALID_INPUT);
			}

			this.delta = new DateTimeComponent(baseTime, past);

			parseYear();

			parseMonth();

			parseDate();
			
			parseRelativeDays();

			parseWeekDays();

			parseMonthsDelta();

			

			if (this.delta.noDateTimePresent()) {
				throw new Exception(DOES_NOT_CONTAIN_ANY_DATES_OR_TIME);
			}
//			if (this.delta.moreDateTimePresent()) {
//				throw new Exception(CONTAIN_MORE_DATE_TIME);
//			}

			result.putToDateTime("", this.delta.getDateTime());

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}

		return result;
	}

	private void parseYear() throws Exception {

		Pattern pattern = Pattern.compile(YEAR_REGEX);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			if (!match) {
				this.delta.setToYear(Integer.parseInt(matcher.group()));
				this.input = this.input.replaceAll(matcher.group(), "");
			} else {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

		}

	}

	private void parseMonth() throws Exception {
		Pattern pattern = Pattern.compile(MONTH_REGEX);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			if (!match) {
				this.delta.setToMonth(matcher.group());
				this.input = this.input.replaceAll(matcher.group(), "");
			} else {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

		}

	}

	private void parseDate() throws Exception {
		Pattern pattern = Pattern.compile(DATE_REGEX);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			if (!match) {
				this.delta.setToDate(Integer.parseInt(matcher.group().replaceAll("st", "").replaceAll("rd", "")
						.replaceAll("nd", "").replaceAll("th", "")));
				this.input = this.input.replaceAll(matcher.group(), "");
			} else {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

		}

	}

	private boolean parseRelativeHours(String input, ParserResult result, DateTime baseTime) throws Exception {
		boolean match = false;
		for (String key : DateTimeUnits.getInstance().getRelativeHoursMap().keySet()) {
			if (hasMatch(key)) {
				match = true;
				int deltaHours = DateTimeUnits.getInstance().getRelativeHour(key);
				DateTime newTime = baseTime.plusHours(deltaHours);
				result.putToDateTime(key + ":", newTime);

			}

		}
		return match;
	}

	private void parseRelativeDays() throws Exception {
		String matchedKey = null;
		for (String key : DateTimeUnits.getInstance().getRelativeDaysMap().keySet()) {
			if (hasMatch(key)) {
				if (key.equals(YESTERDAY)) {
					matchedKey = YESTERDAY;
				} else if (key.equals(TOMORROW)) {
					matchedKey = TOMORROW;
				} else {
					matchedKey = key;
				}
			}
		}
		if (matchedKey != null)
			this.delta.setRelativeDayDelta(matchedKey);

	}

	private void parseMonthsDelta() throws Exception {
		for (String key : DateTimeUnits.getInstance().getMonthsMap().keySet()) {
			if (hasMatch(key)) {
				this.delta.setMonthDelta(key);
			}

		}
	}

	private void parseWeekDays() throws Exception {
		for (String key : DateTimeUnits.getInstance().getWeekdayMap().keySet()) {
			if (hasMatch(key)) {
				this.delta.setDayDelta(key);
			}

		}
	}

	private boolean inputHasLastorPast(String input) {
		boolean past = false;
		if (hasMatch(LAST) || hasMatch(PAST)) {
			past = true;
		}
		return past;
	}

	private boolean inputHasNext(String input) {
		boolean next = false;
		if (hasMatch(NEXT)) {
			next = true;
		}
		return next;
	}

	/**
	 * Extracting the time zones from the input string, so that the time zone
	 * conversions can be handled separately.
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

	private boolean hasMatch(String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			match = true;
			this.input=this.input.replaceAll(matcher.group(), "");
		}
		return match;
	}
}
