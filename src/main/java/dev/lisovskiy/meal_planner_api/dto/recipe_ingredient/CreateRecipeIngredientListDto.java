package dev.lisovskiy.meal_planner_api.dto.recipe_ingredient;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.List;

@Value
public class CreateRecipeIngredientListDto {
    @Valid
    @NotEmpty
    List<CreateRecipeIngredientDto> createDtoList;
}
