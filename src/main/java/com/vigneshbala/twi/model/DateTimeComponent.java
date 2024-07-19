package com.vigneshbala.twi.model;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Period;

import com.vigneshbala.twi.nlp.DateTimeUnits;

public class DateTimeComponent {
	int dayDelta;
	int monthDelta;
	int yearDelta;
	int hourDelta;
	int minuteDelta;
	DateTime baseTime;
	boolean isPast;
	boolean isDateTimePresent;
	int countDeltas;
	int countAbsoluteDateTimes;
	int toDate;
	int toMonth;
	int toYear;

	private static final int TOTAL_WEEKDAYS = 7;
	private static final int TOTAL_MONTHS = 12;

	public DateTimeComponent(DateTime baseTime, boolean isPast) {
		this.baseTime = baseTime;
		this.isPast = isPast;
		this.toDate = baseTime.dayOfMonth().get();
		this.toMonth = baseTime.monthOfYear().get();
		this.toMonth = baseTime.getYear();

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
		this.toMonth = Months.parseMonths(toMonth).getMonths();
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
		this.countAbsoluteDateTimes++;
		this.isDateTimePresent = true;
	}

	public int getDayDelta() {
		return dayDelta;
	}

	public void setDayDelta(String day) {

		int deltaDays = DateTimeUnits.getInstance().getWeekDay(day) - baseTime.getDayOfWeek();
		if (!isPast) {
			deltaDays = deltaDays >= 0 ? deltaDays : (TOTAL_WEEKDAYS + deltaDays);
		}

		this.dayDelta = deltaDays;
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

	public void setMonthDelta(String month) {
		int deltaMonths = DateTimeUnits.getInstance().getMonth(month) - baseTime.getMonthOfYear();
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
		this.countDeltas++;
		this.isDateTimePresent = true;
	}

	public boolean noDateTimePresent() {
		return !this.isDateTimePresent;
	}

	public boolean moreDateTimePresent() {
		return this.countDeltas > 1 || (this.countAbsoluteDateTimes > 0 && this.countDeltas > 0);

	}

	public DateTime getDateTime() {
		DateTime result = null;
		if (countAbsoluteDateTimes > 0) {
			Period period = new Period().withMonths(toMonth).withDays(toDate).withYears(toYear);
			result = new DateTime(period);

		} else {
			result = this.baseTime.plusDays(dayDelta).plusMonths(monthDelta).plusYears(yearDelta)
					.plusMinutes(minuteDelta).plusHours(hourDelta);
		}
		return result;
	}

}
