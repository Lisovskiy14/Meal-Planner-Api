package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.repository.MealPlanRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipePlanRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipePlanEntity;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.RecipeIngredientDtoSnippetProvider;
import dev.lisovskiy.meal_planner_api.util.RecipePlanDtoSnippetProvider;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeWebMapper;
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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("RecipePlanController IT")
public class RecipePlanControllerIT extends AbstractIT {

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

    @Autowired
    private RecipeEntityMapper recipeEntityMapper;

    @Autowired
    private RecipeWebMapper recipeWebMapper;

    @AfterEach
    public void cleanUp() {
        recipePlanRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipePlanById - Should Return RecipePlan")
    public void getRecipePlanById_shouldReturnRecipePlan() {
        // Arrange
        MealPlanEntity mealPlanEntity = MealPlanEntity.builder()
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
        mealPlanRepository.save(mealPlanEntity);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(15)
                .build();
        recipeRepository.save(recipeEntity);

        String description = "Description of RecipePlan 1";
        LocalTime time = LocalTime.of(14, 0);

        RecipePlanEntity recipePlanEntity = RecipePlanEntity.builder()
                .mealPlan(mealPlanEntity)
                .recipe(recipeEntity)
                .description(description)
                .time(time)
                .build();
        recipePlanRepository.save(recipePlanEntity);

        Long recipePlanId = recipePlanEntity.getId();

        RecipePlanDto expectedResult = new RecipePlanDto(
                recipePlanId,
                recipeWebMapper.toRecipeDto(recipeEntityMapper.toRecipe(
                        recipeEntity
                )),
                description,
                time
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipe-plans/{id}", recipePlanId)
                        .accept(APPLICATION_JSON_VALUE)
        );

        // Assert
        resultActions.andExpect(status().isOk());

        MvcResult mvcResult = resultActions.andReturn();

        RecipePlanDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipePlanDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        // Document
        resultActions.andDo(document("get-recipe-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get recipe plan")
                                .description("Finds and returns recipe plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe plan.")
                                )
                                .responseSchema(Schema.schema("RecipePlanDto"))
                                .responseFields(
                                        RecipePlanDtoSnippetProvider.getRecipePlanDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipePlanById - Should Return 404 Not Found")
    public void getRecipePlanById_shouldReturn404NotFound() {
        // Arrange
        Long recipePlanId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipe-plans/{id}", recipePlanId)
                        .accept(APPLICATION_PROBLEM_JSON)
        );

        // Assert
        resultActions.andExpect(status().isNotFound());

        // Document
        resultActions.andDo(document("get-recipe-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get recipe plan")
                                .description("Finds and returns recipe plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe plan.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

}
