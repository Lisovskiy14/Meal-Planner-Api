package dev.lisovskiy.meal_planner_api.service.core.recipe;

import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;

public interface RecipeServiceCommunicator {
    RecipeEntity getRecipeEntityById(Long id);
}
