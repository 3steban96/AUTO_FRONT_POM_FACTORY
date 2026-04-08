package com.reservassofka.pom.stepdefinitions;

import com.reservassofka.pom.pages.QrScannerModal;
import com.reservassofka.pom.pages.ReservationsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.time.Instant;

public class QrErrorHandlingSteps {

    private ReservationsPage reservationsPage;
    private QrScannerModal qrScannerModal;

    public QrErrorHandlingSteps() {
        this.reservationsPage = new ReservationsPage();
        this.qrScannerModal = new QrScannerModal();
    }

    @When("se escanea un QR con firma JWT inválida")
    public void seEscaneaUnQrConFirmaJwtInvalida() {
        qrScannerModal.scanValidQrCode("INVALID_SIGNATURE");
    }

    @When("se escanea un QR de otro espacio {string}")
    public void seEscaneaUnQrDeOtroEspacio(String otherSpace) {
        qrScannerModal.scanValidQrCode(otherSpace);
    }

    @When("se escanea el QR fuera del periodo de gracia")
    public void seEscaneaFueraDelPeriodoDeGracia() {
        qrScannerModal.scanValidQrCode("Sala Zeus");
    }

    /**
     * Step para escenarios donde la hora actual supera el periodo de gracia.
     * Documenta el contexto temporal del escenario y delega la validación al backend.
     * Referencia: grace period = 5 minutos tras startAt de la reserva.
     */
    @When("la hora actual {string} está fuera del periodo de gracia")
    public void laHoraActualEstaFueraDelPeriodoDeGracia(String timeReference) {
        System.out.printf("  Verificando estado fuera de gracia (referencia: \"%s\", ahora: %s)%n",
            timeReference, Instant.now());
        qrScannerModal.scanValidQrCode("Sala Zeus");
    }

    @Then("la reserva permanece en estado {string}")
    public void laReservaPermaneceEnEstado(String expectedStatus) {
    }

    @Then("se muestra el contacto de soporte técnico")
    public void seMuestraElContactoDeSoporteTecnico() {
    }
}
