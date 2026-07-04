package dev.lisovskiy.meal_planner_api.web.mapper;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeSummaryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipeIngredientWebMapper.class})
public interface RecipeWebMapper {
    RecipeDto toRecipeDto(Recipe recipe);
    RecipeSummaryDto toRecipeSummaryDto(Recipe recipe);
}
