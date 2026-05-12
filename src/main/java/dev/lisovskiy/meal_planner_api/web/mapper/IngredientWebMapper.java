package dev.lisovskiy.meal_planner_api.web.mapper;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientWebMapper {
    IngredientDto toIngredientDto(Ingredient ingredient);
}
