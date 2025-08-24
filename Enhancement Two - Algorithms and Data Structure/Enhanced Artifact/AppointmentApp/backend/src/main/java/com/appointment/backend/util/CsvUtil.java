package com.appointment.backend.util;

import com.appointment.backend.model.Appointment;

import java.util.List;

/**
 * Utilities to serialize appointments as CSV.
 * - Adds a header row
 * - Escapes fields that contain commas, quotes, or newlines
 * This keeps exports compatible with spreadsheets and CSV readers.
 */
public final class CsvUtil {
    private CsvUtil() {
    }

    /**
     * Escape a single CSV field:
     * - Double any internal quotes
     * - Surround with quotes if the value contains commas/quotes/newlines
     */
    private static String escape(String s) {
        if (s == null)
            return "";
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String cleaned = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + cleaned + "\"" : cleaned;
    }

    /** Convert a list of appointments into a CSV string with a header row. */
    public static String toCsv(List<Appointment> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("appointmentId,appointmentDate,description\n");
        for (Appointment a : items) {
            sb.append(escape(a.getAppointmentId())).append(",")
                    .append(a.getAppointmentDate()).append(",")
                    .append(escape(a.getDescription())).append("\n");
        }
        return sb.toString();
    }
}
