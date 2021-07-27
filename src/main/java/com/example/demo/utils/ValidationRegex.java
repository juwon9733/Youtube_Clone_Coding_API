package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);     // CASE_INSENSITIVE : 대소문자를 무시하고 matching한다.
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    public static boolean isRegexBirth(String target) {
        String regex = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);     // CASE_INSENSITIVE : 대소문자를 무시하고 matching한다.
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexPhoneNumber(String target) {
        String regex = "^(01\\d{1}|02|0505|0502|0506|0\\d{1,2})-?(\\d{3,4})-?(\\d{4})";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);     // CASE_INSENSITIVE : 대소문자를 무시하고 matching한다.
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}

