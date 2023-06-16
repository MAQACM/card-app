package com.app.cards.models.payloads;


import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CardQueryParams {
    private String name;
    private int limit;
    private int offset;
    private String color;
    private CardStatus status;
    private Instant createdAt;
    private SortBy sortBy;
}
