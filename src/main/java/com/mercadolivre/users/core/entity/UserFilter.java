package com.mercadolivre.users.core.entity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

@Getter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {

  @Builder.Default
  private LogicalOperator operator = LogicalOperator.AND;

  @Builder.Default
  private SearchType type = SearchType.EQUALS;

  @Builder.Default
  private Boolean normalize = true;

  private String cpf;
  private String name;
  private String email;

  public UserFilter(final Map<String, String> filter) {
    final UserFilter newInstance = new UserFilter();
    initFields(newInstance, filter);
  }

  public boolean isSearchableFields(final String filter) {
    final Set<String> nonSearchableFields = Set.of("operator", "type", "normalize");
    return !nonSearchableFields.contains(filter);
  }

  private void initFields(final UserFilter newInstance, final Map<String, String> filter) {
    filter.entrySet().stream()
        .filter(this::isSearchableFields)
        .forEach(entrySet -> {
          final Field field = ReflectionUtils.findField(this.getClass(), entrySet.getKey());
          if (field == null) throw new IllegalArgumentException(String.format("UserFilter %s does not exist!", entrySet.getKey()));
          setValueOnField(newInstance, field, entrySet.getValue());
        });
  }

  private boolean isSearchableFields(final Entry<String, String> filter) {
    return isSearchableFields(filter.getKey());
  }

  private void setValueOnField(final UserFilter newInstance, final Field targetField, final String value) {
    try {
      targetField.set(newInstance, value);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Could not initialize filters");
    }
  }

}
