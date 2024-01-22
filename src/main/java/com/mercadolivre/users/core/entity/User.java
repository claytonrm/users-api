package com.mercadolivre.users.core.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class User {

    private final String id;
    private final String name;
    private final String cpf;
    private final LocalDate birthDate;
    private final LocalDateTime createdAt;

    public User(final String name, final String cpf, final LocalDate birthDate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
    }

    public boolean hasValidAge(final int allowedMinimalAge) {
        final Period period = Period.between(this.birthDate, LocalDate.now());
        return period.getYears() >= allowedMinimalAge;
    }
}
