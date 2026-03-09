package se.sundsvall.configuration.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Schema(description = "Configuration model")
public class Configuration {

	@Schema(description = "Application identifier", examples = "my-app", accessMode = READ_ONLY)
	private String application;

	@Schema(description = "Configuration namespace", examples = "feature-flags", accessMode = READ_ONLY)
	private String namespace;

	@Schema(description = "Configuration data as key-value pairs", examples = "{\"enabled\": [\"true\"], \"roles\": [\"admin\", \"editor\"]}")
	@NotEmpty
	private Map<String, List<String>> data;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	@DateTimeFormat(iso = DATE_TIME)
	private OffsetDateTime createdAt;

	@Schema(description = "Last updated timestamp", accessMode = READ_ONLY)
	@DateTimeFormat(iso = DATE_TIME)
	private OffsetDateTime updatedAt;

	public static Configuration create() {
		return new Configuration();
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(final String application) {
		this.application = application;
	}

	public Configuration withApplication(final String application) {
		this.application = application;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public Configuration withNamespace(final String namespace) {
		this.namespace = namespace;
		return this;
	}

	public Map<String, List<String>> getData() {
		return data;
	}

	public void setData(final Map<String, List<String>> data) {
		this.data = data;
	}

	public Configuration withData(final Map<String, List<String>> data) {
		this.data = data;
		return this;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(final OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Configuration withCreatedAt(final OffsetDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(final OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Configuration withUpdatedAt(final OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final var that = (Configuration) o;
		return Objects.equals(application, that.application)
			&& Objects.equals(namespace, that.namespace)
			&& Objects.equals(data, that.data)
			&& Objects.equals(createdAt, that.createdAt)
			&& Objects.equals(updatedAt, that.updatedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(application, namespace, data, createdAt, updatedAt);
	}

	@Override
	public String toString() {
		return "Configuration{" +
			"application='" + application + '\'' +
			", namespace='" + namespace + '\'' +
			", data=" + data +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}
