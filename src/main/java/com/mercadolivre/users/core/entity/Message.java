package com.mercadolivre.users.core.entity;

import lombok.Getter;

@Getter
public enum Message {
  REGISTRATION_ERROR_AGE_BELOW_X(
      "AGE_BELOW_X", "Registration is only allowed to users aged 18 and above."),
  REGISTRATION_ERROR_USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User already exists."),
  REGISTRATION_ERROR_CPF_INVALID("INVALID_CPF", "BrazilianCPF is invalid."),

  ERROR_TEMPLATE_USER_NOT_FOUND("USER_NOT_FOUND", "User %s not found.");

  private String code;
  private String message;

  Message(final String code, final String message) {
    this.code = code;
    this.message = message;
  }
}
