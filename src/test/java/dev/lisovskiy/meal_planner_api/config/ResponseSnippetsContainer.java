package dev.lisovskiy.meal_planner_api.config;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ResponseSnippetsContainer {

    public static List<FieldDescriptor> getProblemDetailFields() {
        return List.of(
                fieldWithPath("type").description("URI identifier that categorizes the error type"),
                fieldWithPath("title").description("A short, human-readable summary of the problem type"),
                fieldWithPath("status").description("The HTTP status code"),
                fieldWithPath("detail").description("A human-readable explanation specific to this occurrence of the problem"),
                fieldWithPath("instance").description("A URI reference that identifies the specific occurrence of the problem")
        );
    }
}
