package dev.lisovskiy.meal_planner_api.util;

public class PrefixValidator {

    public static String validate(String prefix) {
        return prefix.isBlank() ? "" : prefix + '.';
    }
}
