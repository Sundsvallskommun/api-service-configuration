package se.sundsvall.configuration.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.configuration.api.model.Configuration;
import se.sundsvall.configuration.integration.db.ConfigurationRepository;
import se.sundsvall.configuration.integration.db.model.ConfigurationEntity;
import se.sundsvall.configuration.integration.db.model.ConfigurationItemEntity;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String APPLICATION = "my-app";
	private static final String NAMESPACE = "feature-flags";

	@Mock
	private ConfigurationRepository repositoryMock;

	@InjectMocks
	private ConfigurationService service;

	@Test
	void getConfiguration() {
		final var entity = ConfigurationEntity.create()
			.withApplication(APPLICATION)
			.withNamespace(NAMESPACE)
			.withItems(List.of(ConfigurationItemEntity.create().withKey("enabled").withValue("true")));
		when(repositoryMock.findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(Optional.of(entity));

		final var result = service.getConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE);

		assertThat(result).isNotNull();
		assertThat(result.getData()).containsEntry("enabled", List.of("true"));
		verify(repositoryMock).findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
	}

	@Test
	void getConfigurationNotFound() {
		when(repositoryMock.findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE))
			.isInstanceOf(ThrowableProblem.class)
			.satisfies(e -> assertThat(((ThrowableProblem) e).getStatus()).isEqualTo(NOT_FOUND));

		verify(repositoryMock).findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void getConfigurationsByNamespace() {
		final var entities = List.of(
			ConfigurationEntity.create().withApplication("app1").withNamespace(NAMESPACE).withItems(List.of()),
			ConfigurationEntity.create().withApplication("app2").withNamespace(NAMESPACE).withItems(List.of()));
		when(repositoryMock.findAllByMunicipalityIdAndNamespace(MUNICIPALITY_ID, NAMESPACE)).thenReturn(entities);

		final var result = service.getConfigurationsByNamespace(MUNICIPALITY_ID, NAMESPACE);

		assertThat(result).hasSize(2);
		verify(repositoryMock).findAllByMunicipalityIdAndNamespace(MUNICIPALITY_ID, NAMESPACE);
	}

	@Test
	void createConfiguration() {
		final var configuration = Configuration.create().withData(Map.of("enabled", List.of("true")));
		final var savedEntity = ConfigurationEntity.create()
			.withId("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
			.withMunicipalityId(MUNICIPALITY_ID)
			.withApplication(APPLICATION)
			.withNamespace(NAMESPACE)
			.withItems(List.of(ConfigurationItemEntity.create().withKey("enabled").withValue("true")));

		when(repositoryMock.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(false);
		when(repositoryMock.save(any(ConfigurationEntity.class))).thenReturn(savedEntity);

		final var result = service.createConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration);

		assertThat(result).isNotNull();
		assertThat(result.getData()).containsEntry("enabled", List.of("true"));
		verify(repositoryMock).existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verify(repositoryMock).save(any(ConfigurationEntity.class));
	}

	@Test
	void createConfigurationAlreadyExists() {
		final var configuration = Configuration.create().withData(Map.of("enabled", List.of("true")));
		when(repositoryMock.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(true);

		assertThatThrownBy(() -> service.createConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration))
			.isInstanceOf(ThrowableProblem.class)
			.satisfies(e -> assertThat(((ThrowableProblem) e).getStatus()).isEqualTo(CONFLICT));
		verify(repositoryMock).existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateConfiguration() {
		final var entity = ConfigurationEntity.create()
			.withApplication(APPLICATION)
			.withNamespace(NAMESPACE)
			.withItems(new java.util.ArrayList<>(List.of(ConfigurationItemEntity.create().withKey("old").withValue("data"))));
		final var configuration = Configuration.create().withData(Map.of("enabled", List.of("false")));
		final var updatedEntity = ConfigurationEntity.create()
			.withApplication(APPLICATION)
			.withNamespace(NAMESPACE)
			.withItems(List.of(ConfigurationItemEntity.create().withKey("enabled").withValue("false")));

		when(repositoryMock.findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(Optional.of(entity));
		when(repositoryMock.save(any(ConfigurationEntity.class))).thenReturn(updatedEntity);

		final var result = service.updateConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration);

		assertThat(result).isNotNull();
		assertThat(result.getData()).containsEntry("enabled", List.of("false"));
		verify(repositoryMock).findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verify(repositoryMock).save(any(ConfigurationEntity.class));
	}

	@Test
	void updateConfigurationNotFound() {
		final var configuration = Configuration.create().withData(Map.of("enabled", List.of("true")));
		when(repositoryMock.findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.updateConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE, configuration))
			.isInstanceOf(ThrowableProblem.class)
			.satisfies(e -> assertThat(((ThrowableProblem) e).getStatus()).isEqualTo(NOT_FOUND));
		verify(repositoryMock).findByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteConfiguration() {
		when(repositoryMock.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(true);

		service.deleteConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE);

		verify(repositoryMock).existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verify(repositoryMock).deleteByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
	}

	@Test
	void deleteConfigurationNotFound() {
		when(repositoryMock.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE)).thenReturn(false);

		assertThatThrownBy(() -> service.deleteConfiguration(MUNICIPALITY_ID, APPLICATION, NAMESPACE))
			.isInstanceOf(ThrowableProblem.class)
			.satisfies(e -> assertThat(((ThrowableProblem) e).getStatus()).isEqualTo(NOT_FOUND));
		verify(repositoryMock).existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, APPLICATION, NAMESPACE);
		verifyNoMoreInteractions(repositoryMock);
	}
}
