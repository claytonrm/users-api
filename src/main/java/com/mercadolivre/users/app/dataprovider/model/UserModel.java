package com.mercadolivre.users.app.dataprovider.model;

import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document("users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserModel {

  private String id;
  private String name;
  private String cpf;
  private String email;
  private LocalDate birthDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UserModel(final User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.cpf = user.getCpf().getRaw();
    this.email = user.getEmail();
    this.birthDate = user.getBirthDate();
    this.createdAt = user.getCreatedAt();
    this.updatedAt = user.getUpdatedAt();
  }

  public User toEntity() {
    return new User(
      this.id,
      this.name,
      new BrazilianCPF(this.cpf),
      this.email,
      this.birthDate,
      this.createdAt,
      this.updatedAt
    );
  }
}
