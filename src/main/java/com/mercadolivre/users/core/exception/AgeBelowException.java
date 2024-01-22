package com.mercadolivre.users.core.exception;

import lombok.Getter;

@Getter
public class AgeBelowException extends IllegalArgumentException {

  private String code;

  public AgeBelowException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
