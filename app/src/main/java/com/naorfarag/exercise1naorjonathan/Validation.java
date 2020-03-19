package com.naorfarag.exercise1naorjonathan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public final class Validation {
    public final static int VALID_PASS_LENGTH = 5;

    public static boolean isValidEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches() && emailStr.trim().length() != 0;
    }

    public static boolean isValidPassword(String password) {
        return password.length() > VALID_PASS_LENGTH;
    }

    public static boolean isValidPhone(String phone){
        return !phone.isEmpty();
    }

    public static boolean isValidAge(String age) {
        return !age.isEmpty();
    }
}
