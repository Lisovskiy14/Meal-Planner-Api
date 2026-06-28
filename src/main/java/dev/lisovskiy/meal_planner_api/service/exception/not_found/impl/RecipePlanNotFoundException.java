package dev.lisovskiy.meal_planner_api.service.exception.not_found.impl;

import dev.lisovskiy.meal_planner_api.service.exception.not_found.NotFoundException;

public class RecipePlanNotFoundException extends NotFoundException {
    private static final String RECIPE_PLAN_WITH_ID_NOT_FOUND = "RecipePlan with id '%s' not found";

    public RecipePlanNotFoundException(Long id) {
        super(RECIPE_PLAN_WITH_ID_NOT_FOUND.formatted(id));
    }
}
