package dev.lisovskiy.meal_planner_api.service.exception.conflict.impl;

import dev.lisovskiy.meal_planner_api.service.exception.conflict.AlreadyExistsException;

public class RecipeAlreadyExistsException extends AlreadyExistsException {
    private static final String RECIPE_WITH_NAME_ALREADY_EXISTS = "Recipe with name '%s' already exists!";

    public RecipeAlreadyExistsException(String name) {
        super(RECIPE_WITH_NAME_ALREADY_EXISTS.formatted(name));
    }
}
