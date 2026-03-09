package se.sundsvall.configuration.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.configuration.Application;
import se.sundsvall.configuration.api.model.Configuration;
import se.sundsvall.configuration.service.ConfigurationService;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class ConfigurationResourceFailureTest {

	private static final String INVALID_MUNICIPALITY_ID = "bad-municipality-id";
	private static final String NAMESPACE = "feature-flags";
	private static final String APPLICATION = "my-app";
	private static final String PATH = "/{municipalityId}/{namespace}/configurations/{application}";

	@MockitoBean
	private ConfigurationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getConfigurationWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of(
				"municipalityId", INVALID_MUNICIPALITY_ID,
				"namespace", NAMESPACE,
				"application", APPLICATION)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(tuple("getConfiguration.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void createConfigurationWithEmptyData() {
		final var configuration = Configuration.create().withData(Map.of());

		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of(
				"municipalityId", "2281",
				"namespace", NAMESPACE,
				"application", APPLICATION)))
			.bodyValue(configuration)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}
}
