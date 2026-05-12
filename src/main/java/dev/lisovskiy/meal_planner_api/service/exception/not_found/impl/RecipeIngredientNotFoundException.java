package dev.lisovskiy.meal_planner_api.service.exception.not_found.impl;

import dev.lisovskiy.meal_planner_api.service.exception.not_found.NotFoundException;

public class RecipeIngredientNotFoundException extends NotFoundException {
    private static final String RECIPE_INGREDIENT_WITH_ID_NOT_FOUND = "Recipe ingredient with id '%d' not found!";

    public RecipeIngredientNotFoundException(Long id) {
        super(RECIPE_INGREDIENT_WITH_ID_NOT_FOUND.formatted(id));
    }
}
