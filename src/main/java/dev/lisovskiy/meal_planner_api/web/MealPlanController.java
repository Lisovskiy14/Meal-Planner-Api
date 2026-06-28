package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.MealPlan;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.CreateMealPlanDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.MealPlanDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.MealPlanListDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.UpdateMealPlanDto;
import dev.lisovskiy.meal_planner_api.service.core.meal_plan.MealPlanService;
import dev.lisovskiy.meal_planner_api.web.mapper.MealPlanWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final MealPlanWebMapper mealPlanWebMapper;

    @GetMapping
    public ResponseEntity<MealPlanListDto> getAllMealPlans() {
        List<MealPlan> mealPlans = mealPlanService.getAllMealPlans();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MealPlanListDto(
                        mealPlans.stream()
                                .map(mealPlanWebMapper::toMealPlanDto)
                                .toList()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealPlanDto> getMealPlanById(@PathVariable Long id) {
        MealPlan mealPlan = mealPlanService.getMealPlanById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mealPlanWebMapper.toMealPlanDto(mealPlan));
    }

    @PostMapping
    public ResponseEntity<MealPlanDto> createMealPlan(
            @RequestBody @Valid CreateMealPlanDto createMealPlanDto
    ) {
        MealPlan mealPlan = mealPlanService.createMealPlan(createMealPlanDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mealPlanWebMapper.toMealPlanDto(mealPlan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealPlanDto> updateMealPlanById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMealPlanDto updateMealPlanDto
    ) {
        MealPlan mealPlan = mealPlanService.updateMealPlanById(id, updateMealPlanDto);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mealPlanWebMapper.toMealPlanDto(mealPlan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealPlanById(@PathVariable Long id) {
        mealPlanService.deleteMealPlanById(id);
        return ResponseEntity.noContent().build();
    }
}
