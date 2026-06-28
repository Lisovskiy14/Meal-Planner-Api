package dev.lisovskiy.meal_planner_api.service.exception.conflict.impl;

import dev.lisovskiy.meal_planner_api.service.exception.conflict.AlreadyExistsException;

import java.time.LocalTime;

public class RecipePlanAlreadyExistsException extends AlreadyExistsException {
    private static final String TIME_ALREADY_EXISTS_WITH_MEAL_PLAN = "Time '%s' already exists with MealPlan '%s'";

    public RecipePlanAlreadyExistsException(LocalTime time, Long mealPlanId) {
        super(TIME_ALREADY_EXISTS_WITH_MEAL_PLAN.formatted(time, mealPlanId));
    }
}
