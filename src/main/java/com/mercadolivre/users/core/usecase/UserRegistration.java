package com.mercadolivre.users.core.usecase;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.Message;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.EntityAlreadyExistsException;
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
    validate(user);
    return this.accountRepository.create(user);
  }

  private void validate(final User user) {
    if (!user.hasValidAge(ALLOWED_MINIMAL_AGE)) {
      throw new AgeBelowException(
          Message.REGISTRATION_ERROR_AGE_BELOW_X.getCode(),
          Message.REGISTRATION_ERROR_AGE_BELOW_X.getMessage());
    }

    final UserFilter filter = UserFilter.builder().cpf(user.getCpf()).build();

    if (!CollectionUtils.isEmpty(this.accountRepository.findBy(filter))) {
      log.warn("User is already registered!");
      throw new EntityAlreadyExistsException(
          Message.REGISTRATION_ERROR_USER_ALREADY_EXISTS.getCode(),
          Message.REGISTRATION_ERROR_USER_ALREADY_EXISTS.getMessage());
    }

  }
}
