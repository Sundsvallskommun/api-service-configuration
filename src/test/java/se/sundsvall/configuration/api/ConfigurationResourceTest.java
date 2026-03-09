package se.sundsvall.configuration.api;

import java.util.List;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class ConfigurationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "feature-flags";
	private static final String APPLICATION = "my-app";
	private static final String PATH = "/{municipalityId}/{namespace}/configurations/{application}";
	private static final String PATH_BY_NAMESPACE = "/{municipalityId}/{namespace}/configurations";

	@MockitoBean
	private ConfigurationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getConfiguration() {
		final var configuration = Configuration.create()
			.withApplication(APPLICATION)
			.withNamespace(NAMESPACE)
			.withData(Map.of("enabled", List.of("true")));
		when(serviceMock.getConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(configuration);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"namespace", NAMESPACE,
				"application", APPLICATION)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Configuration.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getData()).containsEntry("enabled", List.of("true"));
		verify(serviceMock).getConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void getConfigurationsByNamespace() {
		final var configurations = List.of(
			Configuration.create().withApplication("app1").withNamespace(NAMESPACE).withData(Map.of("k", List.of("v"))),
			Configuration.create().withApplication("app2").withNamespace(NAMESPACE).withData(Map.of("k", List.of("v"))));
		when(serviceMock.getConfigurationsByNamespace(MUNICIPALITY_ID, NAMESPACE)).thenReturn(configurations);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH_BY_NAMESPACE).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"namespace", NAMESPACE)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Configuration.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).hasSize(2);
		verify(serviceMock).getConfigurationsByNamespace(MUNICIPALITY_ID, NAMESPACE);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void createConfiguration() {
		final var configuration = Configuration.create().withData(Map.of("enabled", List.of("true")));
		when(serviceMock.createConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration)).thenReturn(configuration);

		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"namespace", NAMESPACE,
				"application", APPLICATION)))
			.bodyValue(configuration)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location("/%s/%s/configurations/%s".formatted(MUNICIPALITY_ID, NAMESPACE, APPLICATION));

		verify(serviceMock).createConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void updateConfiguration() {
		final var configuration = Configuration.create().withData(Map.of("enabled", List.of("false")));
		when(serviceMock.updateConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration)).thenReturn(configuration);

		final var response = webTestClient.put()
			.uri(builder -> builder.path(PATH).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"namespace", NAMESPACE,
				"application", APPLICATION)))
			.bodyValue(configuration)
			.exchange()
			.expectStatus().isOk()
			.expectBody(Configuration.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getData()).containsEntry("enabled", List.of("false"));
		verify(serviceMock).updateConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void deleteConfiguration() {
		webTestClient.delete()
			.uri(builder -> builder.path(PATH).build(Map.of(
				"municipalityId", MUNICIPALITY_ID,
				"namespace", NAMESPACE,
				"application", APPLICATION)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).deleteConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verifyNoMoreInteractions(serviceMock);
	}
}
