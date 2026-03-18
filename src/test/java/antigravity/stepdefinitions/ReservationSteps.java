package antigravity.stepdefinitions;

import antigravity.ui.components.UpdateReservationModal;
import antigravity.ui.pages.LoginPage;
import antigravity.ui.pages.MyReservationsPage;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ReservationSteps - Cucumber Step Definitions
 *
 * Two credential sets are read from serenity.properties:
 *
 *   # For the login scenario (any valid registered user)
 *   test.login.email    = admin@sofka.com.co
 *   test.login.password = <password>
 *
 *   # For the reservation scenarios (user with at least one Proxima reservation)
 *   test.reservation.email    = esteban@sofka.com.co
 *   test.reservation.password = <password>
 */
public class ReservationSteps {

    private static final String BASE_URL         = "http://localhost:5173";
    private static final String RESERVATIONS_URL = "http://localhost:5173/my-reservations";

    // Load credentials from serenity.properties
    private static final String LOGIN_EMAIL;
    private static final String LOGIN_PASSWORD;
    private static final String RESERVATION_EMAIL;
    private static final String RESERVATION_PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream("serenity.properties")) {
            props.load(is);
        } catch (IOException e) {
            System.err.println("[WARN] Could not read serenity.properties: " + e.getMessage());
        }
        LOGIN_EMAIL           = props.getProperty("test.login.email",         "admin@sofka.com.co");
        LOGIN_PASSWORD        = props.getProperty("test.login.password",       "password123");
        RESERVATION_EMAIL     = props.getProperty("test.reservation.email",    "esteban@sofka.com.co");
        RESERVATION_PASSWORD  = props.getProperty("test.reservation.password", "password123");
    }

    LoginPage loginPage;
    MyReservationsPage myReservationsPage;
    UpdateReservationModal updateReservationModal;

    @Before
    public void setUp() {
        WebDriver driver = ThucydidesWebDriverSupport.getDriver();
        loginPage              = PageFactory.initElements(driver, LoginPage.class);
        myReservationsPage     = PageFactory.initElements(driver, MyReservationsPage.class);
        updateReservationModal = PageFactory.initElements(driver, UpdateReservationModal.class);
    }

    // ────────────────────────────────────────────────────────
    // Shared helpers
    // ────────────────────────────────────────────────────────

    /** Waits until the browser URL changes away from the root / login page. */
    private void waitForRedirectAfterLogin() {
        WebDriver driver = ThucydidesWebDriverSupport.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> !d.getCurrentUrl().equals(BASE_URL)
                     && !d.getCurrentUrl().equals(BASE_URL + "/")
                     && !d.getCurrentUrl().contains("/login"));
    }

    /** Full login + navigate to /my-reservations for the given user. */
    private void loginAndGoToReservations(String email, String password) {
        WebDriver driver = ThucydidesWebDriverSupport.getDriver();
        driver.get(BASE_URL);
        loginPage.loginAs(email, password);
        waitForRedirectAfterLogin();
        driver.get(RESERVATIONS_URL);
    }

    // ────────────────────────────────────────────────────────
    // Scenario 1: Successful login — uses LOGIN credentials
    // ────────────────────────────────────────────────────────

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        ThucydidesWebDriverSupport.getDriver().get(BASE_URL);
    }

    @When("the user enters valid credentials")
    public void theUserEntersValidCredentials() {
        // Uses the "login user" (e.g. admin@sofka.com.co)
        loginPage.loginAs(LOGIN_EMAIL, LOGIN_PASSWORD);
    }

    @Then("the user should see the reservations dashboard")
    public void theUserShouldSeeTheReservationsDashboard() {
        waitForRedirectAfterLogin();
        ThucydidesWebDriverSupport.getDriver().get(RESERVATIONS_URL);
        assertThat(myReservationsPage.isDashboardVisible())
                .as("Reservations list should be visible at /my-reservations")
                .isTrue();
    }

    // ────────────────────────────────────────────────────────
    // Scenario 2: Invalid time range — uses RESERVATION credentials
    // ────────────────────────────────────────────────────────

    @Given("the user is logged in and has at least one upcoming reservation")
    public void theUserIsLoggedInAndHasAnUpcomingReservation() {
        // Uses the "reservation user" (e.g. esteban@sofka.com.co) who has Proxima reservations
        loginAndGoToReservations(RESERVATION_EMAIL, RESERVATION_PASSWORD);
        assertThat(myReservationsPage.isDashboardVisible())
                .as("Reservations list must be visible before editing")
                .isTrue();
    }

    @When("the user tries to update the reservation with a start time greater than or equal to end time")
    public void theUserTriesToUpdateWithInvalidTimes() {
        myReservationsPage.clickFirstEditButton();
        updateReservationModal.waitForModal();
        updateReservationModal.setStartTime("15:00");
        updateReservationModal.setEndTime("14:00");   // start > end — triggers validation error
        updateReservationModal.clickSave();
    }

    @Then("the system should show an error message with {string}")
    public void theSystemShouldShowAnErrorMessage(String expectedMessage) {
        assertThat(updateReservationModal.getErrorMessage())
                .as("Error message should mention the invalid time range")
                .contains(expectedMessage);
    }

    // ────────────────────────────────────────────────────────
    // Scenario 3: Successful update — uses RESERVATION credentials
    // ────────────────────────────────────────────────────────

    @When("the user updates the reservation with valid start time {string} and end time {string}")
    public void theUserUpdatesWithValidTimes(String startTime, String endTime) {
        myReservationsPage.clickFirstEditButton();
        updateReservationModal.waitForModal();
        updateReservationModal.setStartTime(startTime);
        updateReservationModal.setEndTime(endTime);
        updateReservationModal.clickSave();
    }

    @Then("the reservation should be saved and the modal should close")
    public void theReservationShouldBeSaved() {
        assertThat(updateReservationModal.isModalClosed())
                .as("Modal should close automatically after a successful update")
                .isTrue();
    }
}
