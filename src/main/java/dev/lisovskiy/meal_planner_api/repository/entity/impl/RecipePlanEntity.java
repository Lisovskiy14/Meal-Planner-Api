package dev.lisovskiy.meal_planner_api.repository.entity.impl;

import dev.lisovskiy.meal_planner_api.repository.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "recipe_plans", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_recipe_plan_meal_time",
                columnNames = {"meal_plan_id", "time"}
        )
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipePlanEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_plan_seq_gen")
    @SequenceGenerator(
            name = "recipe_plan_seq_gen",
            sequenceName = "recipe_plan_seq"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlanEntity mealPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;

    @Column(name = "description")
    private String description;

    @Column(name = "time")
    private LocalTime time;
}
