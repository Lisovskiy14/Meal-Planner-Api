package dev.lisovskiy.meal_planner_api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

public class GlobalExtractor {

    public static <T> T getObjectFromMvcResult(
            MvcResult mvcResult, Class<T> clazz,
            ObjectMapper objectMapper
    ) throws Exception {

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, clazz);
    }
}
