package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.service.exception.not_found.NotFoundException;
import dev.lisovskiy.meal_planner_api.web.exception.ParamsValidationDetails;
import dev.lisovskiy.meal_planner_api.web.exception.ProblemDetailBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ParamsValidationDetails> errors = ex.getFieldErrors().stream()
                .map(fieldError -> ParamsValidationDetails.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();

        ProblemDetail problemDetail = ProblemDetailBuilder.initBuilding()
                .status(HttpStatus.BAD_REQUEST)
                .type(URI.create("urn:problem-type:validation-error"))
                .title("Validation Error")
                .detail("Validation error has occurred")
                .property("errors", errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problemDetail = ProblemDetailBuilder.initBuilding()
                .status(HttpStatus.BAD_REQUEST)
                .type(URI.create("urn:problem-type:bad-request"))
                .title("Bad Request")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetailBuilder.initBuilding()
                .status(HttpStatus.NOT_FOUND)
                .type(URI.create("urn:problem-type:not-found"))
                .title("Resource Not Found")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleInternalServerError(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetailBuilder.initBuilding()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .type(URI.create("urn:problem-type:internal-server-error"))
                .title("Internal Server Error")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }
}
