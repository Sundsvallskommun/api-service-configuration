package se.sundsvall.configuration.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "configuration_item")
public class ConfigurationItemEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "configuration_id", nullable = false, foreignKey = @ForeignKey(name = "fk_configuration_item_configuration_id"))
	private ConfigurationEntity configurationEntity;

	@Column(name = "item_key", nullable = false)
	private String key;

	@Column(name = "item_value", length = 2000)
	private String value;

	public static ConfigurationItemEntity create() {
		return new ConfigurationItemEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ConfigurationItemEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public ConfigurationEntity getConfigurationEntity() {
		return configurationEntity;
	}

	public void setConfigurationEntity(final ConfigurationEntity configurationEntity) {
		this.configurationEntity = configurationEntity;
	}

	public ConfigurationItemEntity withConfigurationEntity(final ConfigurationEntity configurationEntity) {
		this.configurationEntity = configurationEntity;
		return this;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public ConfigurationItemEntity withKey(final String key) {
		this.key = key;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public ConfigurationItemEntity withValue(final String value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final var that = (ConfigurationItemEntity) o;
		return Objects.equals(id, that.id)
			&& Objects.equals(key, that.key)
			&& Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, key, value);
	}

	@Override
	public String toString() {
		return "ConfigurationItemEntity{" +
			"id='" + id + '\'' +
			", key='" + key + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}
