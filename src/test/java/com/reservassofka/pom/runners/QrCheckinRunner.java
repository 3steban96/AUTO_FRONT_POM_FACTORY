package com.reservassofka.pom.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features/qr_checkin.feature",
    glue = "com.reservassofka.pom.stepdefinitions",
    snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class QrCheckinRunner {}
