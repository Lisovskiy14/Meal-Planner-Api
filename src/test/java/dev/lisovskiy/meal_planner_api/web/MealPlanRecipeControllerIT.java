package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.CreateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.UpdateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.repository.MealPlanRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipePlanRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipePlanEntity;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.RecipePlanDtoSnippetProvider;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeWebMapper;
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

    @Autowired
    private RecipeEntityMapper recipeEntityMapper;

    @Autowired
    private RecipeWebMapper recipeWebMapper;

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

    @Test
    @SneakyThrows
    @DisplayName("GetAllRecipePlansByMealPlanId - Should Return 404 Not Found")
    public void getAllRecipePlansByMealPlanId_shouldReturn404NotFound() {
        // Arrange
        Long mealPlanId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/meal-plans/{mealPlanId}/recipe-plans", mealPlanId)
                        .accept(APPLICATION_PROBLEM_JSON)
        );

        // Assert
        resultActions.andExpect(status().isNotFound());

        // Document
        resultActions.andDo(document("get-all-recipe-plans-by-meal-plan-id-not-found",
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
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipePlan - Should Create And Return")
    public void createRecipePlan_shouldCreateAndReturn() {
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

        Long mealPlanId = mealPlanEntity.getId();
        String description = "Description of Recipe Plan 1";
        LocalTime time = LocalTime.of(14, 00);

        CreateRecipePlanDto createRecipePlanDto = new CreateRecipePlanDto(
                recipeEntity.getId(),
                description,
                time.toString()
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/meal-plans/{mealPlanId}/recipe-plans", mealPlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createRecipePlanDto))

        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isCreated());

        RecipePlanDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipePlanDto.class, objectMapper
        );

        Long recipePlanId = actualResult.getId();
        RecipeDto recipeDto = recipeWebMapper.toRecipeDto(recipeEntityMapper.toRecipe(recipeEntity));
        RecipePlanDto expectedResult = new RecipePlanDto(
                recipePlanId,
                recipeDto,
                description,
                time
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        assertThat(recipePlanRepository.existsById(recipePlanId))
                .isTrue();

        // Document
        resultActions.andDo(document("create-recipe-plan",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe plan")
                                .description("Creates and returns recipe plan.")
                                .pathParameters(
                                        parameterWithName("mealPlanId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target meal plan.")
                                )
                                .responseSchema(Schema.schema("RecipePlanDto"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipePlan - Should Return 400 Bad Request")
    public void createRecipePlan_shouldReturn400BadRequest() {
        // Arrange
        CreateRecipePlanDto createRecipePlanDto = new CreateRecipePlanDto(
                0L,
                "d".repeat(201),
                "wrong time pattern"
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/meal-plans/{mealPlanId}/recipe-plans", 1L)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipePlanDto))
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

        assertThat(recipePlanRepository.count())
                .isEqualTo(0);

        // Document
        resultActions.andDo(document("create-recipe-plan-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe plan")
                                .description("Creates and returns recipe plan.")
                                .pathParameters(
                                        parameterWithName("mealPlanId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target meal plan.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipePlan - Should Return 404 Not Found")
    public void createRecipePlan_shouldReturn404NotFound() {
        // Arrange
        Long mealPlanId = 1L;
        Long recipePlanId = 2L;

        CreateRecipePlanDto createRecipePlanDto = new CreateRecipePlanDto(
                mealPlanId,
                "Description of Recipe Plan 1",
                "14:00"
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/meal-plans/{mealPlanId}/recipe-plans", recipePlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipePlanDto))
        );

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(recipePlanRepository.count())
                .isEqualTo(0);

        // Document
        resultActions.andDo(document("create-recipe-plan-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe plan")
                                .description("Creates and returns recipe plan.")
                                .pathParameters(
                                        parameterWithName("mealPlanId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target meal plan.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipePlan - Should Return 409 Conflict")
    public void  createRecipePlan_shouldReturn409Conflict() {
        // Arrange
        MealPlanEntity mealPlanEntity = MealPlanEntity.builder()
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
        mealPlanEntity = mealPlanRepository.save(mealPlanEntity);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(15)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        LocalTime sameLocalTime = LocalTime.of(14, 0);

        RecipePlanEntity anotherRecipePlanEntity = RecipePlanEntity.builder()
                .mealPlan(mealPlanEntity)
                .recipe(recipeEntity)
                .description("Description of RecipePlan 1")
                .time(sameLocalTime)
                .build();
        anotherRecipePlanEntity = recipePlanRepository.save(anotherRecipePlanEntity);

        CreateRecipePlanDto updateRecipePlanDto = new CreateRecipePlanDto(
                recipeEntity.getId(),
                "Description of RecipePlan 2",
                sameLocalTime.toString()
        );

        Long mealPlanId = mealPlanEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/meal-plans/{mealPlanId}/recipe-plans", mealPlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipePlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isConflict());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper);

        assertThat(actualResult.getDetail())
                .matches("^.*[Tt]ime.*already exists.*MealPlan.*$");

        assertThat(recipePlanRepository.count())
                .isEqualTo(1);

        // Document
        resultActions.andDo(document("create-recipe-plan-conflict",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe plan")
                                .description("Creates and returns recipe plan.")
                                .pathParameters(
                                        parameterWithName("mealPlanId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target meal plan.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }
}
