Feature: Check-in con QR para Reservas
  Como colaborador de Sofka
  Quiero escanear el código QR de la sala reservada
  Para confirmar mi asistencia y evitar que mi reserva sea cancelada

  # NOTA: El ID de reserva es descriptivo para legibilidad. 
  # El test creará una reserva real vía API y usará su ID numérico.
  # El botón de check-in solo se muestra 5 minutos antes del inicio de la reserva.

  Background:
    Given el usuario "admin@sofka.com.co" está autenticado
    And tiene una reserva activa con ID "RES-DYN-001" para el espacio "Sala Zeus" 
    And la reserva está en estado "PENDING"
    And la hora de inicio de la reserva es "ahora - 2 minutos"
    And la hora actual del sistema es "ahora"

  @smoke @critical
  Scenario: Check-in exitoso con QR válido
    Given el usuario navega hacia "Mis Reservas"
    And se encuentra en la tarjeta de su reserva
    When el usuario hace clic en el botón "Hacer Check-in"
    And se abre el modal de escaneo QR
    And se otorgan permisos de cámara
    And se escanea el QR válido del espacio "Sala Zeus"
    Then el estado de la reserva cambia a "CHECKED_IN"
    And se muestra el mensaje de éxito "Check-in realizado exitosamente"
    And se cierra el modal de escaneo
    And la tarjeta de reserva muestra el badge verde "Confirmada"
    And se recibe notificación push "Has confirmado tu asistencia a Sala Zeus"

  @negative
  Scenario: Check-in fuera del período de gracia
    Given la hora actual del sistema es "ahora + 10 minutos"
    When el usuario intenta hacer check-in escaneando el QR válido
    Then se muestra el mensaje de error "El tiempo para hacer check-in ha expirado"
    And el estado de la reserva permanece en "PENDING"
