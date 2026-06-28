package dev.lisovskiy.meal_planner_api.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
public class MealPlan {
    private Long id;
    private String description;
    private List<RecipePlan> recipePlans;
    private DayOfWeek dayOfWeek;
}
