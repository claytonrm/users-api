package com.mercadolivre.users.app.entrypoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.usecase.AccountSearchEngine;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserSearchingRESTController.class)
@DisplayName("[UserSearchingRESTController] Unit Test")
class UserSearchingRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountSearchEngine<User, UserFilter> userSearching;

  @Value("classpath:samples/user-searching-response.json")
  private Resource userSearchingResponseSampleResource;

  @Value("classpath:samples/user-searching-without-updated-at-response.json")
  private Resource userSearchingResponseSampleWithoutUpdatedAtResource;

  @Value("classpath:samples/users-response.json")
  private Resource usersResponseSampleResource;

  @Test
  @DisplayName("[GET] /users/{id} -> Should return user given an id")
  void shouldReturnASingleUserGivenId() throws Exception {
    final String userIdToSearch = "929f30e3-0745-4e00-bc88-9125e9dcad6b";
    final User expectedUserModel = getUserSample();
    final String expectedUserResponse = new String(Files.readAllBytes(userSearchingResponseSampleResource.getFile().toPath()));

    given(userSearching.findById(any())).willReturn(expectedUserModel);

    this.mockMvc
        .perform(get("/users/{id}", userIdToSearch).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedUserResponse));

    verify(userSearching).findById(userIdToSearch);
  }



  @Test
  @DisplayName("[GET] /users/{id} -> Should return user given an id without updatedAt")
  void shouldNotSerializeUpdatedAt() throws Exception {
    final String userIdToSearch = "929f30e3-0745-4e00-bc88-9125e9dcad6b";
    final User expectedUserModel = getUserSample();
    final String expectedUserResponse = new String(Files.readAllBytes(userSearchingResponseSampleWithoutUpdatedAtResource.getFile().toPath()));

    given(userSearching.findById(any())).willReturn(expectedUserModel);

    this.mockMvc
        .perform(get("/users/{id}", userIdToSearch).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedUserResponse));
  }

  @Test
  @DisplayName("[GET] /users -> Should return all existing users")
  void shouldReturnAllExistingUsers() throws Exception {
    final List<User> existingUsers = List.of(
      getUserSample(),
      new User(
        "7e51e285-6f53-433c-b2a3-6607b70e468e",
        "Hayley",
        new BrazilianCPF("421.634.543-39"),
        "hayley@something.com",
        LocalDate.of(1987, 4, 17),
        LocalDateTime.of(2024, 1, 21, 4, 45, 25),
          null
    ));

    final String expectedUsersResponse = new String(Files.readAllBytes(usersResponseSampleResource.getFile().toPath()));

    given(userSearching.searchBy(any())).willReturn(existingUsers);

    this.mockMvc
        .perform(get("/users").param("name", "").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedUsersResponse));

  }

  @Test
  @DisplayName("[GET] /users -> Should return 404 BAD_REQUEST query param is not valid")
  void shouldReturnBadRequestQueryParamIsNotValid() throws Exception {
    this.mockMvc
        .perform(get("/users").param("anything", "").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(userSearching);
  }

  private User getUserSample() {
    return new User(
        "929f30e3-0745-4e00-bc88-9125e9dcad6b",
        "Josh",
        new BrazilianCPF("13087756792"),
        "josh@something.com",
        LocalDate.of(1990, 1, 20),
        LocalDateTime.of(2024, 1, 27, 1, 46, 25),
        LocalDateTime.of(2024, 1, 27, 16, 51, 9)
    );
  }

}
