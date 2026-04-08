package com.reservassofka.pom.stepdefinitions;

import com.reservassofka.pom.pages.LoginPage;
import com.reservassofka.pom.pages.ReservationsPage;
import com.reservassofka.pom.pages.UpdateReservationModal;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReservationSteps {

    private static String RESERVATION_EMAIL;
    private static String RESERVATION_PASSWORD;

    static {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("serenity.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        RESERVATION_EMAIL = props.getProperty("test.reservation.email", "admin@sofka.com.co");
        RESERVATION_PASSWORD  = props.getProperty("test.reservation.password", "password1234");
    }

    private LoginPage loginPage;
    private ReservationsPage reservationsPage;
    private UpdateReservationModal updateReservationModal;

    public ReservationSteps() {
        this.loginPage = new LoginPage();
        this.reservationsPage = new ReservationsPage();
        this.updateReservationModal = new UpdateReservationModal();
    }

    private void ensureUserIsAtReservations() {
        loginPage.loginAs(RESERVATION_EMAIL, RESERVATION_PASSWORD);
        reservationsPage.navigateToMyReservations();
    }

    @Given("the user is on the reservations page")
    public void theUserIsOnTheReservationsPage() {
        ensureUserIsAtReservations();
    }

    @When("the user clicks on update for a reservation")
    public void theUserClicksOnUpdateForAReservation() {
        reservationsPage.clickFirstEditButton();
    }

    @When("the user updates the reservation with valid start time {string} and end time {string}")
    public void theUserUpdatesTheReservationWithValidStartTimeAndEndTime(String startTime, String endTime) {
        updateReservationModal.setStartTime(startTime);
        updateReservationModal.setEndTime(endTime);
        updateReservationModal.clickSave();
    }

    @Then("the user should see the reservation updated successfully")
    public void theUserShouldSeeTheReservationUpdatedSuccessfully() {
        Assert.assertNotNull("Success message should be displayed", reservationsPage.getSuccessMessage());
    }

    @When("the user attempts to update a reservation with an existing time slot")
    public void theUserAttemptsToUpdateAReservationWithAnExistingTimeSlot() {
        updateReservationModal.setStartTime("09:00");
        updateReservationModal.setEndTime("10:00");
        updateReservationModal.clickSave();
    }

    @Then("the user should see an error message indicating a time conflict")
    public void theUserShouldSeeAnErrorMessageIndicatingATimeConflict() {
        Assert.assertNotNull("Conflict error should be displayed", reservationsPage.getErrorMessage());
    }

    // New step definitions for the updated feature scenarios

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        loginPage.openLoginPage();
    }

    @When("the user enters valid credentials")
    public void theUserEntersValidCredentials() {
        loginPage.loginAs(RESERVATION_EMAIL, RESERVATION_PASSWORD);
    }

    @Then("the user should see the reservations dashboard")
    public void theUserShouldSeeTheReservationsDashboard() {
        Assert.assertTrue("User should be on reservations page", 
            reservationsPage.isCurrentlyVisible());
    }

    @Given("the user is logged in and has at least one upcoming reservation")
    public void theUserIsLoggedInAndHasAtLeastOneUpcomingReservation() {
        ensureUserIsAtReservations();
        Assert.assertTrue("At least one reservation should be visible", 
            reservationsPage.hasAtLeastOneReservation());
    }

    @When("the user tries to update the reservation with a start time greater than or equal to end time")
    public void theUserTriesToUpdateTheReservationWithStartTimeGreaterThanOrEqualToEndTime() {
        reservationsPage.clickFirstEditButton();
        updateReservationModal.setStartTime("14:00");
        updateReservationModal.setEndTime("13:00");
        updateReservationModal.clickSave();
    }

    @Then("the system should show an error message with {string}")
    public void theSystemShouldShowAnErrorMessageWith(String expectedMessage) {
        String actualMessage = updateReservationModal.getErrorMessage();
        Assert.assertTrue("Error message should contain: " + expectedMessage, 
            actualMessage.contains(expectedMessage));
    }

    @Then("the reservation should be saved and the modal should close")
    public void theReservationShouldBeSavedAndTheModalShouldClose() {
        updateReservationModal.waitForModalToClose();
        Assert.assertFalse("Modal should be closed", updateReservationModal.isModalVisible());
    }
}
