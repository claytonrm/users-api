package com.mercadolivre.users;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureDataMongo
@DisplayName("[Integration Test] User (Narrow)")
class UsersApiApplicationIntegrationTests {

//	@LocalServerPort
//	private static int PORT;
//
//	@Autowired
//	private TestRestTemplate restTemplate;
//
//	private static String USERS_HOST = String.format("http://localhost:%d/users", PORT);
//
//	@Disabled
//	@Test
//	@DisplayName("[POST] Should create a new user")
//	void shouldCreateANewUser() throws URISyntaxException {
//		/* Given */
//		final UserRegistrationDTO userToCreate = new UserRegistrationDTO("Billy", "000.000.000-00", LocalDate.of(2000, 01, 01));
//		final HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//		final HttpEntity<UserRegistrationDTO> request = new HttpEntity<>(userToCreate, headers);
//
//		/* When */
//		final ResponseEntity<String> response = this.restTemplate.postForEntity(new URI(USERS_HOST), request, String.class);
//
//		/* Then */
//		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
//	}

}
