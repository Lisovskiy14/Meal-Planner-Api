package dev.lisovskiy.meal_planner_api.service.exception.impl;

import dev.lisovskiy.meal_planner_api.service.exception.NotFoundException;

public class IngredientNotFoundException extends NotFoundException {
    private static final String INGREDIENT_WITH_ID_NOT_FOUND = "Ingredient with id '%d' not found!";
    public IngredientNotFoundException(Long id) {
        super(INGREDIENT_WITH_ID_NOT_FOUND.formatted(id));
    }
}
