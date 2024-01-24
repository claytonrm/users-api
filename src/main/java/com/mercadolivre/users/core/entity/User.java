package com.mercadolivre.users.core.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class User {

    private String id;
    private final String name;
    private final BrazilianCPF cpf;
    private final String email;
    private final LocalDate birthDate;
    private final LocalDateTime createdAt;

    public User(final String name, final BrazilianCPF cpf, final String email, final LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
    }

    public User(final String id, final String name, final BrazilianCPF cpf, final String email, final LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isValidAge(final int allowedMinimalAge) {
        final Period period = Period.between(this.birthDate, LocalDate.now());
        return period.getYears() >= allowedMinimalAge;
    }

}

