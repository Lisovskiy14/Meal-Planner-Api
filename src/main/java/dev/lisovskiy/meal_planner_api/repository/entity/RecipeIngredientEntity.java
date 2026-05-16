package dev.lisovskiy.meal_planner_api.repository.entity;

import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_ingredient_seq_gen")
    @SequenceGenerator(
            name = "recipe_ingredient_seq_gen",
            sequenceName = "recipe_ingredient_seq"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;

    @OneToOne
    @JoinColumn(name = "ingredient_id",  nullable = false)
    private IngredientEntity ingredient;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private IngredientUnit unit;

    @Column(name = "quantity")
    private Double quantity;
}
