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
        String p = validateAndReturnPrefix(prefix);

        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath(p + "ingredient")
                        .type(JsonFieldType.OBJECT)
                        .description("Target ingredient"),
                fieldWithPath(p + "unit").description("Ingredient unit of measure"),
                fieldWithPath(p + "quantity").description("Quantity of ingredient by its unit")
        );

        Stream<FieldDescriptor> nestedIngredientFields = IngredientDtoSnippetsProvider
                .getIngredientDtoFieldsWithPrefix(p + "ingredient").stream();

        return Stream.concat(rootFields, nestedIngredientFields)
                .toList();
    }

    public static List<FieldDescriptor> getCreateRecipeIngredientListDtoFields() {
        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath("createDtoList")
                        .type(JsonFieldType.ARRAY)
                        .description("List of actual create DTOs")
        );

        Stream<FieldDescriptor> nestedCreateDtoFields = getCreateRecipeIngredientDtoFieldsWithPrefix("createDtoList[]")
                .stream();

        return Stream.concat(rootFields, nestedCreateDtoFields)
                .toList();
    }

    public static List<FieldDescriptor> getUpdateRecipeIngredientDtoFields() {
        return getCreateRecipeIngredientDtoFieldsWithPrefix("");
    };

    public static List<FieldDescriptor> getCreateRecipeIngredientDtoFieldsWithPrefix(String prefix) {
        String p = validateAndReturnPrefix(prefix);

        return List.of(
                fieldWithPath(p + "ingredientId")
                        .description("Id of target ingredient"),
                fieldWithPath(p + "unit")
                        .description("Unit of measurement of ingredient"),
                fieldWithPath(p + "quantity")
                    .description("Quantity of ingredient by its unit")
        );
    }

    private static String validateAndReturnPrefix(String prefix) {
        return prefix.isBlank() ? "" : prefix + '.';
    }
}
