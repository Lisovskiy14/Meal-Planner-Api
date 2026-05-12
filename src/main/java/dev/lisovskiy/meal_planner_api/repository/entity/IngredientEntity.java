package dev.lisovskiy.meal_planner_api.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredient_seq_gen")
    @SequenceGenerator(
            name = "ingredient_seq_gen",
            sequenceName = "ingredient_seq"
    )
    private Long id;
}
