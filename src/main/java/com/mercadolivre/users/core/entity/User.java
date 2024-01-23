package com.mercadolivre.users.core.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class User {

    private final String name;
    private final String cpf;
    private final String email;
    private final LocalDate birthDate;
    private final LocalDateTime createdAt;

    public User(final String name, final String cpf, final String email, final LocalDate birthDate) {
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

    public boolean isValidCPF() {
        calculateDigit("", 0, 8, 10);
        calculateDigit("", 0, 9, 11);
        return false;
    }

    private boolean isValidLength(final String cleanedCpf) {
        return cleanedCpf.length() == 11;
    }

    private boolean isAllDigitsEqual(final String cleanedCpf) {
        return cleanedCpf.matches("(\\d)\\1{10}");
    }

    private int calculateDigit(final String cleanedCPF, final int startIndex, final int endIndex, final int multiplier) {
        final int sum = IntStream.rangeClosed(startIndex, endIndex)
            .map(i -> Character.getNumericValue(cleanedCPF.charAt(i)) * (multiplier - i))
            .sum();

        final int remainder = sum % 11;
        final int digit = (remainder < 2) ? 0 : 11 - remainder;
        return digit;
    }

}

