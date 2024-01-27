package com.mercadolivre.users.core.usecase;

import org.springframework.stereotype.Component;

@Component
public interface AccountRegistration<T> {

  String create(T user);

  void update(T user);

  T findById(final String id);
}
