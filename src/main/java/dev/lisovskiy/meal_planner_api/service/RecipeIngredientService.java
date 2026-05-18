package dev.lisovskiy.meal_planner_api.service;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.UpdateRecipeIngredientDto;

import java.util.List;

public interface RecipeIngredientService {
    List<RecipeIngredient> getAllRecipeIngredients(Long recipeId);
    RecipeIngredient getRecipeIngredientById(Long recipeId, Long recipeIngredientId);
    List<RecipeIngredient> createRecipeIngredients(Long recipeId, List<CreateRecipeIngredientDto> createRecipeIngredientDtoList);
    RecipeIngredient updateRecipeIngredient(
            Long recipeId, Long recipeIngredientId,
            UpdateRecipeIngredientDto createRecipeIngredientDto);
    void deleteRecipeIngredientById(Long id);
}
