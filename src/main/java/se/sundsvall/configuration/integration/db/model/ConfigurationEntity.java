package se.sundsvall.configuration.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "configuration",
	uniqueConstraints = @UniqueConstraint(name = "uq_configuration", columnNames = {
		"municipality_id", "application", "namespace"
	}),
	indexes = @Index(name = "idx_configuration_municipality_namespace", columnList = "municipality_id, namespace"))
public class ConfigurationEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "municipality_id", nullable = false, length = 4)
	private String municipalityId;

	@Column(name = "application", nullable = false)
	private String application;

	@Column(name = "namespace", nullable = false)
	private String namespace;

	@OneToMany(mappedBy = "configurationEntity", cascade = ALL, orphanRemoval = true, fetch = EAGER)
	private List<ConfigurationItemEntity> items = new ArrayList<>();

	@Column(name = "created_at")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime updatedAt;

	public static ConfigurationEntity create() {
		return new ConfigurationEntity();
	}

	@PrePersist
	void prePersist() {
		createdAt = now().truncatedTo(MILLIS);
		updatedAt = createdAt;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = now().truncatedTo(MILLIS);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ConfigurationEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public ConfigurationEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(final String application) {
		this.application = application;
	}

	public ConfigurationEntity withApplication(final String application) {
		this.application = application;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	public ConfigurationEntity withNamespace(final String namespace) {
		this.namespace = namespace;
		return this;
	}

	public List<ConfigurationItemEntity> getItems() {
		return items;
	}

	public void setItems(final List<ConfigurationItemEntity> items) {
		this.items = items;
	}

	public ConfigurationEntity withItems(final List<ConfigurationItemEntity> items) {
		this.items = items;
		return this;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(final OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ConfigurationEntity withCreatedAt(final OffsetDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(final OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ConfigurationEntity withUpdatedAt(final OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final var that = (ConfigurationEntity) o;
		return Objects.equals(id, that.id)
			&& Objects.equals(municipalityId, that.municipalityId)
			&& Objects.equals(application, that.application)
			&& Objects.equals(namespace, that.namespace)
			&& Objects.equals(items, that.items)
			&& Objects.equals(createdAt, that.createdAt)
			&& Objects.equals(updatedAt, that.updatedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, municipalityId, application, namespace, items, createdAt, updatedAt);
	}

	@Override
	public String toString() {
		return "ConfigurationEntity{" +
			"id='" + id + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", application='" + application + '\'' +
			", namespace='" + namespace + '\'' +
			", items=" + items +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}
