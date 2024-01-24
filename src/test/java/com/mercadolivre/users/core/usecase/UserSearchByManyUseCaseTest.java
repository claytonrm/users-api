package com.mercadolivre.users.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UserSearchByMany] Unit Tests")
public class UserSearchByManyUseCaseTest {

  @InjectMocks
  private UserSearchByMany userSearchByMany;

  @Mock
  private AccountRepository<User, UserFilter> accountRepository;

  @Test
  @DisplayName("Should find all existing users")
  void shouldFindAllUsers() {
    final List<User> existingUsersSample = List.of(
        new User(UUID.randomUUID().toString(), "Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(1990, 01, 21)),
        new User(UUID.randomUUID().toString(), "Jean", new BrazilianCPF("43874170993"), "jean@jean.com", LocalDate.of(1991, 02, 10))
    );
    given(accountRepository.findAll()).willReturn(existingUsersSample);

    final List<User> actualUsers = userSearchByMany.findAll();

    assertThat(actualUsers).containsExactlyInAnyOrderElementsOf(existingUsersSample);
  }

  @Test
  @DisplayName("Should return an empty list when there is no users")
  void shouldReturnEmptyListWhereThereIsNoUsers() {
    given(accountRepository.findAll()).willReturn(Collections.emptyList());

    final List<User> actualUsers = userSearchByMany.findAll();

    assertThat(actualUsers).isEmpty();
  }

  @Test
  @DisplayName("Should find by name")
  void shouldFindUsersByName() {
    final List<User> existingUsersSample = List.of(
        new User(UUID.randomUUID().toString(), "Billy Joe", new BrazilianCPF("86371844563"), "billy@joe.com", LocalDate.of(1990, 01, 21)),
        new User(UUID.randomUUID().toString(), "Jean Grey", new BrazilianCPF("43874170993"), "jean@grey.com", LocalDate.of(1991, 02, 10)),
        new User(UUID.randomUUID().toString(), "Jean Grey", new BrazilianCPF("09983892936"), "jean@jean.com", LocalDate.of(1991, 02, 10))
    );
    final List<User> mockedUsersFromRepository = List.of(existingUsersSample.get(1), existingUsersSample.get(2));
    given(accountRepository.findBy(UserFilter.builder().name("jean grey").build())).willReturn(mockedUsersFromRepository);

    final List<User> actualUsers = userSearchByMany.findBy(UserFilter.builder().name("jean grey").build());

    assertThat(actualUsers).containsExactlyInAnyOrderElementsOf(mockedUsersFromRepository);
  }

}
