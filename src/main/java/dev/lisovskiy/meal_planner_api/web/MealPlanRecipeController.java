package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.CreateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanListDto;
import dev.lisovskiy.meal_planner_api.service.core.recipe_plan.RecipePlanService;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipePlanWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meal-plans/{mealPlanId}/recipe-plans")
@RequiredArgsConstructor
public class MealPlanRecipeController {

    private final RecipePlanService recipePlanService;
    private final RecipePlanWebMapper recipePlanWebMapper;

    @GetMapping
    public ResponseEntity<RecipePlanListDto> getAllRecipePlansByMealPlanId(@PathVariable Long mealPlanId) {
        List<RecipePlan> recipePlans = recipePlanService.getAllRecipePlansByMealPlanId(mealPlanId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RecipePlanListDto(
                        recipePlans.stream()
                                .map(recipePlanWebMapper::toRecipePlanDto)
                                .toList()
                ));
    }

    @PostMapping
    public ResponseEntity<RecipePlanDto> createRecipePlan(
            @PathVariable Long mealPlanId,
            @RequestBody @Valid CreateRecipePlanDto createRecipePlanDto
    ) {
        RecipePlan recipePlan = recipePlanService.createRecipePlan(mealPlanId, createRecipePlanDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipePlanWebMapper.toRecipePlanDto(recipePlan));
    }
}
