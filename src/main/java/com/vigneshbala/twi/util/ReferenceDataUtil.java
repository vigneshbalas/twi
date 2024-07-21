package com.vigneshbala.twi.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vigneshbala.twi.model.CountryRecord;

/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public class ReferenceDataUtil {

	private static Path COUNTRY_NAMES_JSON = null;
	private static Path COUNTRY_CODES_JSON = null;
	private static Path COUNTRY_TIME_ZONES_JSON = null;

	private static final List<String> TIME_ZONE_SPECIFIERS = Arrays.asList("standard", "std", "time", "timezone",
			"zone", "day", "light", "daylight", "savings");

	private static Map<String, CountryRecord> countryMap = new HashMap<String, CountryRecord>();

	public static void loadCountryData() throws FileNotFoundException, IOException, URISyntaxException {
		URI uri = ReferenceDataUtil.class.getResource("/referencedata").toURI();
		String dirPath = Paths.get(uri).toString();
		COUNTRY_NAMES_JSON = Paths.get(dirPath, "country-names.json");
		COUNTRY_CODES_JSON = Paths.get(dirPath, "country-codes.json");
		COUNTRY_TIME_ZONES_JSON = Paths.get(dirPath, "country-timezones.json");
		loadCountryNames();
		loadCountryCodes();
		loadCountryTimeZones();
	}

	private static void loadCountryNames() throws FileNotFoundException, IOException {

		JSONObject countries = new JSONObject(readFileAsString(COUNTRY_NAMES_JSON));
		for (String alpha2Code : countries.keySet()) {
			CountryRecord countryRecord = getOrCreate(alpha2Code);
			countryRecord.setCountryName((String) countries.get(alpha2Code));
			countryMap.put(alpha2Code, countryRecord);

		}

	}

	private static CountryRecord getOrCreate(String alpha2Code) {
		return countryMap.containsKey(alpha2Code) ? countryMap.get(alpha2Code) : new CountryRecord();
	}

	private static void loadCountryCodes() throws FileNotFoundException, IOException {

		JSONObject countries = new JSONObject(readFileAsString(COUNTRY_CODES_JSON));
		for (String alpha2Code : countries.keySet()) {
			CountryRecord countryRecord = getOrCreate(alpha2Code);
			countryRecord.setAlpha3Code((String) countries.get(alpha2Code));
			countryRecord.setAlpha2Code(alpha2Code);
			countryMap.put(alpha2Code, countryRecord);

		}

	}

	private static void loadCountryTimeZones() throws FileNotFoundException, IOException {

		JSONArray records = new JSONArray(readFileAsString(COUNTRY_TIME_ZONES_JSON));
		for (Object jsonRecord : records) {
			String alpha2Code = ((JSONObject) jsonRecord).get("IsoAlpha2").toString();
			CountryRecord countryRecord = getOrCreate(alpha2Code);
			JSONArray timezones = (JSONArray) ((JSONObject) jsonRecord).get("TimeZones");
			for (Object timezone : timezones) {
				countryRecord.addTimeZone(ZoneId.of(timezone.toString()));
			}
			countryMap.put(alpha2Code, countryRecord);
		}

	}

	public static String readFileAsString(Path filePath) throws IOException {
		return new String(Files.readAllBytes(filePath), Charset.forName(StandardCharsets.UTF_8.name()));
	}

	public static Map<String, CountryRecord> getCountryMap() {
		return countryMap;
	}

	public static List<String> getTimeZoneSpecifiers() {
		return TIME_ZONE_SPECIFIERS;
	}
}
