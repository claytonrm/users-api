package com.mercadolivre.users.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class UserRegistrationDTO {

  @NotEmpty
  private String name;
  @NotEmpty
  private String cpf;

  @NotEmpty
  private String email;

  @NotNull
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate birthDate;

  public UserRegistrationDTO(final User user) {
    this.name = user.getName();
    this.cpf = user.getCpf().number();
    this.email = user.getEmail();
    this.birthDate = user.getBirthDate();
  }

  public User toUserModel() {
    return new User(
      this.name,
      new BrazilianCPF(this.cpf),
      this.email,
      this.birthDate
    );
  }

  public User toUserModel(final String id, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
    return new User(
      id,
      this.name,
      new BrazilianCPF(this.cpf),
      this.email,
      this.birthDate,
      createdAt,
      updatedAt
    );
  }

}
