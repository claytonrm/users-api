package com.mercadolivre.users.core.usecase;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.Message;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSearching {

  private final AccountRepository<User, UserFilter> accountRepository;

  public UserSearching(final AccountRepository<User, UserFilter> accountRepository) {
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

  public List<User> findAll() {
    return this.accountRepository.findAll();
  }

  public List<User> findByName(final String name) {
    final String nonNullName = Optional.ofNullable(name).orElse("");
    final UserFilter userFilter = UserFilter.builder().name(nonNullName.strip().toLowerCase()).build();
    return findBy(userFilter);
  }

  private List<User> findBy(final UserFilter filter) {
    return this.accountRepository.findBy(filter);
  }
}
