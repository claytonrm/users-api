package com.mercadolivre.users.core.usecase;

import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserSearchByMany {

  private final AccountRepository<User, UserFilter> accountRepository;

  public UserSearchByMany(final AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
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
