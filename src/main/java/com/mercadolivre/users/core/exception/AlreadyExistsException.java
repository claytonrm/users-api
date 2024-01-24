package com.mercadolivre.users.core.exception;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends IllegalStateException {

  private String code;

  public AlreadyExistsException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
