package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.IngredientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientEntityMapper {
    Ingredient toIngredient(IngredientEntity ingredientEntity);
    IngredientEntity toIngredientEntity(Ingredient ingredient);

}
