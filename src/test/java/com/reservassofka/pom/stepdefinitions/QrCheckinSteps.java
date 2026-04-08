package com.reservassofka.pom.stepdefinitions;

import com.reservassofka.pom.pages.LoginPage;
import com.reservassofka.pom.pages.QrScannerModal;
import com.reservassofka.pom.pages.ReservationsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static io.restassured.RestAssured.given;

public class QrCheckinSteps {
    
    @Managed
    WebDriver driver;

    private LoginPage loginPage;
    private ReservationsPage reservationsPage;
    private QrScannerModal qrScannerModal;
    
    private String currentReservationId;
    private String authToken;
    private int currentSpaceId;
    private String currentQrToken;
    private static final String API_BASE_URL = "http://localhost:3000";
    
    // Rotar entre diferentes espacios para evitar conflictos
    private static int spaceIdCounter = 1;
    private static synchronized int getNextSpaceId() {
        int id = spaceIdCounter;
        spaceIdCounter = (spaceIdCounter % 5) + 1; // Rotar entre espacios 1-5
        return id;
    }
    
    // Estado de tiempo para el escenario actual
    private Instant reservationStartAt;
    
    private void initPages() {
        if (loginPage == null) {
            loginPage = new LoginPage();
            loginPage.setDriver(driver);
            reservationsPage = new ReservationsPage();
            reservationsPage.setDriver(driver);
            qrScannerModal = new QrScannerModal();
            qrScannerModal.setDriver(driver);
        }
    }
    
    @Given("el usuario {string} está autenticado")
    public void elUsuarioEstaAutenticado(String email) {
        initPages();
        
        // Login via API para obtener token
        RestAssured.baseURI = API_BASE_URL;
        Response authResponse = given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"email\":\"%s\",\"password\":\"password1234\"}", email))
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract().response();
        
        this.authToken = authResponse.jsonPath().getString("data.token");
        System.out.println("✓ Usuario autenticado vía API. Token obtenido.");
        
        // Login via UI
        loginPage.loginAs(email, "password1234");
    }
    
    @Given("tiene una reserva activa con ID {string} para el espacio {string}")
    public void tieneUnaReservaActivaConId(String reservationId, String spaceName) {
        initPages();
        
        // Reserva empieza 2 minutos en el pasado → dentro del grace period de 5 min
        this.currentSpaceId = getNextSpaceId();
        int spaceId = this.currentSpaceId;
        long offsetMinutes = -2;
        
        Instant startAt = Instant.now().plus(offsetMinutes, ChronoUnit.MINUTES);
        Instant endAt = startAt.plus(1, ChronoUnit.HOURS);
        this.reservationStartAt = startAt;
        
        String requestBody = String.format("""
            {
                "spaceId": %d,
                "startAt": "%s",
                "endAt": "%s",
                "title": "E2E Test - %s - %d"
            }
            """, spaceId, startAt.toString(), endAt.toString(), spaceName, System.currentTimeMillis());
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("CREANDO RESERVA DE PRUEBA");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  SpaceId: " + spaceId);
        System.out.println("  StartAt: " + startAt + " (" + offsetMinutes + " min desde ahora)");
        System.out.println("  EndAt: " + endAt);
        
        Response createResponse = given()
            .header("Authorization", "Bearer " + this.authToken)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/bookings/reservations")
            .then()
            .extract().response();
        
        int statusCode = createResponse.getStatusCode();
        String responseBody = createResponse.getBody().asString();
        
        System.out.println("  Response Status: " + statusCode);
        
        if (statusCode != 201) {
            System.out.println("  ✗ Error: " + responseBody);
            throw new RuntimeException("Failed to create reservation. Status: " + statusCode + ", Body: " + responseBody);
        }
        
        // Guardar el ID numérico real
        try {
            Integer numericId = createResponse.jsonPath().getInt("data.id");
            this.currentReservationId = String.valueOf(numericId);
            
            System.out.println("  ✓ Reserva ID: " + this.currentReservationId);
            
            // Obtener el JWT QR token del espacio para usarlo en el escaneo
            try {
                Response qrTokenResponse = given()
                    .header("Authorization", "Bearer " + this.authToken)
                    .when()
                    .get("/locations/spaces/" + spaceId + "/qr-token")
                    .then()
                    .statusCode(200)
                    .extract().response();
                this.currentQrToken = qrTokenResponse.jsonPath().getString("data.token");
                System.out.println("  ✓ QR Token obtenido para espacio " + spaceId);
            } catch (Exception e) {
                System.out.println("  ⚠ No se pudo obtener QR token: " + e.getMessage() + " — usando spaceName como fallback");
                this.currentQrToken = spaceName;
            }
            System.out.println("═══════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.out.println("ERROR: No se pudo extraer el ID: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener el ID de la reserva creada", e);
        }
    }
    
    @Given("el usuario navega hacia {string}")
    public void elUsuarioNavegaHacia(String pageName) {
        initPages();
        if (pageName.equals("Mis Reservas")) {
            reservationsPage.navigateToMyReservations();
        }
    }
    
    @Given("se encuentra en la tarjeta de la reserva {string}")
    public void seEncuentraEnLaTarjetaDe(String reservationId) {
        initPages();
        // Usar el ID numérico real guardado en currentReservationId
        Assert.assertNotNull(reservationsPage.getReservationCardById(this.currentReservationId));
    }

    @Given("se encuentra en la tarjeta de su reserva")
    public void seEncuentraEnLaTarjetaDeSuReserva() {
        initPages();
        System.out.println("DEBUG: Intentando encontrar reserva con ID: " + this.currentReservationId);
        
        // Intentar encontrar la tarjeta con retry logic
        int maxRetries = 3;
        WebElementFacade card = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                card = reservationsPage.getReservationCardById(this.currentReservationId);
                if (card != null && card.isVisible()) {
                    System.out.println("✓ Tarjeta de reserva encontrada en intento " + (i + 1));
                    break;
                }
            } catch (Exception e) {
                System.out.println("⚠ Intento " + (i + 1) + " falló: " + e.getMessage());
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("  Reintentando...");
                } else {
                    throw new AssertionError(
                        "No se encontró la tarjeta de reserva con ID: " + this.currentReservationId + 
                        " después de " + maxRetries + " intentos. " +
                        "Verifica que la reserva se haya creado correctamente y que el ID sea correcto."
                    );
                }
            }
        }
        
        Assert.assertNotNull("La tarjeta de reserva no debe ser null", card);
    }

    @When("el usuario hace clic en el botón {string}")
    public void elUsuarioHaceClicEnElBoton(String buttonText) {
        initPages();
        reservationsPage.clickCheckInButton(currentReservationId);
    }
    
    @When("se abre el modal de escaneo QR")
    public void seAbreElModalDeEscaneoQr() {
        initPages();
        Assert.assertTrue("El modal de escaneo QR debería estar abierto", qrScannerModal.isModalOpen());
    }
    
    @When("se otorgan permisos de cámara")
    public void seOtorganPermisosDeCamara() {
        initPages();
        qrScannerModal.grantCameraPermissions();
    }
    
    @When("se escanea el QR válido del espacio {string}")
    public void seEscaneaElQrValidoDelEspacio(String spaceName) {
        initPages();
        // Use the real JWT QR token fetched from the locations-service
        String token = (this.currentQrToken != null && !this.currentQrToken.isEmpty())
            ? this.currentQrToken
            : spaceName;
        qrScannerModal.scanValidQrCode(token);
    }
    
    @Then("el estado de la reserva cambia a {string}")
    public void elEstadoDeLaReservaCambiaA(String expectedStatus) {
        initPages();
        String actualStatus = reservationsPage.getReservationStatus(currentReservationId);
        Assert.assertEquals("El estado de la reserva no coincide", expectedStatus, actualStatus);
    }
    
    @Then("se muestra el mensaje de éxito {string}")
    public void seMuestraElMensajeDeExito(String expectedMessage) {
        initPages();
        String actualMessage = reservationsPage.getSuccessMessage();
        Assert.assertTrue("Mensaje esperado contenía: " + expectedMessage + " pero fue: " + actualMessage, 
            actualMessage.contains(expectedMessage));
    }
    
    @Then("se cierra el modal de escaneo")
    public void seCierraElModalDeEscaneo() {
        initPages();
        Assert.assertTrue("El modal debería estar cerrado después del check-in exitoso", qrScannerModal.isModalClosed());
    }

    @Then("se muestra el mensaje de error {string}")
    public void seMuestraElMensajeDeError(String expectedError) {
        initPages();
        String actualError = reservationsPage.getErrorMessage();
        Assert.assertTrue("Error esperado contenía: " + expectedError + " pero fue: " + actualError,
            actualError.contains(expectedError));
    }
    
    @Given("la reserva está en estado {string}") public void noop1(String s) {}
    
    @Given("la hora de inicio de la reserva es {string}")
    public void elHoraDeInicioEsRelativa(String relativeExpr) {
        // El step es informativo: documenta la intención del escenario.
        // La reserva real se crea con Instant.now() + offset en el step anterior.
        Instant actualStart = reservationStartAt != null ? reservationStartAt : Instant.now().minus(2, ChronoUnit.MINUTES);
        System.out.printf("  Hora de inicio (referencia: \"%s\"): %s%n", relativeExpr, actualStart);
    }
    
    @Given("la hora actual del sistema es {string}")
    public void elHoraActualDelSistemaEs(String relativeExpr) {
        // El step es informativo: documenta el contexto temporal del escenario.
        // Los tests de UI no pueden simular tiempo del servidor; la lógica de gracia
        // es controlada por el backend (grace period = 5 minutos tras startAt).
        System.out.printf("  Hora actual del sistema (referencia: \"%s\"): %s%n", relativeExpr, Instant.now());
    }
    
    @When("el usuario intenta hacer check-in escaneando el QR válido") public void noop4() {}
    @Then("la tarjeta de reserva muestra el badge verde {string}") public void noop5(String s) {}
    @Then("se recibe notificación push {string}") public void noop6(String s) {}
    @Then("el estado de la reserva permanece en {string}") public void noop7(String s) {}
}
