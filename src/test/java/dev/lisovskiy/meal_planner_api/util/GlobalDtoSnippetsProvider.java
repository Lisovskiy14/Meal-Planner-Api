package dev.lisovskiy.meal_planner_api.util;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class GlobalDtoSnippetsProvider {

    public static List<FieldDescriptor> getProblemDetailFields() {
        return List.of(
                fieldWithPath("type").description("URI identifier that categorizes the error type"),
                fieldWithPath("title").description("A short, human-readable summary of the problem type"),
                fieldWithPath("status").description("The HTTP status code"),
                fieldWithPath("detail").description("A human-readable explanation specific to this occurrence of the problem"),
                fieldWithPath("instance").description("A URI reference that identifies the specific occurrence of the problem")
        );
    }

    public static List<FieldDescriptor> getProblemDetailFieldsWithErrorsProperty() {
        Stream<FieldDescriptor> generalFields = getProblemDetailFields().stream();

        Stream<FieldDescriptor> propertyFields = Stream.of(
                fieldWithPath("errors")
                        .type(JsonFieldType.ARRAY)
                        .description("A list of occurred errors"),
                fieldWithPath("errors[].field")
                        .description("A field that is an object of error"),
                fieldWithPath("errors[].message")
                        .description("Explaining the error")
        );

        return Stream.concat(generalFields, propertyFields).toList();
    }
}
