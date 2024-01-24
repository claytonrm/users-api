package com.mercadolivre.users.core.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends IllegalStateException {

  private String code;

  public NotFoundException(final String code, final String message) {
    super(message);
    this.code = code;
  }

}
