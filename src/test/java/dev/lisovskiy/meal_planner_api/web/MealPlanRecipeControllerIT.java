package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanListDto;
import dev.lisovskiy.meal_planner_api.repository.MealPlanRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipePlanRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipePlanEntity;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.RecipePlanDtoSnippetProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MealPlanRecipeController IT")
public class MealPlanRecipeControllerIT extends AbstractIT {

    private final String SCHEMA_TAG = "RecipePlans";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipePlanRepository recipePlanRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @AfterEach
    public void cleanUp() {
        recipePlanRepository.deleteAll();
        mealPlanRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("GetAllRecipePlansByMealPlanId - Should Return List")
    public void getAllRecipePlansByMealPlanId_shouldReturnList() {
        // Arrange
        MealPlanEntity mealPlanEntity = MealPlanEntity.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .build();
        mealPlanEntity = mealPlanRepository.save(mealPlanEntity);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe Title")
                .instructions("Recipe Instructions")
                .prepTimeMinutes(15)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        List<RecipePlanEntity> recipePlanEntities = List.of(
                RecipePlanEntity.builder()
                        .mealPlan(mealPlanEntity)
                        .recipe(recipeEntity)
                        .time(LocalTime.of(9, 0))
                        .build(),
                RecipePlanEntity.builder()
                        .mealPlan(mealPlanEntity)
                        .recipe(recipeEntity)
                        .time(LocalTime.of(12, 0))
                        .build()
        );
        recipePlanEntities = recipePlanRepository.saveAll(recipePlanEntities);

        Long mealPlanId = mealPlanEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/meal-plans/{mealPlanId}/recipe-plans", mealPlanId)
                        .accept(APPLICATION_JSON_VALUE)
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipePlanListDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipePlanListDto.class, objectMapper
        );

        assertThat(actualResult.getRecipePlans())
                .hasSize(recipePlanEntities.size());

        mealPlanEntity = mealPlanRepository.findWithRecipePlansById(mealPlanId).get();
        assertThat(mealPlanEntity.getRecipePlans())
                .extracting(RecipePlanEntity::getId)
                .containsExactlyInAnyOrderElementsOf(
                        actualResult.getRecipePlans().stream()
                                .map(RecipePlanDto::getId)
                                .toList()
                );

        // Document
        resultActions.andDo(document("get-all-recipe-plans-by-meal-plan-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get all recipe plans")
                                .description("Finds and returns all recipe plans by its meal plan ID.")
                                .pathParameters(
                                        parameterWithName("mealPlanId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target meal plan.")
                                )
                                .responseSchema(Schema.schema("RecipePlanListDto"))
                                .responseFields(
                                        RecipePlanDtoSnippetProvider.getRecipePlanListDtoFields()
                                )
                                .build()
                )
        ));
    }
}
