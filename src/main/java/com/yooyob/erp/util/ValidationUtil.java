package com.yooyob.erp.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

@UtilityClass
public class ValidationUtil {

    // Expressions régulières pour la validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s\\-()]{8,15}$"
    );

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(
            "^[0-9]{5}$|^[A-Z0-9]{3}\\s?[A-Z0-9]{3}$"
    );

    private static final Pattern IBAN_PATTERN = Pattern.compile(
            "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$"
    );

    private static final Pattern TVA_NUMBER_PATTERN = Pattern.compile(
            "^[A-Z]{2}[0-9A-Z]{2,13}$"
    );

    /**
     * Vérifie si une chaîne n'est pas null et n'est pas vide
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Vérifie si une chaîne est null ou vide
     */
    public static boolean isBlank(String value) {
        return !isNotBlank(value);
    }

    /**
     * Vérifie si une collection n'est pas null et n'est pas vide
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Vérifie si une collection est null ou vide
     */
    public static boolean isEmpty(Collection<?> collection) {
        return !isNotEmpty(collection);
    }

    /**
     * Valide un email
     */
    public static boolean isValidEmail(String email) {
        return isNotBlank(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valide un numéro de téléphone
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return isNotBlank(phoneNumber) && PHONE_PATTERN.matcher(phoneNumber.replaceAll("\\s", "")).matches();
    }

    /**
     * Valide un code postal
     */
    public static boolean isValidPostalCode(String postalCode) {
        return isNotBlank(postalCode) && POSTAL_CODE_PATTERN.matcher(postalCode.trim()).matches();
    }

    /**
     * Valide un IBAN
     */
    public static boolean isValidIban(String iban) {
        if (isBlank(iban)) {
            return false;
        }
        String cleanedIban = iban.replaceAll("\\s", "").toUpperCase();
        return IBAN_PATTERN.matcher(cleanedIban).matches();
    }

    /**
     * Valide un numéro de TVA
     */
    public static boolean isValidTvaNumber(String tvaNumber) {
        if (isBlank(tvaNumber)) {
            return false;
        }
        String cleanedTva = tvaNumber.replaceAll("\\s", "").toUpperCase();
        return TVA_NUMBER_PATTERN.matcher(cleanedTva).matches();
    }

    /**
     * Valide un UUID
     */
    public static boolean isValidUuid(String uuid) {
        if (isBlank(uuid)) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Valide un UUID objet
     */
    public static boolean isValidUuid(UUID uuid) {
        return uuid != null;
    }

    /**
     * Valide qu'un BigDecimal est positif
     */
    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Valide qu'un BigDecimal est positif ou zéro
     */
    public static boolean isNonNegativeAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Valide qu'un entier est positif
     */
    public static boolean isPositiveInteger(Integer value) {
        return value != null && value > 0;
    }

    /**
     * Valide qu'un entier est positif ou zéro
     */
    public static boolean isNonNegativeInteger(Integer value) {
        return value != null && value >= 0;
    }

    /**
     * Valide qu'une date n'est pas dans le futur
     */
    public static boolean isNotInFuture(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    /**
     * Valide qu'une date n'est pas dans le passé
     */
    public static boolean isNotInPast(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    /**
     * Valide qu'une date d'échéance est après la date de facturation
     */
    public static boolean isValidDueDate(LocalDate facturationDate, LocalDate dueDate) {
        return facturationDate != null && dueDate != null &&
                (dueDate.isAfter(facturationDate) || dueDate.isEqual(facturationDate));
    }

    /**
     * Valide qu'une chaîne respecte une longueur minimale
     */
    public static boolean hasMinLength(String value, int minLength) {
        return isNotBlank(value) && value.trim().length() >= minLength;
    }

    /**
     * Valide qu'une chaîne respecte une longueur maximale
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.trim().length() <= maxLength;
    }

    /**
     * Valide qu'une chaîne respecte une plage de longueur
     */
    public static boolean hasValidLength(String value, int minLength, int maxLength) {
        return hasMinLength(value, minLength) && hasMaxLength(value, maxLength);
    }

    /**
     * Valide qu'un montant est dans une plage acceptable
     */
    public static boolean isAmountInRange(BigDecimal amount, BigDecimal min, BigDecimal max) {
        if (amount == null) {
            return false;
        }

        boolean aboveMin = min == null || amount.compareTo(min) >= 0;
        boolean belowMax = max == null || amount.compareTo(max) <= 0;

        return aboveMin && belowMax;
    }

    /**
     * Valide un code client/fournisseur (alphanumerique)
     */
    public static boolean isValidCode(String code) {
        return isNotBlank(code) && code.matches("^[A-Za-z0-9\\-_]+$");
    }

    /**
     * Valide un numéro de facture
     */
    public static boolean isValidFactureNumber(String numeroFacture) {
        return isNotBlank(numeroFacture) && numeroFacture.matches("^[A-Za-z0-9\\-_/]+$");
    }

    /**
     * Valide qu'une référence produit est valide
     */
    public static boolean isValidProductReference(String reference) {
        return isNotBlank(reference) && reference.matches("^[A-Za-z0-9\\-_\\.]+$");
    }

    /**
     * Valide un code barre
     */
    public static boolean isValidBarcode(String barcode) {
        return isNotBlank(barcode) && barcode.matches("^[0-9]{8,14}$");
    }

    /**
     * Valide un taux de taxe (entre 0 et 100%)
     */
    public static boolean isValidTaxRate(BigDecimal taxRate) {
        return taxRate != null &&
                taxRate.compareTo(BigDecimal.ZERO) >= 0 &&
                taxRate.compareTo(BigDecimal.valueOf(100)) <= 0;
    }

    /**
     * Valide qu'une date est dans une plage acceptable pour une facture
     */
    public static boolean isValidFactureDate(LocalDate date) {
        if (date == null) {
            return false;
        }

        // Ne peut pas être plus de 2 ans dans le passé
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
        // Ne peut pas être plus de 1 mois dans le futur
        LocalDate oneMonthFromNow = LocalDate.now().plusMonths(1);

        return date.isAfter(twoYearsAgo) && date.isBefore(oneMonthFromNow);
    }

    /**
     * Valide les conditions de paiement (format: nombre de jours)
     */
    public static boolean isValidPaymentTerms(String paymentTerms) {
        if (isBlank(paymentTerms)) {
            return true; // Optionnel
        }

        return paymentTerms.matches("^\\d{1,3}\\s*(jours?|days?|j)?$");
    }

    /**
     * Nettoie et valide un numéro de téléphone
     */
    public static String cleanPhoneNumber(String phoneNumber) {
        if (isBlank(phoneNumber)) {
            return null;
        }

        return phoneNumber.replaceAll("[^+0-9]", "");
    }

    /**
     * Nettoie un email
     */
    public static String cleanEmail(String email) {
        if (isBlank(email)) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    /**
     * Valide qu'un montant de paiement ne dépasse pas le montant dû
     */
    public static boolean isValidPaymentAmount(BigDecimal paymentAmount, BigDecimal amountDue) {
        return paymentAmount != null && amountDue != null &&
                paymentAmount.compareTo(BigDecimal.ZERO) > 0 &&
                paymentAmount.compareTo(amountDue) <= 0;
    }

    /**
     * Valide la cohérence d'une ligne de facture
     */
    public static boolean isValidFactureLine(Integer quantity, BigDecimal unitPrice, BigDecimal totalAmount) {
        if (!isPositiveInteger(quantity) || !isNonNegativeAmount(unitPrice)) {
            return false;
        }

        if (totalAmount != null) {
            BigDecimal calculatedTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            return calculatedTotal.compareTo(totalAmount) == 0;
        }

        return true;
    }

    /**
     * Valide qu'un pourcentage est dans la plage valide
     */
    public static boolean isValidPercentage(BigDecimal percentage) {
        return percentage != null &&
                percentage.compareTo(BigDecimal.ZERO) >= 0 &&
                percentage.compareTo(BigDecimal.valueOf(100)) <= 0;
    }
}