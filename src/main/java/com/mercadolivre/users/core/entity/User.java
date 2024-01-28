package com.mercadolivre.users.core.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {

    private String id;
    private final String name;
    private final BrazilianCPF cpf;
    private final String email;
    private final LocalDate birthDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public User(final String name, final BrazilianCPF cpf, final String email, final LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    public User(final String id, final String name, final BrazilianCPF cpf, final String email, final LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    public User(final String id, final String name, final BrazilianCPF cpf, final String email, final LocalDate birthDate, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isValidAge(final int allowedMinimalAge) {
        final Period period = Period.between(this.birthDate, LocalDate.now());
        return period.getYears() >= allowedMinimalAge;
    }
    
}

