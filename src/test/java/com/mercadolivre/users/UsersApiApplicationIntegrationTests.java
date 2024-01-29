package com.mercadolivre.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.users.app.dataprovider.model.UserModel;
import com.mercadolivre.users.app.entrypoint.dto.UserResponseDTO;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@DisplayName("[Integration Test] User (Narrow)")
class UsersApiApplicationIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

  @Value("classpath:samples/user-registration.json")
  private Resource userRegistrationSampleResource;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ObjectMapper mapper;

	private String userHost;

	@BeforeEach
	void setupBeforeEach() {
		this.userHost = String.format("http://localhost:%d/users", port);
	}

	@AfterEach
	void tearDownAfterEach() {
		mongoTemplate.remove(new Query(), "users");
	}

	@Test
	@DisplayName("[POST] Should create a new user")
	void shouldCreateANewUser() throws URISyntaxException, IOException {
		/* Given */
		final String userRegistrationJson = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    final HttpEntity<String> request = new HttpEntity<>(userRegistrationJson, headers);

    /* When */
    final ResponseEntity<String> response = this.restTemplate.postForEntity(new URI(this.userHost), request, String.class);

		/* Then */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	@DisplayName("[GET] Should find user by name")
	void shouldFindUserByName() throws URISyntaxException, JsonProcessingException {
		/* Given */
		this.mongoTemplate.save(new UserModel(
				null,
				"Eddie",
				"11837232539",
				"eddie@something.com",
				LocalDate.of(2000, 12, 25),
				LocalDateTime.now(),
				null
		), "users");
		final String hostWithQueryParams = String.format("%s?name=Eddie", this.userHost);

    /* When */
    final ResponseEntity<String> response = this.restTemplate.getForEntity(new URI(hostWithQueryParams), String.class);

		final List<JsonNode> responseBody =  mapper.readValue(response.getBody(), new TypeReference<>() {});

		/* Then */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseBody).hasSize(1);
		assertThat(response.getBody()).contains("name");
		assertThat(response.getBody()).contains("Eddie");
	}

  @Test
  @DisplayName("[GET] Should find all existing users")
  void shouldFindAllUsers() throws JsonProcessingException, URISyntaxException {
		/* Given */
		this.mongoTemplate.save(new UserModel(null,"Tom","43951674202","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Morello","11671645987","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Josh","77883278835","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");

    /* When */
    final ResponseEntity<String> response = this.restTemplate.getForEntity(new URI(this.userHost), String.class);

		final List<JsonNode> responseBody =  mapper.readValue(response.getBody(), new TypeReference<>() {});

		/* Then */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseBody).hasSize(3);
	}

}
