package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.UpdateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.service.RecipeIngredientService;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeIngredientWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;
    private final RecipeIngredientWebMapper recipeIngredientWebMapper;

    @GetMapping("/{recipeId}/ingredients")
    public ResponseEntity<RecipeIngredientListDto> getAllRecipeIngredients(@PathVariable Long recipeId) {
        List<RecipeIngredient> recipeIngredients = recipeIngredientService.getAllRecipeIngredients(recipeId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RecipeIngredientListDto(
                        recipeIngredients.stream()
                                .map(recipeIngredientWebMapper::toRecipeIngredientDto)
                                .toList()
                ));
    }

    @GetMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<RecipeIngredientDto> getRecipeIngredientById(
            @PathVariable Long recipeId, @PathVariable Long ingredientId
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientService
                .getRecipeIngredientById(recipeId, ingredientId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeIngredientWebMapper.toRecipeIngredientDto(recipeIngredient));
    }

    @PostMapping("/{recipeId}/ingredients")
    public ResponseEntity<RecipeIngredientListDto> createRecipeIngredients(
            @PathVariable Long recipeId,
            @RequestBody @Validated List<CreateRecipeIngredientDto> createRecipeIngredientDtoList
    ) {
        List<RecipeIngredient> recipeIngredients = recipeIngredientService
                .createRecipeIngredients(recipeId, createRecipeIngredientDtoList);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RecipeIngredientListDto(
                        recipeIngredients.stream()
                                .map(recipeIngredientWebMapper::toRecipeIngredientDto)
                                .toList()
                ));
    }

    @PutMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<RecipeIngredientDto> updateRecipeIngredientById(
            @PathVariable Long recipeId, @PathVariable Long ingredientId,
            @RequestBody @Validated UpdateRecipeIngredientDto updateRecipeIngredientDto
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientService
                .updateRecipeIngredientById(
                        recipeId, ingredientId,
                        updateRecipeIngredientDto
                );

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeIngredientWebMapper.toRecipeIngredientDto(recipeIngredient));
    }

    @DeleteMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<Void> deleteRecipeIngredientById(
            @PathVariable Long recipeId, @PathVariable Long ingredientId
    ) {
        recipeIngredientService.deleteRecipeIngredientById(recipeId, ingredientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
