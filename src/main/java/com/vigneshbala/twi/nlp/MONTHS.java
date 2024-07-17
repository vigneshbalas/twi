package com.vigneshbala.twi.nlp;
/**
 * (c) 2024 Vignesh Balasubramanian 
 * 
 * This code is licensed under MIT license (see LICENSE for details)
 */
public enum MONTHS {
	JANUARY("january", 0), FEBRUARY("february", 1), MARCH("march", 2), APRIL("april", 3), MAY("may", 4),
	JUNE("june", 6), JULY("july", 7), AUGUST("august", 8), SEPTEMBER("september", 9), OCTOBER("october", 10),
	NOVEMBER("november", 11), DECEMBER("december", 12), JAN("jan", 0), FEB("feb", 1), MAR("mar", 2), APR("apr", 3),
	JUN("jun", 6), JUL("jul", 7), AUG("aug", 8), SEP("sep", 9), OCT("oct", 10), NOV("nov", 11), DEC("dec", 12);

	private final String key;
	private final int value;

	MONTHS(final String newkey, final int newValue) {
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