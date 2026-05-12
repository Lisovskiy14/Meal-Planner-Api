package dev.lisovskiy.meal_planner_api.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredient_seq_gen")
    @SequenceGenerator(
            name = "ingredient_seq_gen",
            sequenceName = "ingredient_seq"
    )
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}
