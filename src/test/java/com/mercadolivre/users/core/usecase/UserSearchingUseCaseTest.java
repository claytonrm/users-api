package com.mercadolivre.users.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.LogicalOperator;
import com.mercadolivre.users.core.entity.SearchType;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.NotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UserSearching] Unit Tests")
class UserSearchingUseCaseTest {

  @InjectMocks
  private UserSearching userSearching;

  @Mock
  private AccountRepository<User, UserFilter> accountRepository;

  @Test
  @DisplayName("Should find all existing users")
  void shouldFindAllUsers() {
    final List<User> existingUsersSample = List.of(
        new User(UUID.randomUUID().toString(), "Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(1990, 1, 21)),
        new User(UUID.randomUUID().toString(), "Jean", new BrazilianCPF("43874170993"), "jean@jean.com", LocalDate.of(1991, 2, 10))
    );
    given(accountRepository.find(any())).willReturn(existingUsersSample);

    final List<User> actualUsers = userSearching.searchBy(UserFilter.builder().build());

    assertThat(actualUsers).containsExactlyInAnyOrderElementsOf(existingUsersSample);
  }

  @Test
  @DisplayName("Should return an empty list when there is no users")
  void shouldReturnEmptyListWhereThereIsNoUsers() {
    given(accountRepository.find(any())).willReturn(Collections.emptyList());

    final List<User> actualUsers = userSearching.searchBy(UserFilter.builder().name("").build());

    assertThat(actualUsers).isEmpty();
  }

  @Test
  @DisplayName("Should find by name")
  void shouldFindUsersByName() {
    final List<User> existingUsersSample = List.of(
        new User(UUID.randomUUID().toString(), "Billy Joe", new BrazilianCPF("86371844563"), "billy@joe.com", LocalDate.of(1990, 1, 21)),
        new User(UUID.randomUUID().toString(), "Jean Grey", new BrazilianCPF("43874170993"), "jean@grey.com", LocalDate.of(1991, 2, 10)),
        new User(UUID.randomUUID().toString(), "Jean Grey", new BrazilianCPF("09983892936"), "jean@jean.com", LocalDate.of(1991, 2, 10))
    );
    final List<User> mockedUsersFromRepository = List.of(existingUsersSample.get(1), existingUsersSample.get(2));
    given(accountRepository.find(UserFilter.builder().name("jean grey").build())).willReturn(mockedUsersFromRepository);

    final List<User> actualUsers = userSearching.searchBy(UserFilter.builder().name("jean grey").build());

    final UserFilter expectedUserFilterWithDefaultConfig = UserFilter.builder().name("jean grey").operator(LogicalOperator.AND).type(SearchType.EQUALS).normalize(true).build();
    verify(accountRepository).find(expectedUserFilterWithDefaultConfig);
    assertThat(actualUsers).containsExactlyInAnyOrderElementsOf(mockedUsersFromRepository);
  }

  @Test
  @DisplayName("Should find existing user by id")
  void shouldFindUserById() {
    final String sampleId = UUID.randomUUID().toString();
    final User existingUserSample = new User(sampleId, "Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(1990, 1, 21));
    given(accountRepository.findById(anyString())).willReturn(Optional.of(existingUserSample));

    final User actualUser = userSearching.findById(sampleId);

    verify(accountRepository).findById(sampleId);
    assertThat(actualUser).isEqualTo(existingUserSample);
  }

  @Test
  @DisplayName("Should throw a NotFoundException when user is not found")
  void shouldThrowNotFoundExceptionWhenUserIsNotFound() {
    given(accountRepository.findById(anyString())).willReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userSearching.findById(UUID.randomUUID().toString()));
  }


}
