package dev.lisovskiy.meal_planner_api.domain;

import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeIngredient {
    private Long id;
    private Ingredient ingredient;
    private IngredientUnit unit;
    private Double quantity;
}
