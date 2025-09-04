package com.yooyob.erp.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@UtilityClass
public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String MONTH_YEAR_FORMAT = "MM/yyyy";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT);
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT);

    /**
     * Formate une date au format standard
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * Formate une date/heure au format standard
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * Formate une date pour l'affichage utilisateur
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE_FORMATTER) : null;
    }

    /**
     * Formate une date au format mois/année
     */
    public static String formatMonthYear(LocalDate date) {
        return date != null ? date.format(MONTH_YEAR_FORMATTER) : null;
    }

    /**
     * Parse une chaîne de caractères en LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return dateString != null ? LocalDate.parse(dateString, DATE_FORMATTER) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date invalide: " + dateString, e);
        }
    }

    /**
     * Parse une chaîne de caractères en LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date/heure invalide: " + dateTimeString, e);
        }
    }

    /**
     * Obtient le premier jour du mois
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.firstDayOfMonth()) : null;
    }

    /**
     * Obtient le dernier jour du mois
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.lastDayOfMonth()) : null;
    }

    /**
     * Obtient le premier jour de l'année
     */
    public static LocalDate getFirstDayOfYear(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.firstDayOfYear()) : null;
    }

    /**
     * Obtient le dernier jour de l'année
     */
    public static LocalDate getLastDayOfYear(LocalDate date) {
        return date != null ? date.with(TemporalAdjusters.lastDayOfYear()) : null;
    }

    /**
     * Calcule le nombre de jours entre deux dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Vérifie si une date est en retard par rapport à aujourd'hui
     */
    public static boolean isOverdue(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Vérifie si une date approche de l'échéance (dans les N jours)
     */
    public static boolean isApproachingDue(LocalDate date, int days) {
        if (date == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(days);
        return date.isAfter(today) && date.isBefore(threshold);
    }

    /**
     * Obtient le début du trimestre pour une date donnée
     */
    public static LocalDate getStartOfQuarter(LocalDate date) {
        if (date == null) {
            return null;
        }
        int quarter = (date.getMonthValue() - 1) / 3 + 1;
        int startMonth = (quarter - 1) * 3 + 1;
        return LocalDate.of(date.getYear(), startMonth, 1);
    }

    /**
     * Obtient la fin du trimestre pour une date donnée
     */
    public static LocalDate getEndOfQuarter(LocalDate date) {
        if (date == null) {
            return null;
        }
        int quarter = (date.getMonthValue() - 1) / 3 + 1;
        int endMonth = quarter * 3;
        return LocalDate.of(date.getYear(), endMonth, 1).with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * Obtient le numéro du trimestre pour une date donnée
     */
    public static int getQuarter(LocalDate date) {
        return date != null ? (date.getMonthValue() - 1) / 3 + 1 : 0;
    }

    /**
     * Vérifie si une année est bissextile
     */
    public static boolean isLeapYear(int year) {
        return LocalDate.of(year, 1, 1).isLeapYear();
    }

    /**
     * Ajoute des jours ouvrables à une date
     */
    public static LocalDate addBusinessDays(LocalDate date, int businessDays) {
        if (date == null) {
            return null;
        }

        LocalDate result = date;
        int addedDays = 0;

        while (addedDays < businessDays) {
            result = result.plusDays(1);
            // Lundi = 1, Dimanche = 7
            if (result.getDayOfWeek().getValue() <= 5) {
                addedDays++;
            }
        }

        return result;
    }

    /**
     * Calcule l'âge en jours depuis une date
     */
    public static long getAgeInDays(LocalDate fromDate) {
        return fromDate != null ? ChronoUnit.DAYS.between(fromDate, LocalDate.now()) : 0;
    }

    /**
     * Vérifie si une date est dans une plage donnée
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null) {
            return false;
        }
        if (startDate != null && date.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && date.isAfter(endDate)) {
            return false;
        }
        return true;
    }
}