package dev.lisovskiy.meal_planner_api.repository.entity.impl;

import dev.lisovskiy.meal_planner_api.repository.entity.BaseEntity;
import dev.lisovskiy.meal_planner_api.web.mapper.MealPlanWebMapper;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredient_seq_gen")
    @SequenceGenerator(
            name = "ingredient_seq_gen",
            sequenceName = "ingredient_seq"
    )
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;
}
