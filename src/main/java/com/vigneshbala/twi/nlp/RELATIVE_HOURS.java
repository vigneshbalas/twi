package com.vigneshbala.twi.nlp;
/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public enum RELATIVE_HOURS {
	NOW("now", 0), HOURS_FROM_NOW("hours from now", 1),
	HOURS_BEFORE_NOW("hours before now", 1);

	private final String key;
	private final int value;

	RELATIVE_HOURS(final String newkey, final int newValue) {
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