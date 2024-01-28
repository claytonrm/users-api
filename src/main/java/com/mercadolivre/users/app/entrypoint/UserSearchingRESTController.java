package com.mercadolivre.users.app.entrypoint;

import com.mercadolivre.users.app.entrypoint.dto.UserResponseDTO;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.usecase.AccountSearchEngine;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping
  public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestParam final Map<String, String> filter) {
    return ResponseEntity.ok(
        userSearching.searchBy(new UserFilter(filter)).stream()
            .map(UserResponseDTO::new)
            .collect(Collectors.toList()));
  }
}
