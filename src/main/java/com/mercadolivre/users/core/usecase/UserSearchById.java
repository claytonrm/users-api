package com.mercadolivre.users.core.usecase;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.Message;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSearchById {

  private final AccountRepository<User, UserFilter> accountRepository;

  public UserSearchById(final AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public User findById(final String id) {
    return this.accountRepository
        .findById(id)
        .orElseThrow(() -> {
          log.error("User {} not found!", id);
          return new NotFoundException(
              Message.ERROR_TEMPLATE_USER_NOT_FOUND.getCode(),
              String.format(Message.ERROR_TEMPLATE_USER_NOT_FOUND.getMessage(), id));
        });
  }
}
