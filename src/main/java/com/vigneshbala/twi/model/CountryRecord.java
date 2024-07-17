package com.vigneshbala.twi.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;

/**
 * (c) 2024 Vignesh Balasubramanian 
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class CountryRecord {
	String countryName;
	String alpha2Code;
	String alpha3Code;
	List<DateTimeZone> timeZones = new ArrayList<>();

	public void addTimeZone(DateTimeZone timeZone) {
		timeZones.add(timeZone);
	}

	public List<DateTimeZone> getTimeZones() {
		return timeZones;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName.toLowerCase();
	}

	public String getAlpha2Code() {
		return alpha2Code;
	}

	public void setAlpha2Code(String alpha2Code) {
		this.alpha2Code = alpha2Code.toLowerCase();
	}

	public String getAlpha3Code() {
		return alpha3Code;
	}

	public void setAlpha3Code(String alpha3Code) {
		this.alpha3Code = alpha3Code.toLowerCase();
	}

}
