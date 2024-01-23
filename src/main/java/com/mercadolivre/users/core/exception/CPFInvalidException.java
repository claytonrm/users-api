package com.mercadolivre.users.core.exception;

import lombok.Getter;

@Getter
public class CPFInvalidException extends IllegalArgumentException {

  private String code;

  public CPFInvalidException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
