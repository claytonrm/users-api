package com.mercadolivre.users.core.entity;

import java.util.stream.IntStream;

public record BrazilianCPF(String number) {

  public boolean isValid() {
    final String cleanedCPF = getRaw();
    final boolean isInvalid = (!isValidLength(cleanedCPF) || isAllDigitsEqual(cleanedCPF));

    if (isInvalid) {
      return false;
    }

    int firstDigit = calculateDigit(cleanedCPF, 8, 10);
    int secondDigit = calculateDigit(cleanedCPF, 9, 11);

    return Character.getNumericValue(cleanedCPF.charAt(9)) == firstDigit
        && Character.getNumericValue(cleanedCPF.charAt(10)) == secondDigit;
  }

  public String getRaw() {
    return this.number.replaceAll("\\D", "");
  }

  private boolean isValidLength(final String cleanedCPF) {
    return cleanedCPF.length() == 11;
  }

  private boolean isAllDigitsEqual(final String cleanedCpf) {
    return cleanedCpf.matches("(\\d)\\1{10}");
  }

  private int calculateDigit(final String cleanedCPF, final int endIndex, final int multiplier) {
    final int sum = IntStream.rangeClosed(0, endIndex)
        .map(i -> Character.getNumericValue(cleanedCPF.charAt(i)) * (multiplier - i))
        .sum();

    final int remainder = sum % 11;
    return (remainder < 2) ? 0 : 11 - remainder;
  }

}
