package com.mercadolivre.users.app.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.users.app.entrypoint.dto.UserRegistrationDTO;
import com.mercadolivre.users.core.entity.BrazilianCPF;
import com.mercadolivre.users.core.entity.Message;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.entity.UserFilter;
import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.AlreadyExistsException;
import com.mercadolivre.users.core.exception.NotFoundException;
import com.mercadolivre.users.core.usecase.AccountRegistration;
import com.mercadolivre.users.core.usecase.AccountSearchEngine;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@DisplayName("[UserRegistrationRESTController] Unit Test")
class UserRegistrationRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountRegistration<User> userRegistration;

  @MockBean
  private AccountSearchEngine<User, UserFilter> accountSearchEngine;

  @Value("classpath:samples/user-registration.json")
  private Resource userRegistrationSampleResource;

  @Value("classpath:samples/user-registration-patch.json")
  private Resource userRegistrationPatchSampleResource;

  @Autowired
  private ObjectMapper mapper;

  @Test
  @DisplayName("[POST] /users -> Should create a new user")
  void shouldCallServiceAndReturnCreated() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
    final ArgumentCaptor<User> userModelExpected = ArgumentCaptor.forClass(User.class);

    this.mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
        .andExpect(status().isCreated());

    verify(userRegistration).create(userModelExpected.capture());

    final User actualUserModel = userModelExpected.getValue();
    final User expectedUserOnService = mapper.readValue(sampleUserRequest, UserRegistrationDTO.class).toUserEntity();
    ReflectionTestUtils.setField(expectedUserOnService, "createdAt", actualUserModel.getCreatedAt());

    assertThat(actualUserModel).isEqualTo(expectedUserOnService);
  }

  @Test
  @DisplayName("[POST] /users -> Should return a header location with resource just created")
  void shouldReturnAHeaderWithResourceJustCreated() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
    given(userRegistration.create(any(User.class))).willReturn(UUID.randomUUID().toString());

    this.mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
        .andExpect(status().isCreated())
        .andExpect(header().exists("location"));

  }

  @Test
  @DisplayName("[POST] /users -> Should return 400 Bad Request when use case throws IllegalArgumentException")
  void shouldReturnBadRequestWhenServiceThrowsIllegalArgumentException() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
    given(userRegistration.create(any())).willThrow(new AgeBelowException("AGE_BELOW_X", "Access allowed only to users aged 18 and above."));

    this.mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("AGE_BELOW_X"))
            .andExpect(jsonPath("$.message.en").value("Access allowed only to users aged 18 and above."));
  }

  @Test
  @DisplayName("[POST] /users -> Should return 409 Conflict when use case throws EntityAlreadyExists")
  void shouldReturnConflictWhenUseCaseThrowsEntityAlreadyExistsException() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
    given(userRegistration.create(any())).willThrow(new AlreadyExistsException("USER_ALREADY_EXISTS", "User already exists."));

    this.mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.message.en").value("User already exists."));
  }

  @Test
  @DisplayName("[PATCH] /users/{id} -> Should update CPF and Birthdate")
  void shouldUpdateCPFAndBirthdate() throws Exception {
    final User mockedExistingUser = mapper.readValue(new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath())), UserRegistrationDTO.class).toUserEntity();
    final String userId = UUID.randomUUID().toString();
    ReflectionTestUtils.setField(mockedExistingUser, "id", userId);
    given(userRegistration.findById(any())).willReturn(mockedExistingUser);
    doNothing().when(userRegistration).update(any());
    final String sampleUserPatch = new String(Files.readAllBytes(userRegistrationPatchSampleResource.getFile().toPath()));
    final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    this.mockMvc
            .perform(patch("/users/{id}", userId).contentType("application/json-patch+json").content(sampleUserPatch))
            .andExpect(status().isNoContent());

    verify(userRegistration, times(1)).findById(userId);
    verify(userRegistration).update(userArgumentCaptor.capture());

    final User expectedChanges =
        new User(
            mockedExistingUser.getId(),
            mockedExistingUser.getName(),
            new BrazilianCPF("892.435.194-09"),
            mockedExistingUser.getEmail(),
            LocalDate.of(1991, 12, 25),
            mockedExistingUser.getCreatedAt(),
            userArgumentCaptor.getValue().getUpdatedAt()
        );

    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedChanges);
  }

  @Test
  @DisplayName("[PATCH] /users/{id} -> Should return 404 NOT_FOUND when trying to update")
  void shouldReturnNotFoundWhenUpdateAMissingUser() throws Exception {
    final String sampleUserPatch = new String(Files.readAllBytes(userRegistrationPatchSampleResource.getFile().toPath()));
    given(userRegistration.findById(anyString())).willThrow(new NotFoundException(
        Message.ERROR_TEMPLATE_USER_NOT_FOUND.getCode(),
        String.format(Message.ERROR_TEMPLATE_USER_NOT_FOUND.getMessage(), "randomId"))
    );

    this.mockMvc
        .perform(patch("/users/{id}", UUID.randomUUID().toString()).contentType("application/json-patch+json").content(sampleUserPatch))
        .andExpect(status().isNotFound());

    verify(userRegistration, times(0)).update(any());
  }


}
