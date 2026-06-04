package dev.lisovskiy.meal_planner_api.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MealPlan {
    private Long id;
    private List<RecipePlan> recipePlans;
    private Date date;
}
