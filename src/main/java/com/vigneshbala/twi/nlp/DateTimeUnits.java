package com.vigneshbala.twi.nlp;

import java.util.HashMap;
import java.util.Map;

public class DateTimeUnits {

	private Map<String, Integer> weekdayMap = new HashMap<>();
	private Map<String, Integer> monthsMap = new HashMap<>();
	private Map<String, Integer> relativeDaysMap = new HashMap<>();
	private Map<String, Integer> relativeHoursMap = new HashMap<>();

	private static DateTimeUnits instance = null;

	public static DateTimeUnits getInstance() {
		if (instance == null) {
			instance = new DateTimeUnits();
		}
		return instance;
	}

	private DateTimeUnits() {
		weekdayMap.put("sun", 0);
		weekdayMap.put("mon", 1);
		weekdayMap.put("tue", 2);
		weekdayMap.put("wed", 3);
		weekdayMap.put("thu", 4);
		weekdayMap.put("fri", 5);
		weekdayMap.put("sat", 6);

		
		monthsMap.put("jan", 1);
		monthsMap.put("feb", 2);
		monthsMap.put("mar", 3);
		monthsMap.put("apr", 4);
		monthsMap.put("may", 5);
		monthsMap.put("jun", 6);
		monthsMap.put("jul", 7);
		monthsMap.put("aug", 8);
		monthsMap.put("sep", 9);
		monthsMap.put("oct", 10);
		monthsMap.put("nov", 11);
		monthsMap.put("dec", 12);
		monthsMap.put("past month", -1);
		monthsMap.put("last month", -1);
		monthsMap.put("next month", 1);

		relativeDaysMap.put("today", 0);
		relativeDaysMap.put("yesterday", -1);
		relativeDaysMap.put("tomorrow", 1);
		relativeDaysMap.put("day after tomorrow", 2);
		relativeDaysMap.put("day before yesterday", -2);
		relativeDaysMap.put("now", 0);
		relativeDaysMap.put("days from today", 1);
		relativeDaysMap.put("days from now", 1);

		relativeHoursMap.put("now", 0);
		relativeHoursMap.put("hours from now", 1);
		relativeHoursMap.put("hours before now", 1);

	}

	public boolean isWeekDay(String day) {
		return weekdayMap.containsKey(day);

	}

	public boolean isMonth(String month) {
		return monthsMap.containsKey(month);

	}

	public boolean isRelativeDay(String relativeDay) {
		return relativeDaysMap.containsKey(relativeDay);

	}

	public boolean isRelativeHour(String relativeHour) {
		return relativeHoursMap.containsKey(relativeHour);

	}

	public Integer getWeekDay(String day) {
		return weekdayMap.get(day);
	}

	public Integer getMonth(String month) {
		return monthsMap.get(month);
	}

	public Integer getRelativeDay(String relativeDay) {
		return relativeDaysMap.get(relativeDay);
	}

	public Integer getRelativeHour(String relativeHour) {
		return relativeHoursMap.get(relativeHour);
	}

	public Map<String, Integer> getWeekdayMap() {
		return weekdayMap;
	}

	public Map<String, Integer> getMonthsMap() {
		return monthsMap;
	}

	public Map<String, Integer> getRelativeDaysMap() {
		return relativeDaysMap;
	}

	public Map<String, Integer> getRelativeHoursMap() {
		return relativeHoursMap;
	}
	
	
}
