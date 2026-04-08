package com.reservassofka.pom.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        plugin = {"pretty"},
        features = "src/test/resources/features/reservations.feature",
        glue = "com.reservassofka.pom.stepdefinitions",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class ReservationTestRunner {
}

