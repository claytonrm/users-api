package com.mercadolivre.users.app.entrypoint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mercadolivre.users.app.entrypoint.dto.UserResponseDTO;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.usecase.AccountSearchEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class UserSearchingRESTController {

  private final AccountSearchEngine<User, UserFilter> userSearching;

  public UserSearchingRESTController(final AccountSearchEngine<User, UserFilter> userSearching) {
    this.userSearching = userSearching;
  }

  @Operation(summary = "Get a user by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the user", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)) }),
      @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
  })
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getSingleUser(@PathVariable final String id) {
    return ResponseEntity.ok(new UserResponseDTO(this.userSearching.findById(id)));
  }

  @Operation(summary = "Get users by filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found users", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))) }),
      @ApiResponse(responseCode = "400", description = "Invalid filter supplied", content = @Content),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<UserResponseDTO>> getUsers(@Parameter(description = "The filter for retrieve users", required = true, schema = @Schema(implementation = UserFilter.class)) @RequestParam final Map<String, String> filter) {
    return ResponseEntity.ok(
        userSearching.searchBy(new UserFilter(filter)).stream()
            .map(UserResponseDTO::new)
            .collect(Collectors.toList()));
  }
}
