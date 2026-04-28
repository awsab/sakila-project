package com.me.learning.framework.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * General-purpose utility methods shared across all Sakila microservices.
 *
 * <p>All methods are stateless static helpers — no Spring bean needed.
 * For Bean Validation constraints use the standard annotations from
 * {@code jakarta.validation.constraints} instead.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SakilaUtils {

    // ── Common regex patterns ────────────────────────────────

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PASSPORT_PATTERN =
            Pattern.compile("^[A-Z0-9]{6,9}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    // ── Null / blank guards ──────────────────────────────────

    /**
     * Returns the value if non-null and non-blank, or the fallback otherwise.
     */
    public static String defaultIfBlank(String value, String fallback) {
        return StringUtils.isNotBlank(value) ? value : fallback;
    }

    /**
     * Throws {@link IllegalArgumentException} if value is null or blank.
     *
     * @param value     value to check
     * @param fieldName used in the exception message
     */
    public static void requireNonBlank(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
    }

    /**
     * Throws {@link IllegalArgumentException} if the collection is null or empty.
     */
    public static void requireNonEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    // ── UUID helpers ─────────────────────────────────────────

    /**
     * Safely parses a UUID string, returning {@code null} on invalid input
     * rather than throwing.
     */
    public static UUID parseUuidOrNull(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns {@code true} if the string is a valid UUID.
     */
    public static boolean isValidUuid(String value) {
        return parseUuidOrNull(value) != null;
    }

    // ── Domain validation ────────────────────────────────────

    /**
     * Returns {@code true} if the email address is syntactically valid.
     * Does NOT check deliverability.
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Returns {@code true} if the value matches the ICAO passport number
     * format: 6–9 uppercase alphanumeric characters.
     */
    public static boolean isValidPassportNumber(String passportNumber) {
        return StringUtils.isNotBlank(passportNumber)
                && PASSPORT_PATTERN.matcher(passportNumber.trim().toUpperCase(Locale.ROOT)).matches();
    }

    /**
     * Returns {@code true} if the phone number is in E.164-compatible format.
     */
    public static boolean isValidPhoneNumber(String phone) {
        return StringUtils.isNotBlank(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Returns {@code true} if the date string is a valid ISO-8601 local date
     * (e.g. {@code "2024-11-15"}).
     */
    public static boolean isValidDate(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return false;
        }
        try {
            LocalDate.parse(dateString.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the given date is in the future.
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Returns {@code true} if the given date is in the past.
     */
    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    // ── String utilities ─────────────────────────────────────

    /**
     * Truncates a string to the given maximum length, appending
     * {@code "..."} if truncated.
     */
    public static String truncate(String value, int maxLength) {
        if (StringUtils.isBlank(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    /**
     * Masks a sensitive string, showing only the first and last {@code visibleChars}
     * characters. Used for logging passport numbers, card numbers etc.
     *
     * <p>Example: {@code mask("A12345678", 2)} → {@code "A1*****78"}
     */
    public static String mask(String value, int visibleChars) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        if (value.length() <= visibleChars * 2) {
            return "***";
        }
        String prefix = value.substring(0, visibleChars);
        String suffix = value.substring(value.length() - visibleChars);
        String masked = "*".repeat(value.length() - visibleChars * 2);
        return prefix + masked + suffix;
    }

    // ── Collection utilities ─────────────────────────────────

    /**
     * Returns {@code true} if the collection is null or empty.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns {@code true} if the map is null or empty.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}

