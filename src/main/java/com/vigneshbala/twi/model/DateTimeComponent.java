package com.vigneshbala.twi.model;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import com.vigneshbala.twi.nlp.DateTimeUnits;

public class DateTimeComponent {
	int dayDelta;
	int monthDelta;
	int yearDelta;
	int hourDelta;
	int minuteDelta;
	ZonedDateTime baseTime;
	boolean isPast;
	boolean isDateTimePresent;
	int countDeltas;
	int countAbsoluteDateTimes;
	int toDate;
	int toMonth;
	int toYear;
	int toHour;
	int toMin;

	boolean adjustForPastDate = true;

	private static final int TOTAL_WEEKDAYS = 7;
	private static final int TOTAL_MONTHS = 12;

	public DateTimeComponent(ZonedDateTime baseTime, boolean isPast) {
		this.baseTime = baseTime;
		this.isPast = isPast;
		this.toDate = baseTime.getDayOfMonth();
		this.toMonth = baseTime.getMonthValue();
		this.toYear = baseTime.getYear();
		this.toHour = baseTime.getHour();
		this.toMin = baseTime.getMinute();

	}

	public int getToHour() {
		return toHour;
	}

	public void setToHour(int toHour) {
		this.toHour = toHour;
		// set minute to zero if hour is available.
		this.toMin = 0;
		this.isDateTimePresent = true;
	}

	public int getToMin() {
		return toMin;
	}

	public void setToMin(int toMin) {
		this.toMin = toMin;
		this.isDateTimePresent = true;
	}

	public int getToDate() {
		return toDate;
	}

	public void setToDate(int toDate) {
		this.toDate = toDate;
		this.countAbsoluteDateTimes++;
		this.isDateTimePresent = true;
	}

	public int getToMonth() {
		return toMonth;
	}

	public void setToMonth(String toMonth) {
		this.toMonth = toMonthNumber(toMonth);
		this.countAbsoluteDateTimes++;
		this.isDateTimePresent = true;
	}

	public void setToMonth(int toMonth) {
		this.toMonth = toMonth;
		this.countAbsoluteDateTimes++;
		this.isDateTimePresent = true;
	}

	public int getToYear() {
		return toYear;
	}

	public void setToYear(int toYear) {
		this.toYear = toYear;
		this.adjustForPastDate = false;
		this.countAbsoluteDateTimes++;
		this.isDateTimePresent = true;
	}

	public int getDayDelta() {
		return dayDelta;
	}

	public void setDayDelta(String day) {

		int deltaDays = DateTimeUnits.getInstance().getWeekDay(day) - baseTime.getDayOfWeek().getValue();
		if (!isPast) {
			deltaDays = deltaDays >= 0 ? deltaDays : (TOTAL_WEEKDAYS + deltaDays);
		}

		this.dayDelta = deltaDays;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public void setDayDelta(int dayDelta) {

		this.dayDelta = dayDelta;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public void setRelativeDayDelta(String day) {
		int deltaDays = DateTimeUnits.getInstance().getRelativeDay(day);
		this.dayDelta = deltaDays;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public int getMonthDelta() {
		return monthDelta;
	}

	public void setMonthDelta(int monthDelta) {
		this.monthDelta = monthDelta;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public void setMonthDelta(String month) {
		int deltaMonths = DateTimeUnits.getInstance().getMonth(month) - baseTime.getMonthValue();
		if (!isPast) {
			deltaMonths = deltaMonths >= 0 ? deltaMonths : (TOTAL_MONTHS + deltaMonths + 1);
		}
		this.monthDelta = deltaMonths;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public int getYearDelta() {
		return yearDelta;
	}

	public void setYearDelta(int yearDelta) {
		this.yearDelta = yearDelta;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public int getHourDelta() {
		return hourDelta;
	}

	public void setHourDelta(int hourDelta) {
		this.hourDelta = hourDelta;
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public int getMinuteDelta() {
		return minuteDelta;
	}

	public void setMinuteDelta(int minuteDelta) {
		this.minuteDelta = minuteDelta;
		this.isDateTimePresent = true;
	}

	public boolean noDateTimePresent() {
		return !this.isDateTimePresent;
	}

	public boolean moreDateTimePresent() {
		return this.countDeltas > 1 || (this.countAbsoluteDateTimes > 0 && this.countDeltas > 0);

	}

	public ZonedDateTime getDateTime() {
		ZonedDateTime result = null;
		if (this.dayDelta != 0) {
			this.toDate = this.baseTime.plusDays(dayDelta).getDayOfMonth();
		}
		if (this.monthDelta != 0) {
			this.toMonth = this.baseTime.plusMonths(monthDelta).getMonthValue();
		}
		if (this.yearDelta != 0) {
			this.toYear = this.baseTime.plusYears(yearDelta).getYear();
		}
		if (this.hourDelta != 0) {
			this.toHour = this.baseTime.plusHours(hourDelta).getHour();

		}
		if (this.minuteDelta != 0) {
			this.toMin = this.baseTime.plusMinutes(minuteDelta).getMinute();
		}

		result = ZonedDateTime.of(LocalDateTime.of(toYear, toMonth, toDate, toHour, toMin), ZoneId.systemDefault());

		if (this.adjustForPastDate && result.getMonthValue() < baseTime.getMonthValue()) {
			result = result.plusYears(1);
			if (result.getDayOfMonth() < baseTime.getDayOfMonth()) {
				result = result.plusMonths(1);
			}

		}
		return result;
	}

	private static int toMonthNumber(String monthName) {
		int result;
		if (monthName.length() > 3) {
			result = Month.valueOf(monthName.toUpperCase()).getValue();
		} else {
			Optional<Month> monthOptional = Arrays.stream(Month.values())
					.filter(month -> month.name().substring(0, 3).equalsIgnoreCase(monthName)).findFirst();

			result = monthOptional.orElseThrow(IllegalArgumentException::new).getValue();
		}
		return result;

	}

}
