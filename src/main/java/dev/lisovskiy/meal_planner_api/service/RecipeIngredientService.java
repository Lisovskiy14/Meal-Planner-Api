package dev.lisovskiy.meal_planner_api.service;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.UpdateRecipeIngredientDto;

import java.util.List;

public interface RecipeIngredientService {
    List<RecipeIngredient> getAllRecipeIngredients();
    RecipeIngredient getRecipeIngredientById(Long id);
    RecipeIngredient createRecipeIngredient(CreateRecipeIngredientDto createRecipeIngredientDto);
    RecipeIngredient updateRecipeIngredient(Long id, UpdateRecipeIngredientDto createRecipeIngredientDto);
    void deleteRecipeIngredientById(Long id);
}
