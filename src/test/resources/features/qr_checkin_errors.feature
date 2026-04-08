Feature: Validación de QR - Manejo de Errores
  Como sistema de seguridad
  Quiero validar rigurosamente el código QR escaneado
  Para prevenir fraudes y asegurar que el check-in corresponda al espacio correcto

  Background:
    Given el usuario "admin@sofka.com.co" está autenticado
    And tiene una reserva activa con ID "RES-20260407-001" para el espacio "Sala Zeus"

  @negative @automation
  Scenario: QR con firma JWT inválida (token adulterado)
    Given el usuario navega hacia "Mis Reservas"
    When el usuario hace clic en el botón "Hacer Check-in"
    And se escanea un QR con firma JWT inválida
    Then se muestra el mensaje de error "Código QR inválido o corrupto"
    And el sistema registra un intento de check-in fallido en los logs de auditoría

  @negative
  Scenario: QR de un espacio diferente (Space ID mismatch)
    Given el usuario navega hacia "Mis Reservas"
    When el usuario hace clic en el botón "Hacer Check-in"
    And se escanea un QR con Space ID "Sala Ares" que no coincide con la reserva
    Then se muestra el mensaje de error "El QR no corresponde a Sala Zeus"
    And se muestra una sugerencia para "verificar la ubicación actual"

    @negative
  Scenario: Check-in para el espacio incorrecto
    Given el usuario intenta escanear el QR
    When la hora actual "ahora + 10 minutos" está fuera del periodo de gracia
    Then se muestra el mensaje de error "El tiempo para hacer check-in ha expirado"
    And se muestra el contacto de soporte técnico
