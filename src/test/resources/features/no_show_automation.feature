Feature: Marcado Automático NO_SHOW por Inasistencia
  Como administrador de espacios
  Quiero que el sistema libere automáticamente reservas sin check-in
  Para maximizar la disponibilidad de salas y equipos

  Background:
    Given el job "ReservationMonitorJob" está configurado para ejecutarse cada 1 minuto
    And el grace period está configurado en 5 minutos

  @critical @automation
  Scenario: Reservas sin check-in son marcadas como NO_SHOW tras 5 minutos
    Given existen las siguientes reservas en estado "PENDING":
      | id               | inicio                    | espacio     |
      | RES-DYN-101      | ahora - 11 minutos        | Oficina 201 |
      | RES-DYN-102      | ahora - 6 minutos         | Sala Zeus   |
    When se ejecuta el job "ReservationMonitorJob" a las "ahora"
    Then el estado de las siguientes reservas cambia a "NO_SHOW":
      | id               |
      | RES-DYN-101      |
      | RES-DYN-102      |
    And se envían notificaciones de penalización a los usuarios

  Scenario: El job es idempotente (no duplica actualizaciones)
    Given existen las siguientes reservas en estado "NO_SHOW":
      | id               | inicio                    |
      | RES-DYN-101      | ahora - 15 minutos        |
    When se ejecuta el job "ReservationMonitorJob" a las "ahora"
    Then el estado de la reserva "RES-DYN-101" permanece en "NO_SHOW"

  Scenario: Procesamiento eficiente de múltiples reservas expiradas
    Given existen 50 reservas en estado "PENDING" iniciadas hace más de 6 minutos
    When se ejecuta el job "ReservationMonitorJob"
    Then todas las 50 reservas cambian de estado a "NO_SHOW"

  Scenario: Reserva recibe check-in justo cuando el job se está ejecutando (race condition)
    Given la reserva "RES-DYN-555" está en estado "PENDING"
    And faltan 5 segundos para que expire el grace period
    When el usuario realiza check-in exitoso
    And simultáneamente se ejecuta el job de monitoreo
    Then el estado final de la reserva debe ser "CHECKED_IN"
    And no debe marcarse como "NO_SHOW"
