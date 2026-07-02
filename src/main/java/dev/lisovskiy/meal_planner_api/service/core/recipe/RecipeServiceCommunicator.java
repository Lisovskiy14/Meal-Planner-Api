package dev.lisovskiy.meal_planner_api.service.core.recipe;

import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeEntity;

public interface RecipeServiceCommunicator {
    RecipeEntity getRecipeEntitySummaryById(Long id);
}
