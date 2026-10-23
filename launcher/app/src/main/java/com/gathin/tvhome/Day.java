package com.gathin.tvhome;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A calendar day (year, month, day): the launcher's stand-in for {@code java.time.LocalDate}, which
 * needs API 26. Dates arrive from a theme's config.json as ISO text and are only compared against
 * today or turned into a day-of-year, so plain Calendar is enough. No Android types, so it is
 * tested on the JVM.
 */
final class Day {

    private static final Pattern ISO = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    final int year;
    /** 1-12, unlike Calendar's months. */
    final int month;
    /** 1-31. */
    final int day;

    private Day(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    static Day of(int year, int month, int day) {
        return new Day(year, month, day);
    }

    static Day today() {
        Calendar now = Calendar.getInstance();
        return new Day(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
    }

    /** "2026-10-23", or null when the text is not a real calendar date. */
    static Day parse(String text) {
        if (text == null) return null;
        Matcher m = ISO.matcher(text.trim());
        if (!m.matches()) return null;
        int year = Integer.parseInt(m.group(1)), month = Integer.parseInt(m.group(2)), day = Integer.parseInt(m.group(3));
        Calendar c = new GregorianCalendar(year, month - 1, day);
        c.setLenient(false);
        try {
            c.getTimeInMillis();
        } catch (RuntimeException e) {
            return null;   // rolls over (13th month, 30 February): not a date
        }
        return new Day(year, month, day);
    }

    /** Strictly later than that day. */
    boolean isAfter(Day other) {
        if (year != other.year) return year > other.year;
        if (month != other.month) return month > other.month;
        return day > other.day;
    }

    /** 1-366, the day number Sun's equations count from. */
    int dayOfYear() {
        return new GregorianCalendar(year, month - 1, day).get(Calendar.DAY_OF_YEAR);
    }

    /** Noon this day as UTC milliseconds in that zone, for the DST-aware offset Sun needs. */
    long noon(TimeZone zone) {
        Calendar c = new GregorianCalendar(zone);
        c.clear();
        c.set(year, month - 1, day, 12, 0, 0);
        return c.getTimeInMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Day)) return false;
        Day other = (Day) o;
        return year == other.year && month == other.month && day == other.day;
    }

    @Override
    public int hashCode() {
        return year * 10000 + month * 100 + day;
    }
}
