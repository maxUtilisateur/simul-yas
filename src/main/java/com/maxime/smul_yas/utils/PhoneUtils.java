package com.maxime.smul_yas.utils;

public class PhoneUtils {

    /**
     * Normalizes a phone number.
     * When a URL query parameter contains a '+' sign (e.g. +228...), Spring decodes it as a space ' '.
     * This method detects if the phone number starts with a space and replaces it back with '+',
     * then trims any leading or trailing whitespace.
     */
    public static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String trimmed = phone.trim();
        if (phone.startsWith(" ") || trimmed.startsWith("+")) {
            String cleaned = trimmed.replace(" ", "");
            if (!cleaned.startsWith("+")) {
                cleaned = "+" + cleaned;
            }
            return cleaned;
        }
        return trimmed.replace(" ", "");
    }
}
