package com.mercadolivre.users.app.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.usecase.UserRegistration;
import com.mercadolivre.users.core.usecase.UserSearching;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserRegistrationRESTController {

  private final UserRegistration userRegistration;
  private final UserSearching userSearching;
  private final ObjectMapper mapper;

  @Autowired
  public UserRegistrationRESTController(final UserRegistration userRegistration, final UserSearching userSearching, final ObjectMapper mapper) {
    this.userRegistration = userRegistration;
    this.userSearching = userSearching;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody @Valid final UserRegistrationDTO user) {
    final String id = userRegistration.create(user.toUserModel());

    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();

    return ResponseEntity.created(location).build();
  }

  @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Void> partialUpdate(@PathVariable final String id, @RequestBody final JsonPatch patch) {
    final User existingUser = this.userSearching.findById(id);
    final UserRegistrationDTO userChanges = applyPatch(patch, new UserRegistrationDTO(existingUser));

    this.userRegistration.update(userChanges.toUserModel(existingUser.getId(), existingUser.getCreatedAt(), LocalDateTime.now()));

    return ResponseEntity.noContent().build();
  }

  private UserRegistrationDTO applyPatch(final JsonPatch patch, final UserRegistrationDTO target) {
    try {
      final JsonNode patched = patch.apply(mapper.convertValue(target, JsonNode.class));
      return mapper.treeToValue(patched, UserRegistrationDTO.class);
    } catch (JsonProcessingException | JsonPatchException e) {
      log.error("Could not apply patch on user!");
      throw new IllegalArgumentException(e);
    }
  }
}
