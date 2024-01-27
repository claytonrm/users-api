package com.mercadolivre.users.app.entrypoint;

import com.mercadolivre.users.app.dto.UserResponseDTO;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.usecase.AccountSearchEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserSearchingRESTController {

  private final AccountSearchEngine<User, UserFilter> userSearching;

  public UserSearchingRESTController(final AccountSearchEngine<User, UserFilter> userSearching) {
    this.userSearching = userSearching;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getSingleUser(@PathVariable final String id) {
    return ResponseEntity.ok(new UserResponseDTO(this.userSearching.findById(id)));
  }

}
