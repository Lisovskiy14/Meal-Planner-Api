package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.UpdateRecipePlanDto;
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
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;
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

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipePlanById - Should Update and Return")
    public void  updateRecipePlanById_shouldUpdateAndReturn() {
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

        RecipePlanEntity recipePlanEntity = RecipePlanEntity.builder()
                .mealPlan(mealPlanEntity)
                .recipe(recipeEntity)
                .description("Description of RecipePlan 1")
                .time(LocalTime.of(14, 0))
                .build();
        recipePlanEntity = recipePlanRepository.save(recipePlanEntity);

        Long recipePlanId = recipePlanEntity.getId();

        String updatedDescription = "Updated Description of RecipePlan 1";
        LocalTime updatedTime = LocalTime.of(16, 0);

        UpdateRecipePlanDto updateRecipePlanDto = new UpdateRecipePlanDto(
                mealPlanEntity.getId(),
                recipeEntity.getId(),
                updatedDescription,
                updatedTime.toString()
        );

        Recipe recipe = recipeEntityMapper.toRecipe(recipeEntity);
        recipe.setRecipeIngredients(new ArrayList<>());

        RecipePlanDto expectedResult = new RecipePlanDto(
                recipePlanId,
                recipeWebMapper.toRecipeDto(recipe),
                updatedDescription,
                updatedTime
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipe-plans/{id}", recipePlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateRecipePlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipePlanDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipePlanDto.class, objectMapper);

        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        RecipePlanEntity dbResult = recipePlanRepository.findById(recipePlanId).get();
        assertThat(dbResult.getDescription())
                .isEqualTo(updatedDescription);
        assertThat(dbResult.getTime())
            .isEqualTo(updatedTime);

        // Document
        resultActions.andDo(document("update-recipe-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update recipe plan")
                                .description("Finds and updates recipe plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe plan.")
                                )
                                .requestSchema(Schema.schema("UpdateRecipePlan"))
                                .requestFields(
                                    RecipePlanDtoSnippetProvider.getUpdateRecipePlanDtoFields()
                                )
                                .responseSchema(Schema.schema("RecipePlanDto"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipePlanById - Should Return 400 Bad Request")
    public void  updateRecipePlanById_shouldReturn400BadRequest() {
        // Arrange
        UpdateRecipePlanDto updateRecipePlanDto = new UpdateRecipePlanDto(
                0L,
                0L,
                "d".repeat(201),
                "wrong time format"
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipe-plans/{id}", 1)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipePlanDto))
        );

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isBadRequest());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper);

        assertThat(actualResult.getTitle())
                .isEqualTo(expectedResultTitle);
        assertThat(actualResult.getProperties())
                .hasFieldOrProperty(expectedPropertyExisting);

        // Document
        resultActions.andDo(document("update-recipe-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update recipe plan")
                                .description("Finds and updates recipe plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe plan.")
                                )
                                .requestSchema(Schema.schema("UpdateRecipePlan"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipePlanById - Should Return 404 Not Found")
    public void  updateRecipePlanById_shouldReturn404NotFound() {
        // Arrange
        UpdateRecipePlanDto updateRecipePlanDto = new UpdateRecipePlanDto(
                1L,
                1L,
                "Updated Description of RecipePlan 1",
                "18:30"
        );

        Long unexistingRecipePlanId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipe-plans/{id}", unexistingRecipePlanId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipePlanDto))
        );

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(recipePlanRepository.existsById(unexistingRecipePlanId))
                .isFalse();

        // Document
        resultActions.andDo(document("update-recipe-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update recipe plan")
                                .description("Finds and updates recipe plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe plan.")
                                )
                                .requestSchema(Schema.schema("UpdateRecipePlan"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipePlanById - Should Return 409 Conflict")
    public void  updateRecipePlanById_shouldReturn409Conflict() {
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

        RecipePlanEntity recipePlanEntity = RecipePlanEntity.builder()
                .mealPlan(mealPlanEntity)
                .recipe(recipeEntity)
                .description("Description of RecipePlan 2")
                .time(LocalTime.of(11,30))
                .build();
        recipePlanEntity = recipePlanRepository.save(recipePlanEntity);

        Long recipePlanId = recipePlanEntity.getId();

        UpdateRecipePlanDto updateRecipePlanDto = new UpdateRecipePlanDto(
                mealPlanEntity.getId(),
                recipeEntity.getId(),
                anotherRecipePlanEntity.getDescription(),
                sameLocalTime.toString()
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipe-plans/{id}", recipePlanId)
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

        recipePlanEntity = recipePlanRepository.findById(recipePlanId).get();
        assertThat(recipePlanEntity.getTime())
                .isNotEqualTo(sameLocalTime);

        // Document
        resultActions.andDo(document("update-recipe-plan-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update recipe plan")
                                .description("Finds and updates recipe plan by its ID.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe plan.")
                                )
                                .requestSchema(Schema.schema("UpdateRecipePlan"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

}
