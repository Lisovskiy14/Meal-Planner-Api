package dev.lisovskiy.meal_planner_api.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_seq_gen")
    @SequenceGenerator(
            name = "recipe_seq_gen",
            sequenceName = "recipe_seq"
    )
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "instructions", nullable = false)
    private String instructions;

    @Column(name = "prep_time_minutes",  nullable = false)
    private int prepTimeMinutes;

    @OneToMany(
            mappedBy = "recipe",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<RecipeIngredientEntity> recipeIngredients = new ArrayList<>();
}
