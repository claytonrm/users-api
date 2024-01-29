package com.mercadolivre.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.users.app.dataprovider.model.UserModel;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
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
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@DisplayName("[Integration Test] User (Narrow)")
class UsersApiApplicationIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private RestTemplate patchRestTemplate;

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
		final HttpClient httpClient = HttpClientBuilder.create().build();
		this.patchRestTemplate = restTemplate.getRestTemplate();
		this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
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
	@DisplayName("[POST] Should return 409 CONFLICT when CPF already exists")
	void shouldReturnConflictWhenCPFAlreadyExists() throws IOException, URISyntaxException {
		/* Given */
		this.mongoTemplate.save(new UserModel(null,"Mike","13087756792","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Emma","11671645987","mike@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		final String userRegistrationJson = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final HttpEntity<String> request = new HttpEntity<>(userRegistrationJson, headers);

		/* When */
		final ResponseEntity<String> response = this.restTemplate.postForEntity(new URI(this.userHost), request, String.class);

		/* Then */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	@DisplayName("[POST] Should return 409 CONFLICT when email already exists")
	void shouldReturnConflictWhenEmailAlreadyExists() throws IOException, URISyntaxException {
		/* Given */
		this.mongoTemplate.save(new UserModel(null,"Mike","81246164000","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Emma","33232307361","josh@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		final String userRegistrationJson = new String(Files.readAllBytes(userRegistrationSampleResource.getFile().toPath()));
		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final HttpEntity<String> request = new HttpEntity<>(userRegistrationJson, headers);

		/* When */
		final ResponseEntity<String> response = this.restTemplate.postForEntity(new URI(this.userHost), request, String.class);

		/* Then */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
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
		this.mongoTemplate.save(new UserModel(null,"Tom","43951674202","b@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Morello","11671645987","a@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Josh","77883278835","e@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");

    /* When */
    final ResponseEntity<String> response = this.restTemplate.getForEntity(new URI(this.userHost), String.class);

		final List<JsonNode> responseBody =  mapper.readValue(response.getBody(), new TypeReference<>() {});

		/* Then */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseBody).hasSize(3);
	}

	@Test
	@DisplayName("[PATCH] Should update user (partial)")
	void shouldUpdateUser() throws URISyntaxException {
		/* Given */
		this.mongoTemplate.save(new UserModel(null,"Tom","43951674202","someone@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Morello","11671645987","morello@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		this.mongoTemplate.save(new UserModel(null,"Josh","77883278835","james@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
		final UserModel existingUser = this.mongoTemplate.find(new BasicQuery("{ name: 'Morello' }"), UserModel.class, "users").get(0);
		final String patchURL = String.format("%s/%s", this.userHost, existingUser.getId());
		final String changes = "[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"John\"}]";

		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json-patch+json");
		final HttpEntity<String> request = new HttpEntity<>(changes, headers);

		/* When */
		final ResponseEntity response = this.patchRestTemplate.exchange(new URI(patchURL), HttpMethod.PATCH, request, ResponseEntity.class);

		/* Then */
		final String actualUpdatedName = this.mongoTemplate.findById(existingUser.getId(), UserModel.class, "users").getName();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(actualUpdatedName).isEqualTo("John");
	}

//	@Test
//	@DisplayName("[PATCH] Should return 409 CONFLICT when trying to update user (partial)")
//	void shouldReturnConflictWhenTryingToUpdateCPFUser() throws URISyntaxException {
//		/* Given */
//		this.mongoTemplate.save(new UserModel(null,"Tom","43951674202","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
//		this.mongoTemplate.save(new UserModel(null,"Morello","11671645987","eddie@something.com", LocalDate.of(2000, 12, 25), LocalDateTime.now(),null), "users");
//		final UserModel existingUser = this.mongoTemplate.find(new BasicQuery("{ name: 'Morello' }"), UserModel.class, "users").get(0);
//		final String patchURL = String.format("%s/%s", this.userHost, existingUser.getId());
//		final String changes = "[{\"op\":\"replace\",\"path\":\"/cpf\",\"value\":\"439.516.742-02\"}]";
//		final HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.CONTENT_TYPE, "application/json-patch+json");
//		final HttpEntity<String> request = new HttpEntity<>(changes, headers);
//
//		/* When */
//		final ResponseEntity response = this.patchRestTemplate.exchange(new URI(patchURL), HttpMethod.PATCH, request, ResponseEntity.class);
//
//		/* Then */
//		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//	}

}
