package dev.lisovskiy.meal_planner_api.service.core.recipe;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;

public interface RecipeFacade {
    Recipe createRecipeWithIngredients(CreateRecipeDto createRecipeDto);
}
