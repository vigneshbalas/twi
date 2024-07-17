package com.vigneshbala.twi.nlp;
/**
 * (c) 2024 Vignesh Balasubramanian
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public enum WEEKDAYS {
	SUNDAY("sunday", 0), MONDAY("monday", 1), TUESDAY("tuesday", 2), WEDNESDAY("wednesday", 3),
	THURSDAY("thursday", 4), FRIDAY("friday", 5), SATURDAY("saturday", 6), SUN("sun", 0), MON("mon", 1),
	TUE("tue", 2), WED("wed", 3), THU("thu", 4), FRI("fri", 5), SAT("sat", 6);

	private final String key;
	private final int value;

	WEEKDAYS(final String newkey, final int newValue) {
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