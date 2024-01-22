package com.mercadolivre.users.core.dataprovider;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface AccountRepository<T, U> {

    String create(final T entity);

    void update(final T entity);

    List<T> findBy(final U filter);

}
