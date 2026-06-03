package dev.lisovskiy.meal_planner_api.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Recipe {
    private Long id;
    private String title;
    private String instructions;
    private int prepTimeMinutes;
    private List<RecipeIngredient> recipeIngredients;
}
