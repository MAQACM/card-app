package com.app.cards.models.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardCreationBody {
    private long id;
    @NotBlank(message="Name is a required field")
    private String name;
    private String color;
    private CardStatus status;
    private String description;
}
