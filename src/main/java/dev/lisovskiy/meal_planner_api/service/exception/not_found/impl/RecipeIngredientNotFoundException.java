package dev.lisovskiy.meal_planner_api.service.exception.not_found.impl;

import dev.lisovskiy.meal_planner_api.service.exception.not_found.NotFoundException;

public class RecipeIngredientNotFoundException extends NotFoundException {
    private static final String RECIPE_INGREDIENT_WITH_ID_NOT_FOUND =
            "Ingredient with id '%d' not found in recipe with id '%d'";

    public RecipeIngredientNotFoundException(Long recipeId, Long ingredientId) {
        super(RECIPE_INGREDIENT_WITH_ID_NOT_FOUND.formatted(ingredientId, recipeId));
    }
}
