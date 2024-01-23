package com.mercadolivre.users.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.LogicalOperator;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.CPFInvalidException;
import com.mercadolivre.users.core.exception.EntityAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationUseCaseTest {

  @InjectMocks
  private UserRegistration userRegistration;

  @Mock
  private AccountRepository<User, UserFilter> accountRepository;

  @Test
  @DisplayName("Should register a simple user calling Account Management as a Data Provider")
  void shouldRegisterANewSimpleUserCallingAccountManagementDataProvider() {
    final User userSample = new User("Billy", "000.000.000-00", "billy@jean.com", LocalDate.of(1990, 01, 21));
    final String mockedId = UUID.randomUUID().toString();
    given(accountRepository.create(userSample)).willReturn(mockedId);

    final String actualId = userRegistration.create(userSample);

    verify(accountRepository).create(userSample);
    assertThat(actualId).isEqualTo(mockedId);
  }

  @Test
  @DisplayName("Should throw an AgeBelowException when registering a new user below 18")
  void shouldThrowAnAgeBelowExceptionWhenRegisteringANewUserBelow18() {
    final User userSample = new User("Jean", "000.000.000-00", "billy@jean.com", LocalDate.of(2010, 01, 21));

    assertThrows(AgeBelowException.class, () -> userRegistration.create(userSample));

    verifyNoInteractions(accountRepository);
  }

  @Test
  @DisplayName("Should ensure there is no user with same CPF when register a new one")
  void shouldEnsureThereIsNoUserWithSameCPFOnRegisterANewOne() {
    final User existingUserSample = new User("Billy", "000.000.000-00", "billy@jean.com", LocalDate.of(2000, 01, 21));
    given(accountRepository.findBy(any())).willReturn(List.of(existingUserSample));

    final User sameCPFUserSample = new User("Jean", "000.000.000-00", "jean@billy.com", LocalDate.of(2000, 01, 21));
    assertThrows(EntityAlreadyExistsException.class, ()-> userRegistration.create(sameCPFUserSample));

    verify(accountRepository).findBy(UserFilter.builder().operator(LogicalOperator.OR).email(sameCPFUserSample.getEmail()).cpf("000.000.000-00").build());
    verify(accountRepository, times(0)).create(any());
  }

  @Test
  @DisplayName("Should ensure there is no user with same email when register a new one")
  void shouldEnsureThereIsNoUserWithSameEmailOnRegisterANewOne() {
    final User existingUserSample = new User("Billy", "000.000.000-00", "billy@jean.com", LocalDate.of(2000, 01, 21));
    given(accountRepository.findBy(any())).willReturn(List.of(existingUserSample));

    final User sameEmailUserSample = new User("Jean", "000.000.000-10", "billy@jean.com", LocalDate.of(2000, 01, 21));
    assertThrows(EntityAlreadyExistsException.class, ()-> userRegistration.create(sameEmailUserSample));

    verify(accountRepository).findBy(UserFilter.builder().operator(LogicalOperator.OR).cpf(sameEmailUserSample.getCpf()).email(sameEmailUserSample.getEmail()).build());
    verify(accountRepository, times(0)).create(any());
  }

  @Test
  @DisplayName("Should throw an IllegalArgumentException when CPF is invalid")
  void shouldThrowAnIllegalArgumentExceptionWhenCPFIsInvalid() {
    final User userSample = new User("Jean", "123.000.000-00", "billy@jean.com", LocalDate.of(2000, 01, 21));

    assertThrows(CPFInvalidException.class, () -> userRegistration.create(userSample));

    verifyNoInteractions(accountRepository);
  }



}
