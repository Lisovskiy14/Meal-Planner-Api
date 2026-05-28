package dev.lisovskiy.meal_planner_api.util;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RecipeIngredientDtoSnippetProvider {

    public static List<FieldDescriptor> getRecipeIngredientDtoFields() {
        return getRecipeIngredientDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getRecipeIngredientDtoFieldsWithPrefix(String prefix) {
        String p = prefix.isBlank() ? "" : prefix + '.';

        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath(p + "ingredient")
                        .type(JsonFieldType.OBJECT)
                        .description("Target ingredient"),
                fieldWithPath(p + "unit").description("Ingredient unit of measure"),
                fieldWithPath(p + "quantity").description("Quantity of ingredient by its unit")
        );

        Stream<FieldDescriptor> nestedIngredientFields = IngredientDtoSnippetsProvider
                .getIngredientDtoFieldsWithPrefix(prefix + "ingredient").stream();

        return Stream.concat(rootFields, nestedIngredientFields)
                .toList();
    }
}
