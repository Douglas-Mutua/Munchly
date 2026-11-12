package com.example.munchly.util;

import java.util.regex.Pattern;

public final class PhoneValidator {

    // Accepts: 07XXXXXXXX, 01XXXXXXXX, +2547XXXXXXXX, +2541XXXXXXXX,
    //          2547XXXXXXXX, 2541XXXXXXXX
    private static final Pattern KE_PHONE = Pattern.compile(
            "^(?:\\+?254|0)([17]\\d{8})$");

    private PhoneValidator() {}

    /** Returns true if the input is a valid Kenyan mobile number. */
    public static boolean isValid(String phone) {
        if (phone == null) return false;
        return KE_PHONE.matcher(phone.trim()).matches();
    }

    /** Normalises to 2547XXXXXXXX / 2541XXXXXXXX form for Daraja. */
    public static String normalize(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("0")) {
            digits = "254" + digits.substring(1);
        } else if (digits.startsWith("7") || digits.startsWith("1")) {
            digits = "254" + digits;
        }
        return digits;
    }
}