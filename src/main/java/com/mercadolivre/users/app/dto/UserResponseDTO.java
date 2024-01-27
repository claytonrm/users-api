package com.mercadolivre.users.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mercadolivre.users.core.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class UserResponseDTO {

  private String id;
  private String name;
  private String cpf;
  private String email;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate birthDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime updatedAt;

  public UserResponseDTO(final User userModel) {
    this.id = userModel.getId();
    this.name = userModel.getName();
    this.cpf = userModel.getCpf().getFormatted();
    this.email = userModel.getEmail();
    this.birthDate = userModel.getBirthDate();
    this.createdAt = userModel.getCreatedAt();
    this.updatedAt = userModel.getUpdatedAt();
  }

}
