package se.sundsvall.configuration.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.configuration.api.model.Configuration;
import se.sundsvall.configuration.integration.db.model.ConfigurationEntity;
import se.sundsvall.configuration.integration.db.model.ConfigurationItemEntity;

import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationMapperTest {

	private static Stream<Arguments> toConfigurationArguments() {
		final var createdAt = now();
		final var updatedAt = now();
		final var entity = ConfigurationEntity.create()
			.withApplication("app")
			.withNamespace("ns")
			.withCreatedAt(createdAt)
			.withUpdatedAt(updatedAt)
			.withItems(List.of(
				ConfigurationItemEntity.create().withKey("key1").withValue("val1"),
				ConfigurationItemEntity.create().withKey("key1").withValue("val2"),
				ConfigurationItemEntity.create().withKey("key2").withValue("val3")));

		final var expected = Configuration.create()
			.withApplication("app")
			.withNamespace("ns")
			.withCreatedAt(createdAt)
			.withUpdatedAt(updatedAt)
			.withData(Map.of(
				"key1", List.of("val1", "val2"),
				"key2", List.of("val3")));

		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(entity, expected));
	}

	private static Stream<Arguments> toConfigurationEntityArguments() {
		return Stream.of(
			Arguments.of(null, true),
			Arguments.of(
				Configuration.create().withData(Map.of("enabled", List.of("true"), "name", List.of("test"))),
				false));
	}

	@ParameterizedTest
	@MethodSource("toConfigurationArguments")
	void toConfiguration(final ConfigurationEntity input, final Configuration expected) {
		final var result = ConfigurationMapper.toConfiguration(input);

		if (expected == null) {
			assertThat(result).isNull();
		} else {
			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}
	}

	@ParameterizedTest
	@MethodSource("toConfigurationEntityArguments")
	void toConfigurationEntity(final Configuration input, final boolean expectNull) {
		final var result = ConfigurationMapper.toConfigurationEntity("2281", "app", "ns", input);

		if (expectNull) {
			assertThat(result).isNull();
		} else {
			assertThat(result).isNotNull();
			assertThat(result.getMunicipalityId()).isEqualTo("2281");
			assertThat(result.getApplication()).isEqualTo("app");
			assertThat(result.getNamespace()).isEqualTo("ns");
			assertThat(result.getItems()).hasSize(2);
			assertThat(result.getItems()).extracting(ConfigurationItemEntity::getKey)
				.containsExactlyInAnyOrder("enabled", "name");
			assertThat(result.getItems()).extracting(ConfigurationItemEntity::getValue)
				.containsExactlyInAnyOrder("true", "test");
		}
	}

	@Test
	void toConfigurationList() {
		final var entities = List.of(
			ConfigurationEntity.create().withApplication("app1").withNamespace("ns1").withItems(List.of()),
			ConfigurationEntity.create().withApplication("app2").withNamespace("ns2").withItems(List.of()));

		final var result = ConfigurationMapper.toConfigurationList(entities);

		assertThat(result).hasSize(2);
		assertThat(result).extracting(Configuration::getApplication).containsExactly("app1", "app2");
		assertThat(result).extracting(Configuration::getNamespace).containsExactly("ns1", "ns2");
	}

	@Test
	void toConfigurationListWithNull() {
		assertThat(ConfigurationMapper.toConfigurationList(null)).isEmpty();
	}

	@Test
	void updateEntity() {
		final var entity = ConfigurationEntity.create()
			.withApplication("app")
			.withNamespace("ns")
			.withItems(new ArrayList<>(List.of(
				ConfigurationItemEntity.create().withKey("old").withValue("data"))));

		final var configuration = Configuration.create()
			.withData(Map.of("new", List.of("value1", "value2")));

		ConfigurationMapper.updateEntity(entity, configuration);

		assertThat(entity.getItems()).hasSize(2);
		assertThat(entity.getItems()).extracting(ConfigurationItemEntity::getKey).containsOnly("new");
		assertThat(entity.getItems()).extracting(ConfigurationItemEntity::getValue).containsExactlyInAnyOrder("value1", "value2");
	}

	@Test
	void updateEntityWithNull() {
		final var entity = ConfigurationEntity.create().withItems(new ArrayList<>());
		ConfigurationMapper.updateEntity(entity, null);
		assertThat(entity.getItems()).isEmpty();
	}

	@Test
	void toDataMap() {
		final var items = List.of(
			ConfigurationItemEntity.create().withKey("k1").withValue("v1"),
			ConfigurationItemEntity.create().withKey("k1").withValue("v2"),
			ConfigurationItemEntity.create().withKey("k2").withValue("v3"));

		final var result = ConfigurationMapper.toDataMap(items);

		assertThat(result).hasSize(2);
		assertThat(result.get("k1")).containsExactly("v1", "v2");
		assertThat(result.get("k2")).containsExactly("v3");
	}

	@Test
	void toDataMapWithNull() {
		assertThat(ConfigurationMapper.toDataMap(null)).isNull();
	}

	@Test
	void toItemEntities() {
		final var parent = ConfigurationEntity.create();
		final var data = Map.of("key1", List.of("a", "b"), "key2", List.of("c"));

		final var result = ConfigurationMapper.toItemEntities(data, parent);

		assertThat(result).hasSize(3)
			.allSatisfy(item -> assertThat(item.getConfigurationEntity()).isSameAs(parent));
	}

	@Test
	void toItemEntitiesWithNull() {
		final var result = ConfigurationMapper.toItemEntities(null, ConfigurationEntity.create());
		assertThat(result).isEmpty();
	}
}
