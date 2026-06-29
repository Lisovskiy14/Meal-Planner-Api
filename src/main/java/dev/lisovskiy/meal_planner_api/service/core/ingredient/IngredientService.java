package dev.lisovskiy.meal_planner_api.service.core.ingredient;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.dto.ingredient.CreateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.UpdateIngredientDto;

import java.util.List;

public interface IngredientService {
    List<Ingredient> getAllIngredients();
    Ingredient getIngredientById(Long id);
    Ingredient createIngredient(CreateIngredientDto createIngredientDto);
    Ingredient updateIngredient(Long id, UpdateIngredientDto updateIngredientDto);
    void deleteIngredientById(Long id);
}
