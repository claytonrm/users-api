package com.mercadolivre.users.core.usecase;

import org.springframework.stereotype.Component;

@Component
public interface AccountSearchEngine<T, U> {

  T findById(final String id);
}
