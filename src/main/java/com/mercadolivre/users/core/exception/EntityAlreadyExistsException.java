package com.mercadolivre.users.core.exception;

public class EntityAlreadyExistsException extends IllegalStateException {

  private String code;

  public EntityAlreadyExistsException(final String code, final String message) {
    super(message);
    this.code = code;
  }
}
