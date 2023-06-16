package com.app.cards.models.payloads;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserCreation {
    @Id
    private long id;
    @NotBlank
    @Email
    private String emailAddress;
    private URole role;
}
