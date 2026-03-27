package com.buildsmart.projectmanager.exception;

import com.buildsmart.common.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component("projectManagerExceptionHandler")
@RestControllerAdvice(basePackages = "com.buildsmart.projectmanager")
public class ProjectManagerExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(ProjectValidationException.class)
    public ResponseEntity<Map<String, Object>> handleProjectValidation(ProjectValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TaskValidationException.class)
    public ResponseEntity<Map<String, Object>> handleTaskValidation(TaskValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidUserDepartmentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDepartment(InvalidUserDepartmentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}