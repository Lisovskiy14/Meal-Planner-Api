package dev.lisovskiy.meal_planner_api.service.core.ingredient;

import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;

public interface IngredientServiceCommunicator {
    IngredientEntity getIngredientEntityById(Long id);
}
