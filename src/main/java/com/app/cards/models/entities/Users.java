package com.app.cards.models.entities;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Table
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Users {
    @Id
    private long id;
    @NotBlank
    @Email
    private String email;
    private String role;
    @Builder.Default
    private Timestamp createdAt=Timestamp.from(Instant.now());
    private String password;
}
