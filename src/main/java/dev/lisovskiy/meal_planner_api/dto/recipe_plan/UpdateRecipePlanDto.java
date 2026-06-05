package dev.lisovskiy.meal_planner_api.dto.recipe_plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.lisovskiy.meal_planner_api.dto.validation.ValidLongId;
import dev.lisovskiy.meal_planner_api.dto.validation.ValidTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UpdateRecipePlanDto {

    @NotNull(message = "is required")
    @ValidLongId
    Long mealPlanId;

    @NotNull(message = "is required")
    @ValidLongId
    Long recipeId;

    @Size(max = 200, message = "must be 200 characters length max")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @ValidTime
    String time;
}
