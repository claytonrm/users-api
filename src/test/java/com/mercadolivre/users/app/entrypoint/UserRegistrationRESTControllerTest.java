package com.mercadolivre.users.app.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.users.core.entity.User;
import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.EntityAlreadyExistsException;
import com.mercadolivre.users.core.usecase.UserRegistration;

import java.nio.file.Files;
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
@DisplayName("[Unit Test] UserRegistrationRESTController")
public class UserRegistrationRESTControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserRegistration userRegistrationUseCase;

  @Value("classpath:samples/user-registration.json")
  private Resource userRegistrationSampleResource;

  @Autowired
  private ObjectMapper mapper;

  @Test
  @DisplayName("[POST] /users -> Should call service to create a new user")
  void shouldCallServiceAndReturnCreated() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
    final ArgumentCaptor<User> userModelExpected = ArgumentCaptor.forClass(User.class);

    this.mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
        .andExpect(status().isCreated());

    verify(userRegistrationUseCase).create(userModelExpected.capture());

    final User actualUserModel = userModelExpected.getValue();
    final User expectedUserOnService = mapper.readValue(sampleUserRequest, UserRegistrationDTO.class).toUserModel();
    ReflectionTestUtils.setField(expectedUserOnService, "createdAt", actualUserModel.getCreatedAt());

    assertThat(actualUserModel).isEqualTo(expectedUserOnService);
  }

  @Test
  @DisplayName("[POST] /users -> Should return a header location with resource just created")
  void shouldReturnAHeaderWithResourceJustCreated() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(
        userRegistrationSampleResource.getFile().toPath()));
    given(userRegistrationUseCase.create(any(User.class))).willReturn(UUID.randomUUID().toString());

    this.mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
        .andExpect(status().isCreated())
        .andExpect(header().exists("location"));

  }

  @Test
  @DisplayName("[POST] /users -> Should return 400 Bad Request when use case throws IllegalArgumentException")
  void shouldReturnBadRequestWhenServiceThrowsIllegalArgumentException() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(
        userRegistrationSampleResource.getFile().toPath()));
    given(userRegistrationUseCase.create(any())).willThrow(new AgeBelowException("AGE_BELOW_X", "Access allowed only to users aged 18 and above."));

    this.mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("AGE_BELOW_X"))
            .andExpect(jsonPath("$.message.en").value("Access allowed only to users aged 18 and above."));
  }

  @Test
  @DisplayName("[POST] /users -> Should return 409 Conflict when use case throws EntityAlreadyExists")
  void shouldReturnConflictWhenUseCaseThrowsEntityAlreadyExistsException() throws Exception {
    final String sampleUserRequest = new String(Files.readAllBytes(
        userRegistrationSampleResource.getFile().toPath()));
    given(userRegistrationUseCase.create(any())).willThrow(new EntityAlreadyExistsException("USER_ALREADY_EXISTS", "User already exists."));

    this.mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(sampleUserRequest))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
            .andExpect(jsonPath("$.message.en").value("User already exists."));
  }

}
