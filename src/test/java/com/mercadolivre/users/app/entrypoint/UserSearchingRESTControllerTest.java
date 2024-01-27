package com.mercadolivre.users.app.entrypoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.usecase.AccountSearchEngine;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Test
  @DisplayName("[GET] /users/{id} -> Should return user given an id")
  void shouldReturnASingleUserGivenId() throws Exception {
    final String userIdToSearch = "929f30e3-0745-4e00-bc88-9125e9dcad6b";
    final User expectedUserModel = new User(
        userIdToSearch,
        "Josh",
        new BrazilianCPF("13087756792"),
        "josh@something.com",
        LocalDate.of(1990, 1, 20),
        LocalDateTime.of(2024, 1, 27, 1, 46, 25),
        LocalDateTime.of(2024, 1, 27, 16, 51, 9)
    );
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
    final User expectedUserModel = new User(
        userIdToSearch,
        "Josh",
        new BrazilianCPF("13087756792"),
        "josh@something.com",
        LocalDate.of(1990, 1, 20),
        LocalDateTime.of(2024, 1, 27, 1, 46, 25),
        null
    );
    final String expectedUserResponse = new String(Files.readAllBytes(userSearchingResponseSampleWithoutUpdatedAtResource.getFile().toPath()));

    given(userSearching.findById(any())).willReturn(expectedUserModel);

    this.mockMvc
        .perform(get("/users/{id}", userIdToSearch).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedUserResponse));
  }

//  @Test
//  @DisplayName("Should return all existing users")
//  void shouldReturnAllExistingUsers() {
//
//
//  }

}
