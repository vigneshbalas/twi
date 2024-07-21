package com.vigneshbala.twi.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.vigneshbala.twi.model.CountryRecord;
/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class ReferenceDataUtil {

	private static final String COUNTRY_NAMES_JSON = "/referencedata/country-names.json";
	private static final String COUNTRY_CODES_JSON = "/referencedata/country-codes.json";
	private static final String COUNTRY_TIME_ZONES_JSON = "/referencedata/country-timezones.json";

	private static final List<String> TIME_ZONE_SPECIFIERS = Arrays.asList("standard", "std", "time", "timezone",
			"zone", "day", "light", "daylight", "savings");

	private static Map<String, CountryRecord> countryMap = new HashMap<String, CountryRecord>();

	public static void loadCountryData() throws FileNotFoundException, IOException, JsonException {
		loadCountryNames();
		loadCountryCodes();
		loadCountryTimeZones();
	}

	private static void loadCountryNames() throws FileNotFoundException, IOException, JsonException {

		try (FileReader reader = getReader(COUNTRY_NAMES_JSON)) {
			JsonObject countries = (JsonObject) Jsoner.deserialize(reader);
			for (String alpha2Code : countries.keySet()) {
				CountryRecord countryRecord = getOrCreate(alpha2Code);
				countryRecord.setCountryName((String) countries.get(alpha2Code));
				countryMap.put(alpha2Code, countryRecord);
			}

		}

	}

	private static CountryRecord getOrCreate(String alpha2Code) {
		return countryMap.containsKey(alpha2Code) ? countryMap.get(alpha2Code) : new CountryRecord();
	}

	private static void loadCountryCodes() throws FileNotFoundException, IOException, JsonException {

		try (FileReader reader = getReader(COUNTRY_CODES_JSON)) {
			JsonObject countries = (JsonObject) Jsoner.deserialize(reader);
			for (String alpha2Code : countries.keySet()) {
				CountryRecord countryRecord = getOrCreate(alpha2Code);
				countryRecord.setAlpha3Code((String) countries.get(alpha2Code));
				countryRecord.setAlpha2Code(alpha2Code);
				countryMap.put(alpha2Code, countryRecord);
			}

		}

	}

	private static void loadCountryTimeZones() throws FileNotFoundException, IOException, JsonException {

		try (FileReader reader = getReader(COUNTRY_TIME_ZONES_JSON)) {
			JsonArray records = (JsonArray) Jsoner.deserialize(reader);
			for (Object jsonRecord : records) {
				String alpha2Code = ((JsonObject) jsonRecord).get("IsoAlpha2").toString();
				CountryRecord countryRecord = getOrCreate(alpha2Code);
				JsonArray timezones = (JsonArray) ((JsonObject) jsonRecord).get("TimeZones");
				for (Object timezone : timezones) {
					countryRecord.addTimeZone(ZoneId.of(timezone.toString()));
				}
				countryMap.put(alpha2Code, countryRecord);
			}

		}

	}

	private static FileReader getReader(String filePath) throws FileNotFoundException {
		return new FileReader(ReferenceDataUtil.class.getResource(filePath).getFile());
	}

	public static Map<String, CountryRecord> getCountryMap() {
		return countryMap;
	}

	public static List<String> getTimeZoneSpecifiers() {
		return TIME_ZONE_SPECIFIERS;
	}

	public static void main(String args[]) throws FileNotFoundException, IOException, JsonException {
		loadCountryTimeZones();
	}
}
