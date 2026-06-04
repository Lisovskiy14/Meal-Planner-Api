package dev.lisovskiy.meal_planner_api.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_plam_seq_gen")
    @SequenceGenerator(
            name = "meal_plam_seq_gen",
            sequenceName = "meal_plan_seq"
    )
    private Long id;

    @Builder.Default
    @OneToMany(
            mappedBy = "mealPlan",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<RecipePlanEntity> recipePlans = new ArrayList<>();

    @Column(name = "date", nullable = false)
    private Date date;
}
