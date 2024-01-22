package com.mercadolivre.users.app.entrypoint;

import com.mercadolivre.users.core.usecase.UserRegistration;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users")
public class UserRegistrationRESTController {

  private final UserRegistration userRegistration;

  @Autowired
  public UserRegistrationRESTController(final UserRegistration userRegistration) {
    this.userRegistration = userRegistration;
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
}
