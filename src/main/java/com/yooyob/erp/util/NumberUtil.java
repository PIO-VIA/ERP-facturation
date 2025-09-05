package com.yooyob.erp.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


@UtilityClass
public class NumberUtil {

    public static final int DEFAULT_SCALE = 2;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    // Formatters pour l'affichage
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#,##0.00%");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.##");

    /**
     * Convertit un double en BigDecimal avec l'échelle par défaut
     */
    public static BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Convertit un double en BigDecimal avec une échelle spécifique
     */
    public static BigDecimal toBigDecimal(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Arrondit un BigDecimal avec l'échelle par défaut
     */
    public static BigDecimal round(BigDecimal value) {
        return value != null ? value.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE) : BigDecimal.ZERO;
    }

    /**
     * Arrondit un BigDecimal avec une échelle spécifique
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        return value != null ? value.setScale(scale, DEFAULT_ROUNDING_MODE) : BigDecimal.ZERO;
    }

    /**
     * Vérifie si un BigDecimal est zéro
     */
    public static boolean isZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Vérifie si un BigDecimal est positif
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Vérifie si un BigDecimal est négatif
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Vérifie si un BigDecimal est positif ou nul
     */
    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Retourne la valeur absolue d'un BigDecimal
     */
    public static BigDecimal abs(BigDecimal value) {
        return value != null ? value.abs() : BigDecimal.ZERO;
    }

    /**
     * Retourne le maximum entre deux BigDecimal
     */
    public static BigDecimal max(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) return value2 != null ? value2 : BigDecimal.ZERO;
        if (value2 == null) return value1;
        return value1.compareTo(value2) >= 0 ? value1 : value2;
    }

    /**
     * Retourne le minimum entre deux BigDecimal
     */
    public static BigDecimal min(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) return value2 != null ? value2 : BigDecimal.ZERO;
        if (value2 == null) return value1;
        return value1.compareTo(value2) <= 0 ? value1 : value2;
    }

    /**
     * Addition sécurisée de BigDecimal
     */
    public static BigDecimal safeAdd(BigDecimal value1, BigDecimal value2) {
        BigDecimal v1 = value1 != null ? value1 : BigDecimal.ZERO;
        BigDecimal v2 = value2 != null ? value2 : BigDecimal.ZERO;
        return v1.add(v2);
    }

    /**
     * Soustraction sécurisée de BigDecimal
     */
    public static BigDecimal safeSubtract(BigDecimal value1, BigDecimal value2) {
        BigDecimal v1 = value1 != null ? value1 : BigDecimal.ZERO;
        BigDecimal v2 = value2 != null ? value2 : BigDecimal.ZERO;
        return v1.subtract(v2);
    }

    /**
     * Multiplication sécurisée de BigDecimal
     */
    public static BigDecimal safeMultiply(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            return BigDecimal.ZERO;
        }
        return value1.multiply(value2);
    }

    /**
     * Division sécurisée de BigDecimal
     */
    public static BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Division sécurisée avec échelle personnalisée
     */
    public static BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor, int scale) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, scale, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Calcule un pourcentage
     */
    public static BigDecimal percentage(BigDecimal value, BigDecimal total) {
        if (isZero(total)) {
            return BigDecimal.ZERO;
        }
        return safeDivide(safeMultiply(value, BigDecimal.valueOf(100)), total);
    }

    /**
     * Applique un pourcentage à une valeur
     */
    public static BigDecimal applyPercentage(BigDecimal value, BigDecimal percentage) {
        return safeMultiply(value, safeDivide(percentage, BigDecimal.valueOf(100)));
    }

    /**
     * Formate un montant pour l'affichage en devise
     */
    public static String formatCurrency(BigDecimal amount) {
        return amount != null ? CURRENCY_FORMAT.format(amount) : "0,00";
    }

    /**
     * Formate un montant avec symbole de devise
     */
    public static String formatCurrencyWithSymbol(BigDecimal amount, String currencySymbol) {
        String formattedAmount = formatCurrency(amount);
        return currencySymbol != null ? formattedAmount + " " + currencySymbol : formattedAmount;
    }

    /**
     * Formate un pourcentage
     */
    public static String formatPercentage(BigDecimal percentage) {
        if (percentage == null) {
            return "0,00%";
        }
        return PERCENTAGE_FORMAT.format(percentage.divide(BigDecimal.valueOf(100)));
    }

    /**
     * Formate un nombre avec séparateurs de milliers
     */
    public static String formatNumber(BigDecimal number) {
        return number != null ? NUMBER_FORMAT.format(number) : "0";
    }

    /**
     * Parse une chaîne en BigDecimal
     */
    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            // Supprime les séparateurs de milliers et remplace la virgule par un point
            String cleanValue = value.trim()
                    .replace(" ", "")
                    .replace(",", ".")
                    .replaceAll("[^0-9.-]", "");

            return new BigDecimal(cleanValue).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Impossible de parser le nombre: " + value, e);
        }
    }

    /**
     * Vérifie si une valeur est dans une plage
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return false;
        }

        boolean aboveMin = min == null || value.compareTo(min) >= 0;
        boolean belowMax = max == null || value.compareTo(max) <= 0;

        return aboveMin && belowMax;
    }

    /**
     * Calcule la TVA
     */
    public static BigDecimal calculateTax(BigDecimal amountHT, BigDecimal taxRate) {
        return safeMultiply(amountHT, safeDivide(taxRate, BigDecimal.valueOf(100)));
    }

    /**
     * Calcule le montant HT à partir du TTC
     */
    public static BigDecimal calculateAmountExcludingTax(BigDecimal amountTTC, BigDecimal taxRate) {
        BigDecimal divisor = BigDecimal.ONE.add(safeDivide(taxRate, BigDecimal.valueOf(100)));
        return safeDivide(amountTTC, divisor);
    }

    /**
     * Calcule le montant TTC à partir du HT
     */
    public static BigDecimal calculateAmountIncludingTax(BigDecimal amountHT, BigDecimal taxRate) {
        BigDecimal tax = calculateTax(amountHT, taxRate);
        return safeAdd(amountHT, tax);
    }

    /**
     * Génère un nombre aléatoirement pour les références
     */
    public static String generateNumericReference(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

}