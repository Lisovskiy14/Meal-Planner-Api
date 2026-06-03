package dev.lisovskiy.meal_planner_api.service.exception.conflict.impl;

import dev.lisovskiy.meal_planner_api.service.exception.conflict.AlreadyExistsException;

public class IngredientAlreadyExistsException extends AlreadyExistsException {
    private static final String INGREDIENT_WITH_NAME_ALREADY_EXISTS = "Ingredient with name '%s' already exists!";

    public IngredientAlreadyExistsException(String name) {
        super(INGREDIENT_WITH_NAME_ALREADY_EXISTS.formatted(name));
    }
}
