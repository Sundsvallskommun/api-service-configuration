package se.sundsvall.configuration.api.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
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

class ConfigurationTest {

	@Test
	void testBean() {
		registerValueGenerator(OffsetDateTime::now, OffsetDateTime.class);

		assertThat(Configuration.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var application = "my-app";
		final var namespace = "feature-flags";
		final var data = Map.of("enabled", List.of("true"));
		final var createdAt = now();
		final var updatedAt = now();

		final var result = Configuration.create()
			.withApplication(application)
			.withNamespace(namespace)
			.withData(data)
			.withCreatedAt(createdAt)
			.withUpdatedAt(updatedAt);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getApplication()).isEqualTo(application);
		assertThat(result.getNamespace()).isEqualTo(namespace);
		assertThat(result.getData()).isEqualTo(data);
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Configuration.create()).hasAllNullFieldsOrProperties();
	}
}
