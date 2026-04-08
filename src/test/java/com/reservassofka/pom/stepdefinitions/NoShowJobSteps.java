package com.reservassofka.pom.stepdefinitions;

import com.reservassofka.pom.pages.ReservationsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class NoShowJobSteps {

    private ReservationsPage reservationsPage;

    public NoShowJobSteps() {
        this.reservationsPage = new ReservationsPage();
    }

    @Given("que el No-Show Job se ejecuta para las reservas vencidas")
    public void noShowJobEjecuta() {
    }

    @When("la reserva con ID {string} no ha realizado check-in después de {int} minutos")
    public void reservaNoHaRealizadoCheckIn(String id, Integer minutes) {
    }

    @Then("la reserva {string} cambia automáticamente a estado {string}")
    public void reservaCambiaAEstado(String id, String expectedStatus) {
        String actualStatus = reservationsPage.getReservationStatus(id);
        Assert.assertEquals("El estado de la reserva no cambió según lo esperado", expectedStatus, actualStatus);
    }
}
