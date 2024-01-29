package com.mercadolivre.users.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.LogicalOperator;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.AlreadyExistsException;
import com.mercadolivre.users.core.exception.CPFInvalidException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UserRegistration] Unit Tests")
public class UserRegistrationUseCaseTest {

  @InjectMocks
  private UserRegistration userRegistration;

  @Mock
  private AccountRepository<User, UserFilter> accountRepository;

  @Mock
  private UserSearching userSearching;

  @Test
  @DisplayName("Should create a simple user calling Account Management as a Data Provider")
  void shouldCreateANewSimpleUserCallingAccountManagementDataProvider() {
    final User userSample = new User("Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(1990, 1, 21));
    final String mockedId = UUID.randomUUID().toString();
    given(accountRepository.create(userSample)).willReturn(mockedId);

    final String actualId = userRegistration.create(userSample);

    verify(accountRepository).create(userSample);
    assertThat(actualId).isEqualTo(mockedId);
  }

  @Test
  @DisplayName("Should throw an AgeBelowException when registering a new user below 18")
  void shouldThrowAnAgeBelowExceptionWhenRegisteringANewUserBelow18() {
    final User userSample = new User("Jean", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2010, 1, 21));

    assertThrows(AgeBelowException.class, () -> userRegistration.create(userSample));

    verifyNoInteractions(accountRepository);
  }

  @Test
  @DisplayName("Should ensure there is no user with same CPF when register a new one")
  void shouldEnsureThereIsNoUserWithSameCPFOnRegisterANewOne() {
    final User existingUserSample = new User("Billy", new BrazilianCPF("183.271.643-09"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    given(accountRepository.find(any())).willReturn(List.of(existingUserSample));

    final User sameCPFUserSample = new User("Jean", new BrazilianCPF("183.271.643-09"), "jean@billy.com", LocalDate.of(2000, 1, 21));
    assertThrows(AlreadyExistsException.class, ()-> userRegistration.create(sameCPFUserSample));

    verify(accountRepository).find(UserFilter.builder().operator(LogicalOperator.OR).email(sameCPFUserSample.getEmail()).cpf(sameCPFUserSample.getCpf().getRaw()).build());
    verify(accountRepository, times(0)).create(any());
  }

  @Test
  @DisplayName("Should ensure there is no user with same email when register a new one")
  void shouldEnsureThereIsNoUserWithSameEmailOnRegisterANewOne() {
    final User existingUserSample = new User("Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    given(accountRepository.find(any())).willReturn(List.of(existingUserSample));

    final User sameEmailUserSample = new User("Jean", new BrazilianCPF("28632641093"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    assertThrows(AlreadyExistsException.class, ()-> userRegistration.create(sameEmailUserSample));

    verify(accountRepository).find(UserFilter.builder().operator(LogicalOperator.OR).cpf(sameEmailUserSample.getCpf().number()).email(sameEmailUserSample.getEmail()).build());
    verify(accountRepository, times(0)).create(any());
  }

  @Test
  @DisplayName("Should throw an CPFInvalidException when CPF is invalid")
  void shouldThrowAnIllegalArgumentExceptionWhenCPFIsInvalid() {
    final User userSample = new User("Jean", new BrazilianCPF("123.000.000-00"), "billy@jean.com", LocalDate.of(2000, 1, 21));

    assertThrows(CPFInvalidException.class, () -> userRegistration.create(userSample));

    verifyNoInteractions(accountRepository);
  }

  @Test
  @DisplayName("Should update user calling Account Management as a Data Provider")
  void shouldUpdateUserCallingAccountManagementDataProvider() {
    final User existingUserSample = new User(UUID.randomUUID().toString(),"Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    final User userToUpdate = new User(existingUserSample.getId(), "Joe", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    userRegistration.update(userToUpdate);

    verifyNoInteractions(userSearching);
    verify(accountRepository).update(userArgumentCaptor.capture());
    assertThat(userToUpdate).isEqualTo(userArgumentCaptor.getValue());
  }

  @Test
  @DisplayName("Should throw an CPFInvalidException when update to an invalid CPF")
  void shouldThrowCPFInvalidExceptionWhenUpdateToAnInvalidCPF() {
    final User existingUserSample = new User(UUID.randomUUID().toString(),"Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    final User userToUpdate = new User(existingUserSample.getId(), "Joe", new BrazilianCPF("12345"), "billy@jean.com", LocalDate.of(2000, 1, 21));

    assertThrows(CPFInvalidException.class, () -> userRegistration.update(userToUpdate));

    verify(accountRepository, times(0)).update(any());
  }

  @Test
  @DisplayName("Should throw an AgeBelowException when update to an age below 18")
  void shouldThrowAnAgeBelowExceptionWhenUpdateUserAgeToUnder18() {
    final User existingUserSample = new User(UUID.randomUUID().toString(),"Billy", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2000, 1, 21));
    final User userToUpdate = new User(existingUserSample.getId(), "Joe", new BrazilianCPF("86371844563"), "billy@jean.com", LocalDate.of(2015, 1, 21));

    assertThrows(AgeBelowException.class, () -> userRegistration.update(userToUpdate));

    verify(accountRepository, times(0)).update(any());
  }

}
