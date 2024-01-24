package com.mercadolivre.users.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.NotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UserSearchById] Unit Tests")
public class UserSearchByIdUseCaseTest {

  @InjectMocks
  private UserSearchById userSearchById;

  @Mock
  private AccountRepository<User, UserFilter> accountRepository;

  @Test
  @DisplayName("Should find existing user by id")
  void shouldFindUserById() {
    final String sampleId = UUID.randomUUID().toString();
    final User existingUserSample = new User(sampleId, "Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(1990, 01, 21));
    given(accountRepository.findById(anyString())).willReturn(Optional.ofNullable(existingUserSample));

    final User actualUser = userSearchById.findById(sampleId);

    verify(accountRepository).findById(sampleId);
    assertThat(actualUser).isEqualTo(existingUserSample);
  }

  @Test
  @DisplayName("Should throw a NotFoundException when user is not found")
  void shouldThrowNotFoundExceptionWhenUserIsNotFound() {
    given(accountRepository.findById(anyString())).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userSearchById.findById(UUID.randomUUID().toString()));
  }

}
