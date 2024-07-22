package com.vigneshbala.twi.nlp;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

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

	private final static Logger LOGGER = Logger.getLogger(DateTimeNLPParser.class.getName());

	// Literal constants
	private static final String HRS = "hrs";
	private static final String HR = "hr";
	private static final String H = "h";
	private static final String HOURS = "hours";
	private static final String HOUR = "hour";
	private static final String D = "d";
	private static final String DAY = "day";
	private static final String DAYS = "days";
	private static final String M = "m";
	private static final String MONTH = "month";
	private static final String MONTHS = "months";
	private static final String MINUS = "-";
	private static final String PLUS = "+";
	private static final String FIELD = "field";
	private static final String DECIMAL = "decimal";
	private static final String NUMBER = "number";
	private static final String RELATION = "relation";
	private static final String INVALID_INPUT = "Invalid Input";
	private static final String NEXT = "next";
	private static final String PAST = "past";
	private static final String LAST = "last";
	private static final String TOMORROW = "tomorrow";
	private static final String YESTERDAY = "yesterday";
	private static final List<String> SPECIFIERS = Arrays.asList("standard", "std", "time", "timezone", "zone", "day",
			"light", "daylight", "savings");

	// Regular Expressions
	private static final String MONTH_REGEX = "jan(?:uary)?|feb(?:raury)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?";
	private static final String DATE_REGEX = "\\d{1,2}(st|rd|th|nd|\\s)?";
	private static final String HOUR_MIN_REGEX = "(?<hour>\\d{1,2})\\s?(?<minute>:?\\d{1,2})?\\s?(?<suffix>hours|hrs|am|pm)?\\b";
	private static final String RELATIVE_REGEX = "(?<relation>\\+|\\-)(?<number>\\d{1,2})(?<decimal>\\.\\d{1,2})?(?<field>days|day|d|months|month|m|hrs|h|hours|hr|min|m|mins)?";
	private static final String TIME_REGEX = "([0-9]{1,2})(:[0-9]{1,2})?";
	private static final String YEAR_REGEX = "\\d{4}";

	// Time Zones
	private static final List<ZoneId> availableTimeZones = ZoneId.getAvailableZoneIds().stream().map(ZoneId::of)
			.collect(Collectors.toList());

	// Exception messages
	private static final String CONTAIN_MORE_DATE_TIME = "String contain more date/time.. currently only one is supported..";
	private static final String DOES_NOT_CONTAIN_ANY_DATES_OR_TIME = "String does not contain any dates or time..";

	private static CountryRecord countryRecord = null;
	private static ZoneId timeZone = null;

	private DateTimeComponent dtmComponent = null;
	private String input = null;

	/**
	 * Natural language Date & Time Parser
	 * 
	 * @param input  input string containing date and time
	 * @param clock  Base Date Time.
	 * @param format Output format
	 * @return ParserResult object
	 * @throws Exception will be thrown on error conditions
	 */
	public ParserResult parse(String input, ZonedDateTime dateTime, String format) throws Exception {
		ParserResult result = new ParserResult(format);
		ZonedDateTime baseTime = null;
		if (dateTime == null) {
			baseTime = ZonedDateTime.now();
		} else {
			baseTime = dateTime;
		}
		boolean past = false;
		boolean next = false;

		try {
			this.input = extractandCleanInput(input);
			past = inputHasLastorPast(input);
			next = inputHasNext(input);

			if (next && past) {
				throw new Exception(INVALID_INPUT);
			}

			this.dtmComponent = new DateTimeComponent(baseTime, past);

			parseYear();
			
			parseRelative();

			parseHourMinuteSeconds();

			parseMonth();

			parseDate();

			parseRelativeDays();

			parseWeekDays();

			parseMonthsDelta();

			if (this.dtmComponent.noDateTimePresent()) {
				throw new Exception(DOES_NOT_CONTAIN_ANY_DATES_OR_TIME);
			}
			if (this.dtmComponent.moreDateTimePresent()) {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

			result.setToDateTime(this.dtmComponent.getDateTime());

		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw e;
		}

		return result;
	}

	private void parseRelative() throws Exception {
		Pattern pattern = Pattern.compile(RELATIVE_REGEX);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			if (!match) {
				String relation = matcher.group(RELATION);
				String number = matcher.group(NUMBER);
				String decimal = matcher.group(DECIMAL);
				String field = matcher.group(FIELD);
				int muliplier = 0;

				if (relation.equals(PLUS)) {
					muliplier = 1;
				} else if (relation.equals(MINUS)) {
					muliplier = -1;
				}

				if (decimalandNotHour(decimal, field)) {

					throw new Exception(INVALID_INPUT);
				} else if (isHour(field)) {
					int minutes = 0;
					int hour = 0;
					if (decimal != null) {
						minutes = (int) (60 * Double.parseDouble(decimal.trim()));
						this.dtmComponent.setMinuteDelta(minutes * muliplier);
					}
					if (number != null) {
						hour = Integer.parseInt(number.trim());
						this.dtmComponent.setHourDelta(hour * muliplier);
					}

				} else if (isDay(field)) {
					if (number != null) {
						this.dtmComponent.setDayDelta(Integer.parseInt(number.trim()) * muliplier);
					}
				} else if (isMonth(field)) {
					if (number != null) {
						this.dtmComponent.setMonthDelta(Integer.parseInt(number.trim()) * muliplier);
					}
				}

			}

			this.input = this.input.replaceAll("\\" + matcher.group(), "");
		}

	}

	private boolean isMonth(String field) {
		return (field.equals(MONTHS) || field.equals(MONTH) || field.equals(M));
	}

	private boolean isDay(String field) {
		return (field.equals(DAYS) || field.equals(DAY) || field.equals(D));
	}

	private boolean decimalandNotHour(String decimal, String field) {
		return decimal != null && !(field.equals(HOUR) || field.equals(HOURS) || field.equals(H) || field.equals(HR)
				|| field.equals(HRS));
	}

	private boolean isHour(String field) {
		return (field.equals(HOUR) || field.equals(HOURS) || field.equals(H) || field.equals(HR) || field.equals(HRS));
	}

	private void parseHourMinuteSeconds() throws Exception {
		Pattern pattern = Pattern.compile(HOUR_MIN_REGEX);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			if (!match) {
				String hourString = matcher.group(HOUR);
				String minString = matcher.group("minute");
				if (hourString != null) {
					int hour = Integer.parseInt(matcher.group(HOUR).trim());
					if (matcher.group("suffix") != null && matcher.group("suffix").trim().equals("pm")) {
						hour += 12;
					}
					this.dtmComponent.setToHour(hour);
				}

				if (minString != null) {
					this.dtmComponent.setToMin(Integer.parseInt(matcher.group("minute").replaceAll(":", "").trim()));
				}

				this.input = this.input.replaceAll(matcher.group(), "");
			} else {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

		}

	}

	private void parseYear() throws Exception {

		Pattern pattern = Pattern.compile(YEAR_REGEX);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			if (!match) {
				this.dtmComponent.setToYear(Integer.parseInt(matcher.group()));
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
				this.dtmComponent.setToMonth(matcher.group());
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
				this.dtmComponent.setToDate(Integer.parseInt(matcher.group().replaceAll("st", "").replaceAll("rd", "")
						.replaceAll("nd", "").replaceAll("th", "")));
				this.input = this.input.replaceAll(matcher.group(), "");
			} else {
				throw new Exception(CONTAIN_MORE_DATE_TIME);
			}

		}

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
			this.dtmComponent.setRelativeDayDelta(matchedKey);

	}

	private void parseMonthsDelta() throws Exception {
		for (String key : DateTimeUnits.getInstance().getMonthsMap().keySet()) {
			if (hasMatch(key)) {
				this.dtmComponent.setMonthDelta(key);
			}

		}
	}

	private void parseWeekDays() throws Exception {
		for (String key : DateTimeUnits.getInstance().getWeekdayMap().keySet()) {
			if (hasMatch(key)) {
				this.dtmComponent.setDayDelta(key);
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
	private static String extractandCleanInput(String input) throws Exception {
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
		LOGGER.fine("Cleaned Input string ==>" + input);
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
	private static ZoneId extractTimeZone(String input) throws Exception {

		List<String> tokens = Arrays.asList(StringUtils.splitByWholeSeparator(input, StringUtils.SPACE));
		TreeMap<String, ZoneId> timeZones = new TreeMap<String, ZoneId>();
		for (ZoneId zone : availableTimeZones) {

			if (tokenMatchesTZCodeOrName(tokens, zone)) {
				timeZones.put(zone.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH), zone);
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
				&& tokens.get(i - 1).matches(TIME_REGEX);
	}

	private static boolean tokenMatchesCountryCodeOrName(List<String> tokens, Entry<String, CountryRecord> entry) {
		return tokens.contains(entry.getValue().getAlpha2Code()) || tokens.contains(entry.getValue().getAlpha3Code())
				|| tokens.contains(entry.getValue().getCountryName());
	}

	private static String stripTimeZone(String input) throws Exception {

		input = RegExUtils.removePattern(input, timeZone.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
		input = RegExUtils.removePattern(input, timeZone.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
		input = RegExUtils.removePattern(input, timeZone.getId());
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
		input = RegExUtils.removePattern(input, "[^A-Za-z0-9\\s\\+\\-\\.:]");
		return input;
	}

	private static boolean tokenMatchesTZCodeOrName(List<String> tokens, ZoneId zone) {
		return tokens.contains(zone.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
				|| tokens.contains(zone.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
				|| tokens.contains(zone.getId());
	}

	private boolean hasMatch(String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(this.input);
		boolean match = false;
		while (matcher.find()) {
			match = true;
			this.input = this.input.replaceAll(matcher.group(), "");
		}
		return match;
	}
}
