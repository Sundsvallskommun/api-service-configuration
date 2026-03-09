package se.sundsvall.configuration.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import se.sundsvall.configuration.api.model.Configuration;
import se.sundsvall.configuration.service.ConfigurationService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/{namespace}/configurations")
@Tag(name = "Configurations", description = "Configuration management")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class ConfigurationResource {

	private final ConfigurationService service;

	ConfigurationResource(final ConfigurationService service) {
		this.service = service;
	}

	@GetMapping(path = "/{application}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get a configuration",
		description = "Returns a specific configuration for the given application and namespace",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	ResponseEntity<Configuration> getConfiguration(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final String namespace,
		@PathVariable final String application) {

		return ok(service.getConfiguration(municipalityId, application, namespace));
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all configurations in a namespace",
		description = "Returns all configurations for the given namespace",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
		})
	ResponseEntity<List<Configuration>> getConfigurationsByNamespace(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final String namespace) {

		return ok(service.getConfigurationsByNamespace(municipalityId, namespace));
	}

	@PostMapping(path = "/{application}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a configuration",
		description = "Creates a new configuration for the given application and namespace",
		responses = {
			@ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
			@ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	ResponseEntity<Void> createConfiguration(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final String namespace,
		@PathVariable final String application,
		@Valid @RequestBody final Configuration configuration) {

		service.createConfiguration(municipalityId, application, namespace, configuration);
		final var location = UriComponentsBuilder.fromPath("/{municipalityId}/{namespace}/configurations/{application}")
			.buildAndExpand(municipalityId, namespace, application)
			.toUri();
		return created(location).build();
	}

	@PutMapping(path = "/{application}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Update a configuration",
		description = "Updates an existing configuration for the given application and namespace",
		responses = {
			@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	ResponseEntity<Configuration> updateConfiguration(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final String namespace,
		@PathVariable final String application,
		@Valid @RequestBody final Configuration configuration) {

		return ok(service.updateConfiguration(municipalityId, application, namespace, configuration));
	}

	@DeleteMapping(path = "/{application}")
	@Operation(summary = "Delete a configuration",
		description = "Deletes a configuration for the given application and namespace",
		responses = {
			@ApiResponse(responseCode = "204", description = "No content"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	ResponseEntity<Void> deleteConfiguration(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final String namespace,
		@PathVariable final String application) {

		service.deleteConfiguration(municipalityId, application, namespace);
		return noContent().build();
	}
}
