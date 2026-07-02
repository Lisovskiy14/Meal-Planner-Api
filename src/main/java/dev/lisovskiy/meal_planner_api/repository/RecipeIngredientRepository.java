package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeIngredientId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredientEntity, RecipeIngredientId> {

    @EntityGraph(attributePaths = {"ingredient"})
    Collection<RecipeIngredientEntity> findAllWithIngredientsByRecipe_Id(Long recipeId);

    @EntityGraph(attributePaths = {"ingredient"})
    Optional<RecipeIngredientEntity> findWithIngredientById(RecipeIngredientId id);
}
