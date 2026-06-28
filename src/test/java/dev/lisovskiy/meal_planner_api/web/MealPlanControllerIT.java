package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.CreateMealPlanDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.MealPlanDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.MealPlanListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.repository.MealPlanRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.MealPlanDtoSnippetProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MealPlanController IT")
public class MealPlanControllerIT extends AbstractIT {

    private final String SCHEMA_TAG = "MealPlans";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @AfterEach
    public void cleanUp() {
        mealPlanRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("GetAllMealPlans - Should Return List")
    public void getAllMealPlans_shouldReturnList() {
        // Arrange
        List<MealPlanEntity> mealPlanEntities = List.of(
                MealPlanEntity.builder()
                        .description("Meal Plan 1")
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .build(),
                MealPlanEntity.builder()
                        .description("Meal Plan 2")
                        .dayOfWeek(DayOfWeek.TUESDAY)
                        .build(),
                MealPlanEntity.builder()
                        .description("Meal Plan 3")
                        .dayOfWeek(DayOfWeek.WEDNESDAY)
                        .build()
        );
        mealPlanEntities = mealPlanRepository.saveAll(mealPlanEntities);

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/meal-plans")
                        .accept(APPLICATION_JSON_VALUE)
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        MealPlanListDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, MealPlanListDto.class, objectMapper
        );

        assertThat(actualResult.getMealPlans())
                .hasSize(mealPlanEntities.size());

        // Document
        resultActions.andDo(document("get-all-meal-plans",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get all meal plans")
                                .description("Finds and returns all existing meal plans.")
                                .responseSchema(Schema.schema("MealPlanListDto"))
                                .responseFields(
                                        MealPlanDtoSnippetProvider.getMealPlanListDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetMealPlanById - Should Return MealPlan")
    public void getMealPlanById_shouldReturnMealPlan() {
        // Arrange
        String expectedDescription = "Meal Plan 1";
        DayOfWeek expectedDayOfWeek = DayOfWeek.MONDAY;

        MealPlanEntity mealPlanEntity = MealPlanEntity.builder()
                .description(expectedDescription)
                .dayOfWeek(expectedDayOfWeek)
                .build();
        mealPlanEntity = mealPlanRepository.save(mealPlanEntity);

        Long mealPlanId = mealPlanEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/meal-plans/{id}", mealPlanId)
                        .accept(APPLICATION_JSON_VALUE)
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        MealPlanDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, MealPlanDto.class, objectMapper
        );

        assertThat(actualResult.getDescription())
                .isEqualTo(expectedDescription);
        assertThat(actualResult.getDayOfWeek())
                .isEqualTo(expectedDayOfWeek);

        // Document
        resultActions.andDo(document("get-meal-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get meal plan by ID.")
                                .description("Finds and returns existing meal plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target MealPlan.")
                                )
                                .responseSchema(Schema.schema("MealPlanDto"))
                                .responseFields(
                                        MealPlanDtoSnippetProvider.getMealPlanDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetMealPlanById - Should Return 404 Not Found")
    public void getMealPlanById_shouldReturn404NotFound() {
        // Arrange
        Long mealPlanId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/meal-plans/{id}", mealPlanId)
                        .accept(APPLICATION_PROBLEM_JSON)
        );

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(mealPlanRepository.existsById(mealPlanId))
                .isFalse();

        // Document
        resultActions.andDo(document("get-meal-plan-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get meal plan by ID.")
                                .description("Finds and returns existing meal plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target MealPlan.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateMealPlan - Should Create And Return MealPlan")
    public void createMealPlan_shouldCreateAndReturnMealPlan() {
        // Arrange
        String description = "Meal Plan 1";
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        List<RecipePlanDto> recipePlans = new ArrayList<>();

        CreateMealPlanDto createMealPlanDto = new CreateMealPlanDto(
                description, dayOfWeek.toString()
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/meal-plans")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createMealPlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isCreated());

        MealPlanDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, MealPlanDto.class, objectMapper
        );

        MealPlanDto expectedResult = new MealPlanDto(
                actualResult.getId(),
                description,
                recipePlans,
                dayOfWeek
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        assertThat(mealPlanRepository.existsById(actualResult.getId()))
                .isTrue();

        // Document
        resultActions.andDo(document("create-meal-plan",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create meal plan.")
                                .description("Creates and returns new meal plan.")
                                .requestSchema(Schema.schema("CreateMealPlanDto"))
                                .requestFields(
                                        MealPlanDtoSnippetProvider.getCreateOrUpdateMealPlanDtoFields()
                                )
                                .responseSchema(Schema.schema("MealPlanDto"))
                                .build()
                )
        ));
    }
}
