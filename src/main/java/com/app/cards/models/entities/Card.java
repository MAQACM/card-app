package com.app.cards.models.entities;

import java.sql.Timestamp;
import java.time.Instant;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.app.cards.models.payloads.CardStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

@Table("cards")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    private int id;
    @NotBlank
    private String name;
    @Max(6l)
    private String color;
    private String status;
    private String description;
    private Instant createdAt;
    private long createdBy;
}
