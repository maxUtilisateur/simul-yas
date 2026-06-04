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
        if (phone.startsWith(" ")) {
            phone = "+" + phone.substring(1);
        }
        return phone.trim();
    }
}
