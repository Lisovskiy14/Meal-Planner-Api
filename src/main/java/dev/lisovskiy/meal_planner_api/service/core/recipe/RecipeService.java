package dev.lisovskiy.meal_planner_api.service.core.recipe;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.UpdateRecipeDto;

import java.util.List;

public interface RecipeService {
    List<Recipe> getAllRecipes();
    Recipe getRecipeById(Long id);
    Recipe createRecipe(CreateRecipeDto createRecipeDto);
    Recipe updateRecipeById(Long id, UpdateRecipeDto updateRecipeDto);
    void deleteRecipeById(Long id);
}
