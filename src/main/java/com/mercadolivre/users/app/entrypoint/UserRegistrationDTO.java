package com.mercadolivre.users.app.entrypoint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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

  public User toUserModel() {
    return new User(
            this.name,
            new BrazilianCPF(this.cpf),
            this.email,
            this.birthDate
    );
  }

}
