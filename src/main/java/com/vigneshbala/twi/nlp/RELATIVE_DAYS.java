package com.vigneshbala.twi.nlp;
/**
 * (c) 2024 Vignesh Balasubramanian 
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public enum RELATIVE_DAYS {
	TODAY("today", 0), YESTERDAY("yesterday", -1), TOMORROW("tomorrow", 1), DAY_AFTER_TOMORROW("day_after_tomorrow", 2),
	DAY_BEFORE_YESTERDAY("day before yesterday", -2), NOW("now", 0), DAYS_FROM_TODAY("days from today", 1),
	DAYS_FROM_NOW("days from now", 1);

	private final String key;
	private final int value;

	RELATIVE_DAYS(final String newkey, final int newValue) {
		key = newkey;
		value = newValue;
	}

	public int getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}
}