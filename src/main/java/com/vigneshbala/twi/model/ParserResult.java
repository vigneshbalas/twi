package com.vigneshbala.twi.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class ParserResult {
	List<ZoneId> timezones;
	String outputformat;
	List<CountryRecord> countries;
	List<String> offsets;
	ZonedDateTime toDateTime = null;

	boolean isTimezonePresent;
	boolean isDatePresent;
	boolean isTimePresent;

	public ParserResult(String outputformat) {
		this.outputformat = outputformat;
	}

	public boolean isTimezonePresent() {
		return isTimezonePresent;
	}

	public void setTimezonePresent(boolean isTimezonePresent) {
		this.isTimezonePresent = isTimezonePresent;
	}

	public boolean isDatePresent() {
		return isDatePresent;
	}

	public void setDatePresent(boolean isDatePresent) {
		this.isDatePresent = isDatePresent;
	}

	public boolean isTimePresent() {
		return isTimePresent;
	}

	public void setTimePresent(boolean isTimePresent) {
		this.isTimePresent = isTimePresent;
	}

	public List<ZoneId> getTimezones() {
		return timezones;
	}

	public List<CountryRecord> getCountries() {
		return countries;
	}

	public List<String> getOffsets() {
		return offsets;
	}

	public ZonedDateTime getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(ZonedDateTime toDateTime) {
		this.toDateTime = toDateTime;
	}

	public void setOffsets(List<String> offsets) {
		this.offsets = offsets;
	}

	public String getPrettyPrintedResult() {
		StringBuilder sb = new StringBuilder();
		sb.append(DateTimeFormatter.ofPattern(outputformat).format(toDateTime));
		return sb.toString();

	}

}
