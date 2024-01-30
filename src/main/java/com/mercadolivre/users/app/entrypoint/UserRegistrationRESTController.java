package com.mercadolivre.users.app.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mercadolivre.users.app.entrypoint.dto.UserRegistrationDTO;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.usecase.AccountRegistration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Users")
@Slf4j
@RestController
@RequestMapping("/users")
public class UserRegistrationRESTController {

  private final AccountRegistration<User> userRegistration;
  private final ObjectMapper mapper;

  public UserRegistrationRESTController(final AccountRegistration<User> userRegistration, final ObjectMapper mapper) {
    this.userRegistration = userRegistration;
    this.mapper = mapper;
  }

  @Operation(summary = "Create a new user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Found the user"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid fields. Also it validates user's age, email and CPF"),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists (either CPF or email"),
        @ApiResponse(
            responseCode = "500",
            description = "Server could not process for internal reasons")
      })
  @PostMapping
  public ResponseEntity<Void> create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples =
      @ExampleObject(value ="{\n"
      + "  \"name\": \"Josh\",\n"
      + "  \"cpf\": \"130.877.567-92\",\n"
      + "  \"email\": \"josh@something.com\",\n"
      + "  \"birthDate\": \"20/01/1990\"\n"
      + "}"))) @RequestBody @Valid final UserRegistrationDTO user) {
    final String id = this.userRegistration.create(user.toUserEntity());

    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();

    return ResponseEntity.created(location).build();
  }

  @Operation(summary = "Update an existing user (partial)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User has been successfully updated"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid fields. Also it validates user's age, email and CPF"),
        @ApiResponse(
            responseCode = "500",
            description = "Server could not process for internal reasons")
      })
  @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Void> partialUpdate(@PathVariable final String id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(examples = @ExampleObject(value = "[{\"op\":\"replace\",\"path\":\"/birthDate\",\"value\":\"25/12/1991\"}]\n"))) @RequestBody final JsonPatch patch) {
    final User existingUser = this.userRegistration.findById(id);
    final UserRegistrationDTO userChanges = applyPatch(patch, new UserRegistrationDTO(existingUser));

    this.userRegistration.update(userChanges.toUserEntity(existingUser.getId(), existingUser.getCreatedAt(), LocalDateTime.now()));

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
