package com.mercadolivre.users.core.usecase;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.LogicalOperator;
import com.mercadolivre.users.core.entity.Message;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.CPFInvalidException;
import com.mercadolivre.users.core.exception.EntityAlreadyExistsException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class UserRegistration {

  private static int ALLOWED_MINIMAL_AGE = 18;

  private final AccountRepository<User, UserFilter> accountRepository;

  public UserRegistration(final AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public String create(final User user) {
    return Optional.of(user)
        .map(this::validate)
        .map(this.accountRepository::create)
        .orElseThrow(() -> new IllegalStateException("Could not create user!"));
  }

  private User validate(final User user) {
    validateAge(user);
    validateCPF(user);
    validateIfAlreadyExists(user);
    return user;
  }

  private void validateCPF(final User user) {
    if (!user.isValidCPF()) {
      log.error("CPF is invalid.");
      throw new CPFInvalidException(
          Message.REGISTRATION_ERROR_CPF_INVALID.getCode(),
          Message.REGISTRATION_ERROR_CPF_INVALID.getMessage());
    }
  }

  private void validateAge(final User user) {
    if (!user.isValidAge(ALLOWED_MINIMAL_AGE)) {
      log.error("The user is below the allowed age.");
      throw new AgeBelowException(
          Message.REGISTRATION_ERROR_AGE_BELOW_X.getCode(),
          Message.REGISTRATION_ERROR_AGE_BELOW_X.getMessage());
    }
  }

  private void validateIfAlreadyExists(final User user) {
    final UserFilter filter = UserFilter.builder()
        .cpf(user.getCpf())
        .email(user.getEmail())
        .operator(LogicalOperator.OR)
        .build();

    if (!CollectionUtils.isEmpty(this.accountRepository.findBy(filter))) {
      log.warn("User is already registered!");
      throw new EntityAlreadyExistsException(
          Message.REGISTRATION_ERROR_USER_ALREADY_EXISTS.getCode(),
          Message.REGISTRATION_ERROR_USER_ALREADY_EXISTS.getMessage());
    }
  }
}
