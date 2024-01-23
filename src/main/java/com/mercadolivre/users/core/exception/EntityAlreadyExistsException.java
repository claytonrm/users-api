package com.mercadolivre.users.core.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends IllegalStateException {

  private String code;

  public EntityAlreadyExistsException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
