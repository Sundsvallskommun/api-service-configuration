package se.sundsvall.configuration.integration.db.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class ConfigurationEntityTest {

	@Test
	void testBean() {
		registerValueGenerator(() -> now(), OffsetDateTime.class);
		assertThat(ConfigurationEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var id = UUID.randomUUID().toString();
		final var municipalityId = "2281";
		final var application = "my-app";
		final var namespace = "feature-flags";
		final var items = List.of(ConfigurationItemEntity.create().withKey("key").withValue("value"));
		final var createdAt = now();
		final var updatedAt = now();

		final var result = ConfigurationEntity.create()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withApplication(application)
			.withNamespace(namespace)
			.withItems(items)
			.withCreatedAt(createdAt)
			.withUpdatedAt(updatedAt);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getApplication()).isEqualTo(application);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getItems()).isEqualTo(items);
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ConfigurationEntity.create()).hasAllNullFieldsOrPropertiesExcept("items");
		assertThat(new ConfigurationEntity()).hasAllNullFieldsOrPropertiesExcept("items");
	}
}
