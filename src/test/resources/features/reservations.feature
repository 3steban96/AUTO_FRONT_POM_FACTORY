Feature: Reservations Management
  As a user
  I want to be able to view and manage my workspace reservations

  @smoke
  Scenario: Successful login
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should see the reservations dashboard

  @negative
  Scenario: Invalid time range for reservation update
    Given the user is logged in and has at least one upcoming reservation
    When the user tries to update the reservation with a start time greater than or equal to end time
    Then the system should show an error message with "La hora de inicio debe ser anterior a la hora de fin"

  @critical
  Scenario: Successful reservation update
    Given the user is logged in and has at least one upcoming reservation
    When the user updates the reservation with valid start time "09:00" and end time "10:00"
    Then the reservation should be saved and the modal should close
