package com.vigneshbala.twi.util;

import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vigneshbala.twi.model.CountryRecord;
import com.vigneshbala.twi.model.ParserResult;
import com.vigneshbala.twi.nlp.DateTimeNLPParser;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class TimeConversionUtil {
	private static final String INVALID_OFFSET_FORMAT = "Invalid Offset Format.. supported formats: +8, +8.5,+08:00,+08:30,-8, -8.5 -08:00,-08:30";
	private static final String DOT = ".";
	private static final String ESCAPED_DOT = "\\.";
	private static final String COLON = ":";

	private static Logger log = LoggerFactory.getLogger(TimeConversionUtil.class);

	/**
	 * Convert the date time based on Natural Language input
	 * 
	 * @param input  Date time in natural language "+5 days" "Monday" "Monday 5 PM
	 *               SGT"
	 * @param format date or time format
	 * @return converted date time in the format specified
	 * @throws Exception
	 */
	public static String convertDateTime(String input, String format, Clock clock, String[] timeZones,
			String[] countries, String[] offsets) throws Exception {
		StringBuilder result = new StringBuilder();
		DateTimeNLPParser parser = new DateTimeNLPParser();
		ParserResult output = parser.parse(input, clock, format);
		if (timeZones != null) {
			result.append(StringUtils.join(convertTimeZones(format, timeZones, output), ","));
		}

		if (offsets != null) {
			result.append(StringUtils.join(convertoffsets(format, offsets, output), ","));

		}
		if (countries != null) {
			result.append(convertCountries(format, countries, output));

		}

		return output.getPrettyPrintedResult();
	}

	private static String convertCountries(String format, String[] countries, ParserResult parserResult) {
		StringBuilder result = new StringBuilder();
		for (String country : countries) {
			for (Entry<String, CountryRecord> entry : ReferenceDataUtil.getCountryMap().entrySet()) {
				if (matchesCountryCodeOrName(country, entry.getValue())) {
					result.append(entry.getValue().getAlpha2Code());
					result.append(COLON);
					result.append("\n");
					for (DateTimeZone zone : entry.getValue().getTimeZones()) {
						result.append(zone);
						result.append(COLON);
						result.append(parserResult.getToDateTime().get(zone).toDateTime(zone).toString(format));
						result.append("\n");
					}

				}
			}
		}
		return result.toString();
	}

	private static Set<String> convertoffsets(String format, String[] offsets, ParserResult parserResult)
			throws Exception {
		Set<String> result = new HashSet<>();
		for (String offset : offsets) {
			StringBuilder sb = new StringBuilder();
			sb.append(offset);
			sb.append(" ");
			sb.append(COLON);
			sb.append(parserResult.getToDateTime().get(offset).toDateTime(DateTimeZone.UTC)
					.toDateTime(getTimeZoneForOffset(offset)).toString(format));
			result.add(sb.toString());
		}
		return result;
	}

	private static DateTimeZone getTimeZoneForOffset(String offset) throws Exception {
		log.debug("Processing Offset: " + offset);
		int minutes = 0;
		int hour = 0;
		int offsetPlusOrMinus = extractOffsetPlusorMinus(offset);
		offset = offset.substring(1);
		boolean isMinuteAvailable = offset.contains(COLON) || offset.contains(DOT);
		if (isMinuteAvailable) {
			String delimiter = offset.contains(COLON) ? COLON : ESCAPED_DOT;
			String[] tokens = offset.split(delimiter);
			if (tokens.length > 2) {
				throw new Exception(INVALID_OFFSET_FORMAT);
			} else {
				// convert hour string to + or minus integer
				hour = Integer.parseInt(tokens[0]) * offsetPlusOrMinus;
				if (tokens.length > 1) {
					// 8.5 --> 30 minutes, 08:30--> 30 minutes, so using the terminal operator
					minutes = (int) (delimiter.equals(COLON) ? Integer.parseInt(tokens[1])
							: 60 * (Double.parseDouble(tokens[1]) / 10));
				}
			}

		} else {
			hour = Integer.parseInt(offset) * offsetPlusOrMinus;
		}
		DateTimeZone dateTimeZone = DateTimeZone.forOffsetHoursMinutes(hour, minutes);
		log.debug("Time Zone for offset:" + dateTimeZone.toString());
		return dateTimeZone;
	}

	private static int extractOffsetPlusorMinus(String offset) throws Exception {
		if (offset.startsWith("+")) {
			return 1;

		} else if (offset.startsWith("-")) {
			return -1;

		} else {
			throw new Exception(INVALID_OFFSET_FORMAT);
		}

	}

	private static Set<String> convertTimeZones(String format, String[] timeZones, ParserResult parserResult) {
		Set<String> result = new HashSet<>();
		List<DateTimeZone> availableTimeZones = DateTimeZone.getAvailableIDs().stream().map(DateTimeZone::forID)
				.collect(Collectors.toList());
		for (String timeZone : timeZones) {
			for (DateTimeZone zone : availableTimeZones) {
				if (matchesTZCodeOrName(timeZone, zone)) {
					StringBuilder sb = new StringBuilder();
					log.debug("Time Zone Processing::" + timeZone);
					sb.append(timeZone);
					sb.append(" ");
					sb.append(COLON);
					sb.append(" ");
					sb.append(parserResult.getToDateTime().get(timeZone).toDateTime(zone).toString(format));
					result.add(sb.toString());
				}
			}
		}
		return result;
	}

	private static boolean matchesTZCodeOrName(String tzString, DateTimeZone zone) {
		return tzString.contains(zone.getShortName(DateTimeUtils.currentTimeMillis()))
				|| tzString.contains(zone.getName(DateTimeUtils.currentTimeMillis()))
				|| tzString.contains(zone.getID());
	}

	private static boolean matchesCountryCodeOrName(String countryString, CountryRecord country) {
		return countryString.contains(country.getAlpha2Code()) || countryString.contains(country.getAlpha3Code())
				|| countryString.contains(country.getCountryName());
	}

}
