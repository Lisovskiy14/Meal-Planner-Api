package dev.lisovskiy.meal_planner_api.service;

import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;

public interface IngredientServiceCommunicator {
    IngredientEntity getIngredientEntityById(Long id);
}
