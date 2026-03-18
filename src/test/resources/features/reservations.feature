Feature: Authentication and Reservation Management
  As a Sofka employee
  I want to log in and manage my reservations
  To organize my time and resources

  @smoke @login
  Scenario: Successful login with valid credentials
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should see the reservations dashboard

  @regression @reservation
  Scenario: Error when updating a reservation with invalid start time
    Given the user is logged in and has at least one upcoming reservation
    When the user tries to update the reservation with a start time greater than or equal to end time
    Then the system should show an error message with "La hora de inicio debe ser menor que la hora de fin"

  @smoke @reservation
  Scenario: Successfully update a reservation with valid times
    Given the user is logged in and has at least one upcoming reservation
    When the user updates the reservation with valid start time "12:00" and end time "13:00"
    Then the reservation should be saved and the modal should close
