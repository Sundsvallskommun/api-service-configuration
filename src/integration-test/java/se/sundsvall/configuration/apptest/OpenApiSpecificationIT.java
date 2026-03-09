package se.sundsvall.configuration.apptest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import se.sundsvall.configuration.Application;
import se.sundsvall.dept44.util.ResourceUtils;
import tools.jackson.dataformat.yaml.YAMLMapper;

import static java.nio.file.Files.writeString;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;

@ActiveProfiles("it")
@AutoConfigureTestRestTemplate
@SpringBootTest(
	webEnvironment = WebEnvironment.RANDOM_PORT,
	classes = Application.class,
	properties = {
		"spring.main.banner-mode=off",
		"logging.level.se.sundsvall.dept44.payload=OFF",
		"wiremock.server.port=10101"
	})
class OpenApiSpecificationIT {

	private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

	@Value("${openapi.name}")
	private String openApiName;

	@Value("${openapi.version}")
	private String openApiVersion;

	@Value("classpath:/api/openapi.yaml")
	private Resource openApiResource;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void compareOpenApiSpecifications() throws IOException {
		final String currentOpenApiSpecification = getCurrentOpenApiSpecification();

		writeString(Path.of("target/api.yaml"), currentOpenApiSpecification);

		final String existingOpenApiSpecification = ResourceUtils.asString(openApiResource);
		assertThatJson(toJson(currentOpenApiSpecification))
			.withOptions(List.of(IGNORING_ARRAY_ORDER))
			.whenIgnoringPaths("servers")
			.isEqualTo(toJson(existingOpenApiSpecification));
	}

	private String getCurrentOpenApiSpecification() {
		final var uri = UriComponentsBuilder.fromPath("/api-docs.yaml")
			.buildAndExpand(openApiName, openApiVersion)
			.toUri();

		return restTemplate.getForObject(uri, String.class);
	}

	private String toJson(final String yaml) {
		return YAML_MAPPER.readTree(yaml).toString();
	}
}
