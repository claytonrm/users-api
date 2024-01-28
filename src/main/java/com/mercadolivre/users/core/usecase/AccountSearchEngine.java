package com.mercadolivre.users.core.usecase;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface AccountSearchEngine<T, U> {

  T findById(final String id);
  List<T> searchBy(final U filter);
}
