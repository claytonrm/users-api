package com.mercadolivre.users.core.dataprovider;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public interface AccountRepository<T, U> {

  String create(final T entity);

  void update(final T entity);

  List<T> findBy(final U filter);

  Optional<T> findById(final String id);

  List<T> findAll();

}
