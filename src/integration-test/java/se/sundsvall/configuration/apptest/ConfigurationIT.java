package se.sundsvall.configuration.apptest;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.configuration.Application;
import se.sundsvall.configuration.integration.db.ConfigurationRepository;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Configuration integration tests.
 *
 * @see src/integration-test/resources/db/scripts/testdata-it.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/ConfigurationIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ConfigurationIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "feature-flags";
	private static final String BASE_PATH = "/" + MUNICIPALITY_ID + "/" + NAMESPACE + "/configurations";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private ConfigurationRepository repository;

	@Test
	void test01_createConfiguration() {
		assertThat(repository.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, "new-app", "new-namespace")).isFalse();

		setupCall()
			.withServicePath("/" + MUNICIPALITY_ID + "/new-namespace/configurations/new-app")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, List.of("^/" + MUNICIPALITY_ID + "/new-namespace/configurations/new-app$"))
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		assertThat(repository.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, "new-app", "new-namespace")).isTrue();
	}

	@Test
	void test02_readConfiguration() {
		setupCall()
			.withServicePath(BASE_PATH + "/test-app")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_readConfigurationsByNamespace() {
		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_updateConfiguration() {
		setupCall()
			.withServicePath(BASE_PATH + "/test-app")
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_deleteConfiguration() {
		assertThat(repository.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, "test-app", "settings")).isTrue();

		setupCall()
			.withServicePath("/" + MUNICIPALITY_ID + "/settings/configurations/test-app")
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		assertThat(repository.existsByMunicipalityIdAndApplicationAndNamespace(MUNICIPALITY_ID, "test-app", "settings")).isFalse();
	}

	@Test
	void test06_readConfigurationNotFound() {
		setupCall()
			.withServicePath(BASE_PATH + "/non-existent")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_deleteConfigurationNotFound() {
		setupCall()
			.withServicePath(BASE_PATH + "/non-existent")
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
