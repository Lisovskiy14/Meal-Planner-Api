package dev.lisovskiy.meal_planner_api.web.mapper;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipeIngredientWebMapper.class})
public interface RecipeWebMapper {
    RecipeDto toRecipeDto(Recipe recipe);
}
