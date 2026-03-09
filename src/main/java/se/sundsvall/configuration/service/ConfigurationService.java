package se.sundsvall.configuration.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.configuration.api.model.Configuration;
import se.sundsvall.configuration.integration.db.ConfigurationRepository;
import se.sundsvall.configuration.service.mapper.ConfigurationMapper;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.configuration.service.mapper.ConfigurationMapper.toConfiguration;
import static se.sundsvall.configuration.service.mapper.ConfigurationMapper.toConfigurationEntity;
import static se.sundsvall.configuration.service.mapper.ConfigurationMapper.toConfigurationList;
import static se.sundsvall.configuration.service.mapper.ConfigurationMapper.updateEntity;

@Service
public class ConfigurationService {

	private static final String NOT_FOUND_MESSAGE = "Configuration for application '%s' and namespace '%s' not found in municipality '%s'";
	private static final String ALREADY_EXISTS_MESSAGE = "Configuration for application '%s' and namespace '%s' already exists in municipality '%s'";

	private final ConfigurationRepository repository;

	public ConfigurationService(final ConfigurationRepository repository) {
		this.repository = repository;
	}

	public Configuration getConfiguration(final String municipalityId, final String application, final String namespace) {
		return repository.findByMunicipalityIdAndApplicationAndNamespace(municipalityId, application, namespace)
			.map(ConfigurationMapper::toConfiguration)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, NOT_FOUND_MESSAGE.formatted(application, namespace, municipalityId)));
	}

	public List<Configuration> getConfigurationsByNamespace(final String municipalityId, final String namespace) {
		return toConfigurationList(repository.findAllByMunicipalityIdAndNamespace(municipalityId, namespace));
	}

	public Configuration createConfiguration(final String municipalityId, final String application, final String namespace, final Configuration configuration) {
		if (repository.existsByMunicipalityIdAndApplicationAndNamespace(municipalityId, application, namespace)) {
			throw Problem.valueOf(CONFLICT, ALREADY_EXISTS_MESSAGE.formatted(application, namespace, municipalityId));
		}
		final var entity = toConfigurationEntity(municipalityId, application, namespace, configuration);
		final var saved = repository.save(entity);
		return toConfiguration(saved);
	}

	@Transactional
	public Configuration updateConfiguration(final String municipalityId, final String application, final String namespace, final Configuration configuration) {
		final var entity = repository.findByMunicipalityIdAndApplicationAndNamespace(municipalityId, application, namespace)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, NOT_FOUND_MESSAGE.formatted(application, namespace, municipalityId)));
		updateEntity(entity, configuration);
		return toConfiguration(repository.save(entity));
	}

	@Transactional
	public void deleteConfiguration(final String municipalityId, final String application, final String namespace) {
		if (!repository.existsByMunicipalityIdAndApplicationAndNamespace(municipalityId, application, namespace)) {
			throw Problem.valueOf(NOT_FOUND, NOT_FOUND_MESSAGE.formatted(application, namespace, municipalityId));
		}
		repository.deleteByMunicipalityIdAndApplicationAndNamespace(municipalityId, application, namespace);
	}
}
