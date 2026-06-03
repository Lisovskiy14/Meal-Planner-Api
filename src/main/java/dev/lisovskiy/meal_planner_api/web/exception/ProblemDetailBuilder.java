package dev.lisovskiy.meal_planner_api.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ProblemDetailBuilder {
    private HttpStatus status;
    private URI type;
    private String title;
    private String detail;
    private final Map<String, Object > properties = new HashMap<>();

    public static ProblemDetailBuilder initBuilding() {
        return new ProblemDetailBuilder();
    }

    public ProblemDetailBuilder status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ProblemDetailBuilder type(URI type) {
        this.type = type;
        return this;
    }

    public ProblemDetailBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProblemDetailBuilder detail(String detail) {
        this.detail = detail;
        return this;
    }

    public ProblemDetailBuilder property(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public ProblemDetailBuilder properties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public ProblemDetail build() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(type);
        problemDetail.setTitle(title);

        if (!properties.isEmpty()) {
            problemDetail.setProperties(properties);
        }

        return problemDetail;
    }
}
