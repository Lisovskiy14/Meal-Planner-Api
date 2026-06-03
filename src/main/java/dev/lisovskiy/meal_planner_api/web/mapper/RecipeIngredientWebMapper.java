package dev.lisovskiy.meal_planner_api.web.mapper;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {IngredientWebMapper.class})
public interface RecipeIngredientWebMapper {
    RecipeIngredientDto toRecipeIngredientDto(RecipeIngredient recipeIngredient);
}
