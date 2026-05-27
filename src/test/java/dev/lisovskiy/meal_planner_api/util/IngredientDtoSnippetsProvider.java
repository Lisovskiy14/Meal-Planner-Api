package dev.lisovskiy.meal_planner_api.util;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class IngredientDtoSnippetsProvider {

    public static List<FieldDescriptor> getIngredientDtoFields() {
        return getIngredientDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getIngredientDtoFieldsWithPrefix(String prefix) {
        String p = prefix.isBlank() ? "" : prefix + '.';
        return List.of(
                fieldWithPath(p + "id").description("Identifier"),
                fieldWithPath(p + "name").description("Name of created ingredient"),
                fieldWithPath(p + "description").description("Description of created ingredient")
        );
    }
}
