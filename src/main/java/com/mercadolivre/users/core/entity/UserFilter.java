package com.mercadolivre.users.core.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserFilter {

  private String cpf;
  private String name;

}
