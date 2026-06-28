package dev.lisovskiy.meal_planner_api.service.exception.not_found.impl;

import dev.lisovskiy.meal_planner_api.service.exception.not_found.NotFoundException;

public class MealPlanNotFoundException extends NotFoundException {
    private static final String MEAL_PLAN_WITH_ID_NOT_FOUND = "MealPlan with id '%s' not found";

    public MealPlanNotFoundException(Long id) {
        super(MEAL_PLAN_WITH_ID_NOT_FOUND.formatted(id));
    }
}
