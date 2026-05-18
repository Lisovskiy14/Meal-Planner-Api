package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredientEntity, RecipeIngredientId> {
    Collection<RecipeIngredientEntity> findAllByRecipe_Id(Long recipeId);
}
