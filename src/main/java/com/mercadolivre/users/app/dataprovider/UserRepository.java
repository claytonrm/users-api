package com.mercadolivre.users.app.dataprovider;

import com.mercadolivre.users.app.dataprovider.model.UserModel;
import com.mercadolivre.users.core.dataprovider.AccountRepository;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements AccountRepository<User, UserFilter> {

  private static final String COLLECTION_NAME = "users";
  public final MongoTemplate mongoTemplate;

  public UserRepository(final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public String create(final User entity) {
    final UserModel userModel = new UserModel(entity);
    return this.mongoTemplate.insert(userModel, COLLECTION_NAME).getId();
  }

  @Override
  public void update(final User entity) {
    this.mongoTemplate.save(new UserModel(entity), COLLECTION_NAME);
  }

  @Override
  public List<User> find(final UserFilter filter) {
    final List<String> paramsKeyValue = Arrays.stream(filter.getClass().getDeclaredFields())
            .filter(field -> getAsString(filter, field) != null && filter.isSearchableFields(field.getName()))
            .map(field -> String.format("%s : '%s'", field.getName(), getAsString(filter, field)))
            .collect(Collectors.toList());

    final StringBuilder queryBuilder = new StringBuilder("{");
    paramsKeyValue.stream().forEach(keyValue -> queryBuilder.append(String.format("%s, ", keyValue)));
    queryBuilder.append("}");

    final BasicQuery query = new BasicQuery(queryBuilder.toString());

    return this.mongoTemplate.find(query, UserModel.class, COLLECTION_NAME).stream()
        .map(UserModel::toEntity)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> findById(final String id) {
    return Optional.ofNullable(this.mongoTemplate.findById(id, UserModel.class, COLLECTION_NAME))
        .map(UserModel::toEntity);
  }

  private String getAsString(final UserFilter filter, final Field field) {
    try {
      field.setAccessible(true);
      return Optional.ofNullable(field.get(filter)).map(String::valueOf).orElse(null);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Could not apply query filter!");
    }
  }

}
