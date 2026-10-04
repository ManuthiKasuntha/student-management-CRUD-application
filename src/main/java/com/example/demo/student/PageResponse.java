package com.example.demo.student;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * What the API returns for a list of students: one page of results
 * plus the information the web page needs to show "Page 2 of 5".
 *
 * A "record" is a short way to write a class that only holds data.
 * Java creates the constructor and getters automatically.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
