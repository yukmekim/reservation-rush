package dev.reservation.rush.common.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(), 
            page.getNumber(), 
            page.getSize(), 
            page.getTotalElements(), 
            page.getTotalPages(), 
            page.isFirst(), 
            page.isLast(), 
            page.hasNext(), 
            page.hasPrevious()
        );
    }

    public static <T, E> PageResponse<T> from(Page<E> page, Function<E, T> mapper) { 
          return new PageResponse<>(
              page.getContent().stream()
                  .map(mapper)
                  .toList(),
              page.getNumber(),
              page.getSize(),
              page.getTotalElements(),
              page.getTotalPages(),
              page.isFirst(),
              page.isLast(),
              page.hasNext(),
              page.hasPrevious()
          );
      }
}