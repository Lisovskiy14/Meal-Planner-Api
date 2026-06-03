package dev.lisovskiy.meal_planner_api.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ingredient {
    private Long id;
    private String name;
    private String description;
}
