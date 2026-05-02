package com.example.thymeleaf_web.util;

import java.util.List;

/**
 * Utility class for string operations.
 */
public final class StringUtils {
    
    private static final String EMPTY_STRING = "";
    
    private StringUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Check if string is null or empty.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Capitalize first letter of string.
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return EMPTY_STRING;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Convert string to lowercase.
     */
    public static String toLowerCase(String str) {
        if (isEmpty(str)) {
            return EMPTY_STRING;
        }
        return str.toLowerCase();
    }
    
    /**
     * Convert string to uppercase.
     */
    public static String toUpperCase(String str) {
        if (isEmpty(str)) {
            return EMPTY_STRING;
        }
        return str.toUpperCase();
    }
    
    /**
     * Truncate string if longer than maxLength and append ellipsis.
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str)) {
            return EMPTY_STRING;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    /**
     * Mask email address (example: user@example.com -> u***@example.com).
     */
    public static String maskEmail(String email) {
        if (isEmpty(email)) {
            return EMPTY_STRING;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        String firstChar = email.substring(0, 1);
        String domain = email.substring(atIndex);
        return firstChar + "***" + domain;
    }
    
    /**
     * Join list of strings with separator.
     */
    public static String join(List<String> list, String separator) {
        if (list == null || list.isEmpty()) {
            return EMPTY_STRING;
        }
        return String.join(separator, list);
    }
    
    /**
     * Check if string contains only alphabets.
     */
    public static boolean isAlpha(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z]+$");
    }
    
    /**
     * Check if string contains only numbers.
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[0-9]+$");
    }
    
    /**
     * Safe toString that returns empty string for null.
     */
    public static String safeToString(Object obj) {
        return obj == null ? EMPTY_STRING : obj.toString();
    }
}