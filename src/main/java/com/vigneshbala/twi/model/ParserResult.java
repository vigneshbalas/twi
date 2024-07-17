package com.vigneshbala.twi.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class ParserResult {
	List<DateTimeZone> timezones;
	List<CountryRecord> countries;
	List<String> offsets;
	Map<String, DateTime> toDateTime = new HashMap<String, DateTime>();
	DateTime fromDateTime;
	boolean isTimezonePresent;
	boolean isDatePresent;
	boolean isTimePresent;

	public Map<String, DateTime> getToDateTime() {
		return toDateTime;
	}

	public DateTime put(String key, DateTime value) {
		return toDateTime.put(key, value);
	}

	public void setToDateTime(Map<String, DateTime> toDateTime) {
		this.toDateTime = toDateTime;
	}

	public DateTime getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(DateTime fromDateTime) {
		this.fromDateTime = fromDateTime;
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

	public void setOffsets(List<String> offsets) {
		this.offsets = offsets;
	}

}
