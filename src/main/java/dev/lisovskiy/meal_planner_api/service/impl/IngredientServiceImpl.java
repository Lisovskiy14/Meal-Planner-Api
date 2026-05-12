package dev.lisovskiy.meal_planner_api.service.impl;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.dto.ingredient.CreateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.UpdateIngredientDto;
import dev.lisovskiy.meal_planner_api.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    @Override
    public List<Ingredient> getAllIngredients() {
        return List.of();
    }

    @Override
    public Ingredient getIngredientById(Long id) {
        return null;
    }

    @Override
    public Ingredient createIngredient(CreateIngredientDto createIngredientDto) {
        return null;
    }

    @Override
    public Ingredient updateIngredient(Long id, UpdateIngredientDto updateIngredientDto) {
        return null;
    }

    @Override
    public void deleteIngredientById(Long id) {

    }
}
