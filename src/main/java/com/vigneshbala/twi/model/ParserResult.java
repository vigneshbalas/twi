package com.vigneshbala.twi.model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class ParserResult {
	List<DateTimeZone> timezones;
	String outputformat;
	List<CountryRecord> countries;
	List<String> offsets;
	DateTime toDateTime = null;
	
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

	public List<DateTimeZone> getTimezones() {
		return timezones;
	}

	public List<CountryRecord> getCountries() {
		return countries;
	}

	public List<String> getOffsets() {
		return offsets;
	}

	public DateTime getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(DateTime toDateTime) {
		this.toDateTime = toDateTime;
	}

	public void setOffsets(List<String> offsets) {
		this.offsets = offsets;
	}

	public String getPrettyPrintedResult() {
		StringBuilder sb = new StringBuilder();
		sb.append(toDateTime.toString(outputformat));
		return sb.toString();

	}

}
