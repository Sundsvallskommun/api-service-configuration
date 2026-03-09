package se.sundsvall.configuration;

import se.sundsvall.dept44.ServiceApplication;

import static org.springframework.boot.SpringApplication.run;

@ServiceApplication
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
