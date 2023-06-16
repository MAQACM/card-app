package com.app.cards.models.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Pagination {
    private int totalPages;
    private int pageNumber;
    private int numberOfElements;
    private long totalElements;
}
