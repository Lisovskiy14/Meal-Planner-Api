package dev.lisovskiy.meal_planner_api.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class RecipePlan {
    private Long id;
    private Recipe recipe;
    private String description;
    private LocalTime time;
}
