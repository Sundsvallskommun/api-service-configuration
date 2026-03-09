package se.sundsvall.configuration;

import se.sundsvall.dept44.ServiceApplication;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

import static org.springframework.boot.SpringApplication.run;

@ExcludeFromJacocoGeneratedCoverageReport
@ServiceApplication
public class Application {

	static void main(final String[] args) {
		run(Application.class, args);
	}
}
