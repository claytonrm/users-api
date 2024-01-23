package com.mercadolivre.users.core.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class UserFilter {

  @Builder.Default
  private LogicalOperator operator = LogicalOperator.AND;

  private String cpf;
  private String name;
  private String email;

}
