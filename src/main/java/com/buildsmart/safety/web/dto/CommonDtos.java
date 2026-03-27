package com.buildsmart.safety.web.dto;

import java.util.List;

public class CommonDtos {

    public record PageResponse<T>(
            List<T> content,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {}

    public record ApiError(String code, String message) {}
}