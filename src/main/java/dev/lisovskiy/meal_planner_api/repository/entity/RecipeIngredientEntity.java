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

    @EmbeddedId
    private RecipeIngredientId id = new RecipeIngredientId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id",  nullable = false)
    private IngredientEntity ingredient;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private IngredientUnit unit;

    @Column(name = "quantity")
    private Double quantity;
}
