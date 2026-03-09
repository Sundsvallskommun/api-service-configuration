package se.sundsvall.configuration.service.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import se.sundsvall.configuration.api.model.Configuration;
import se.sundsvall.configuration.integration.db.model.ConfigurationEntity;
import se.sundsvall.configuration.integration.db.model.ConfigurationItemEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public final class ConfigurationMapper {

	private ConfigurationMapper() {}

	public static Configuration toConfiguration(final ConfigurationEntity entity) {
		return ofNullable(entity)
			.map(e -> Configuration.create()
				.withApplication(e.getApplication())
				.withNamespace(e.getNamespace())
				.withData(toDataMap(e.getItems()))
				.withCreatedAt(e.getCreatedAt())
				.withUpdatedAt(e.getUpdatedAt()))
			.orElse(null);
	}

	public static ConfigurationEntity toConfigurationEntity(final String municipalityId, final String application, final String namespace, final Configuration configuration) {
		return ofNullable(configuration)
			.map(c -> {
				final var entity = ConfigurationEntity.create()
					.withMunicipalityId(municipalityId)
					.withApplication(application)
					.withNamespace(namespace);
				entity.setItems(toItemEntities(c.getData(), entity));
				return entity;
			})
			.orElse(null);
	}

	public static List<Configuration> toConfigurationList(final List<ConfigurationEntity> entities) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(ConfigurationMapper::toConfiguration)
				.toList())
			.orElse(emptyList());
	}

	public static void updateEntity(final ConfigurationEntity entity, final Configuration configuration) {
		ofNullable(configuration).ifPresent(c -> {
			entity.getItems().clear();
			entity.getItems().addAll(toItemEntities(c.getData(), entity));
		});
	}

	static Map<String, List<String>> toDataMap(final List<ConfigurationItemEntity> items) {
		return ofNullable(items)
			.map(list -> list.stream()
				.collect(groupingBy(
					ConfigurationItemEntity::getKey,
					LinkedHashMap::new,
					mapping(ConfigurationItemEntity::getValue, toList()))))
			.orElse(null);
	}

	static List<ConfigurationItemEntity> toItemEntities(final Map<String, List<String>> data, final ConfigurationEntity parent) {
		return ofNullable(data)
			.map(map -> map.entrySet().stream()
				.flatMap(entry -> ofNullable(entry.getValue()).orElse(emptyList()).stream()
					.map(value -> ConfigurationItemEntity.create()
						.withConfigurationEntity(parent)
						.withKey(entry.getKey())
						.withValue(value)))
				.toList())
			.map(ArrayList::new)
			.orElse(new ArrayList<>());
	}
}
