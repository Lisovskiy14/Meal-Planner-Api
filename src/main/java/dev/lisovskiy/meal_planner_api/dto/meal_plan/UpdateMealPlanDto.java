package dev.lisovskiy.meal_planner_api.dto.meal_plan;

import dev.lisovskiy.meal_planner_api.dto.validation.ValidDayOfWeek;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UpdateMealPlanDto {

    @Size(max = 500, message = "must be 500 characters length max")
    String description;

    @NotNull
    @ValidDayOfWeek
    String dayOfWeek;
}
