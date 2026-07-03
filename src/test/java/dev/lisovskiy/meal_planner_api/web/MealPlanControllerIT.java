package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.*;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.repository.MealPlanRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.service.mapper.MealPlanEntityMapper;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.MealPlanDtoSnippetProvider;
import dev.lisovskiy.meal_planner_api.web.mapper.MealPlanWebMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
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

    @Autowired
    private MealPlanEntityMapper mealPlanEntityMapper;

    @Autowired
    private MealPlanWebMapper mealPlanWebMapper;

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

        MealPlanSummaryDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, MealPlanSummaryDto.class, objectMapper
        );

        MealPlanSummaryDto expectedResult = new MealPlanSummaryDto(
                actualResult.getId(),
                description,
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
                                .responseSchema(Schema.schema("MealPlanSummaryDto"))
                                .responseFields(
                                        MealPlanDtoSnippetProvider.getMealPlanSummaryDtoFieldsWithPrefix("")
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateMealPlan - Should Return 400 Bad Request")
    public void createMealPlan_shouldReturn400BadRequest() {
        // Arrange
        CreateMealPlanDto createMealPlanDto = new CreateMealPlanDto(
                "d".repeat(501), "Not Valid Day Of Week"
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/meal-plans")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createMealPlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isBadRequest());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getTitle())
                .isEqualTo(expectedResultTitle);
        assertThat(actualResult.getProperties())
                .hasFieldOrProperty(expectedPropertyExisting);

        assertThat(mealPlanRepository.count())
                .isEqualTo(0);

        // Document
        resultActions.andDo(document("create-meal-plan-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create meal plan.")
                                .description("Creates and returns new meal plan.")
                                .requestSchema(Schema.schema("CreateMealPlanDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateMealPlanById - Should Update And Return MealPlan")
    public void updateMealPlanById_shouldUpdateAndReturnMealPlan() {
        // Arrange
        MealPlanEntity existingMealPlanEntity = MealPlanEntity.builder()
                .description("Old Description")
                .dayOfWeek(DayOfWeek.MONDAY)
                .build();
        existingMealPlanEntity = mealPlanRepository.save(existingMealPlanEntity);

        Long mealPlanId = existingMealPlanEntity.getId();
        String newDescription = "New Description";
        DayOfWeek newDayOfWeek = DayOfWeek.TUESDAY;

        UpdateMealPlanDto createMealPlanDto = new UpdateMealPlanDto(
                newDescription, newDayOfWeek.toString()
        );

        MealPlanSummaryDto expectedResult = new MealPlanSummaryDto(
                mealPlanId,
                newDescription,
                newDayOfWeek
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/meal-plans/{id}", mealPlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createMealPlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        MealPlanSummaryDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, MealPlanSummaryDto.class, objectMapper
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        MealPlanSummaryDto dbResult = mealPlanWebMapper.toMealPlanSummaryDto(mealPlanEntityMapper.toMealPlan(
                mealPlanRepository.findById(mealPlanId).get()
        ));
        assertThat(dbResult)
                .isEqualTo(expectedResult);

        // Document
        resultActions.andDo(document("update-meal-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update meal plan By Id.")
                                .description("Updates and returns updated meal plan.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target MealPlan.")
                                )
                                .requestSchema(Schema.schema("UpdateMealPlanDto"))
                                .requestFields(
                                        MealPlanDtoSnippetProvider.getCreateOrUpdateMealPlanDtoFields()
                                )
                                .responseSchema(Schema.schema("MealPlanSummaryDto"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateMealPlanById - Should Return 400 Bad Request")
    public void updateMealPlanById_shouldReturn400BadRequest() {
        // Arrange
        UpdateMealPlanDto createMealPlanDto = new UpdateMealPlanDto(
                "d".repeat(501), "Not Valid Day Of Week"
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/meal-plans/{id}", 1L)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createMealPlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isBadRequest());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getTitle())
                .isEqualTo(expectedResultTitle);
        assertThat(actualResult.getProperties())
                .hasFieldOrProperty(expectedPropertyExisting);

        // Document
        resultActions.andDo(document("update-meal-plan-by-id-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update meal plan By Id.")
                                .description("Updates and returns updated meal plan.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target MealPlan.")
                                )
                                .requestSchema(Schema.schema("UpdateMealPlanDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateMealPlanById - Should Return 404 Not Found")
    public void updateMealPlanById_shouldReturn404NotFound() {
        // Arrange
        UpdateMealPlanDto createMealPlanDto = new UpdateMealPlanDto(
                "New Description", "TUESDAY"
        );

        Long unexistingMealPlanId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/meal-plans/{id}", unexistingMealPlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createMealPlanDto))
        );

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(mealPlanRepository.existsById(unexistingMealPlanId))
                .isFalse();

        // Document
        resultActions.andDo(document("update-meal-plan-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update meal plan By Id.")
                                .description("Updates and returns updated meal plan.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target MealPlan.")
                                )
                                .requestSchema(Schema.schema("UpdateMealPlanDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("DeleteMealPlanById - Should Delete MealPlan")
    public void deleteMealPlanById_shouldDeleteMealPlan() {
        // Arrange
        MealPlanEntity existingMealPlanEntity = MealPlanEntity.builder()
                .description("Old Description")
                .dayOfWeek(DayOfWeek.MONDAY)
                .build();
        existingMealPlanEntity = mealPlanRepository.save(existingMealPlanEntity);

        Long mealPlanId = existingMealPlanEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/meal-plans/{id}", mealPlanId)
                        .accept(APPLICATION_JSON_VALUE)
        );

        // Assert
        resultActions.andExpect(status().isNoContent());

        assertThat(mealPlanRepository.existsById(mealPlanId))
                .isFalse();

        // Document
        resultActions.andDo(document("delete-meal-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Delete meal plan By Id.")
                                .description("Deletes existing meal plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target MealPlan.")
                                )
                                .build()
                )
        ));
    }
}
