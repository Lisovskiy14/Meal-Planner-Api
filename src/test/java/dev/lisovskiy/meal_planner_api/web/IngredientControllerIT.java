package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.dto.ingredient.CreateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.web.mapper.IngredientWebMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("IngredientController IT")
public class IngredientControllerIT extends AbstractIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientWebMapper ingredientWebMapper;


    @Test
    @SneakyThrows
    @DisplayName("Should Save Category")
    public void shouldSaveIngredient() {
        String name = "Ingredient 1";
        String description = "Description of ingredient 1";

        CreateIngredientDto createIngredientDto = new CreateIngredientDto(
                name,
                description
        );

        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/ingredients")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createIngredientDto)))
                .andExpect(status().isCreated());

        MvcResult mvcResult = resultActions.andReturn();

        IngredientDto actualResult = getObjectFromMvcResult(mvcResult, IngredientDto.class);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();

        IngredientDto expectedResult = new IngredientDto(
                actualResult.getId(),
                name,
                description
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        resultActions.andDo(document("create-ingredient",
                requestFields(
                        fieldWithPath("name").description("Name of new ingredient"),
                        fieldWithPath("description").description("Description of new ingredient")
                ),
                responseFields(
                        fieldWithPath("id").description("Identifier"),
                        fieldWithPath("name").description("Name of created ingredient"),
                        fieldWithPath("description").description("Description of created ingredient")
                )
        ));
    }

    private <T> T getObjectFromMvcResult(MvcResult mvcResult, Class<T> clazz) throws Exception {
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, clazz);
    }
}
