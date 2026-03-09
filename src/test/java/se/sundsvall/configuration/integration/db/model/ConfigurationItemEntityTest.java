package se.sundsvall.configuration.integration.db.model;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class ConfigurationItemEntityTest {

	@Test
	void testBean() {
		assertThat(ConfigurationItemEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("configurationEntity"),
			hasValidBeanEqualsExcluding("configurationEntity"),
			hasValidBeanToStringExcluding("configurationEntity")));
	}

	@Test
	void testBuilderMethods() {
		final var id = UUID.randomUUID().toString();
		final var configurationEntity = ConfigurationEntity.create().withId("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
		final var key = "feature.enabled";
		final var value = "true";

		final var result = ConfigurationItemEntity.create()
			.withId(id)
			.withConfigurationEntity(configurationEntity)
			.withKey(key)
			.withValue(value);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getConfigurationEntity()).isEqualTo(configurationEntity);
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getValue()).isEqualTo(value);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ConfigurationItemEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new ConfigurationItemEntity()).hasAllNullFieldsOrProperties();
	}
}
