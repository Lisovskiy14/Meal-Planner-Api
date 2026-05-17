package dev.lisovskiy.meal_planner_api.service.exception.not_found.impl;

import dev.lisovskiy.meal_planner_api.service.exception.not_found.NotFoundException;

public class RecipeNotFoundException extends NotFoundException {
    private static final String RECIPE_WITH_ID_NOT_FOUND = "Recipe with id '%d' not found!";

    public RecipeNotFoundException(Long id) {
        super(RECIPE_WITH_ID_NOT_FOUND.formatted(id));
    }
}
