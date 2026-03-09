package se.sundsvall.configuration.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.configuration.integration.db.model.ConfigurationEntity;

@CircuitBreaker(name = "configurationRepository")
public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, String> {

	Optional<ConfigurationEntity> findByMunicipalityIdAndApplicationAndNamespace(final String municipalityId, final String application, final String namespace);

	List<ConfigurationEntity> findAllByMunicipalityIdAndNamespace(final String municipalityId, final String namespace);

	boolean existsByMunicipalityIdAndApplicationAndNamespace(final String municipalityId, final String application, final String namespace);

	void deleteByMunicipalityIdAndApplicationAndNamespace(final String municipalityId, final String application, final String namespace);
}
