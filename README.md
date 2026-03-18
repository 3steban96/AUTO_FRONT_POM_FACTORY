# Reservas Sofka - Automatización (POM)

Este módulo contiene las pruebas automatizadas para el proyecto **[Reservas Sofka](https://github.com/Sherman97/reservasSofka)**, y su implementación corresponde a dicho repositorio. La arquitectura de pruebas se ha diseñado utilizando **Serenity BDD**, **Cucumber** y el patrón **Page Object Model (POM)**.

## Tecnologías Utilizadas
- **Java**
- **Gradle**
- **Serenity BDD** (v4.0.12)
- **Cucumber**
- **JUnit**
- **AssertJ**

## Estructura del Proyecto
- `src/main/java/com.reservassofka`: Contiene los Page Objects, los componentes de Interfaz de Usuario (UI) y las tareas para interactuar con la aplicación.
- `src/test/java/com.reservassofka`: Contiene las definiciones de los pasos (Step Definitions) de Cucumber y los ejecutores de prueba (Test Runners).
- `src/test/resources/features`: Contiene los archivos con sintaxis Gherkin (`.feature`) donde se definen los escenarios de prueba.
- `serenity.properties`: Archivo de configuración para Serenity BDD (ej. ajustes del WebDriver, URLs base, tiempos de espera, credenciales).

## Flujos de Prueba

El proyecto de automatización cubre diferentes escenarios de uso de la aplicación, divididos en flujos **positivos** (caminos felices) y flujos **negativos** (validación de reglas de negocio y manejo de errores). A continuación se detallan los escenarios cubiertos:

### 1. Autenticación (Login)
- **Flujo Positivo:** `Successful login with valid credentials`
  - *Descripción:* Valida que un empleado pueda iniciar sesión correctamente utilizando credenciales válidas.
  - *Resultado Esperado:* El usuario ingresa correctamente y es redirigido a la vista principal de sus reservas (`reservations dashboard`).

### 2. Gestión de Reservas
- **Flujo Positivo:** `Successfully update a reservation with valid times`
  - *Descripción:* Valida que el usuario pueda modificar una de sus próximas reservas ingresando un periodo de tiempo válido (por ejemplo, modificando la hora de inicio a las "14:00" y la de fin a las "15:00").
  - *Resultado Esperado:* La reserva se actualiza o guarda exitosamente y la ventana modal se cierra automáticamente.

- **Flujo Negativo:** `Error when updating a reservation with invalid start time`
  - *Descripción:* Valida que el sistema verifique la coherencia de los horarios, probando el escenario en el que un usuario intenta guardar una reserva donde la hora de inicio es igual o posterior a la hora de fin.
  - *Resultado Esperado:* El sistema restringe la acción de guardado y arroja el mensaje de validación: *"La hora de inicio debe ser menor que la hora de fin"*.

## Ejecución

### Ejecutar todas las pruebas
Para ejecutar todas las pruebas y generar simultáneamente el reporte de Serenity, ejecuta el siguiente comando en la raíz del directorio `POM`:

```bash
gradle clean test aggregate
```

Si estás utilizando el Wrapper de Gradle configurado en el proyecto, puedes ejecutar localmente según tu sistema operativo:
- **Windows**: `gradlew.bat clean test aggregate`
- **Mac/Linux**: `./gradlew clean test aggregate`

## Reportes
Una vez que finaliza la ejecución de las pruebas, Serenity BDD procesa los resultados y genera un reporte HTML enriquecido con todo el detalle de las historias y los pasos ejecutados. 

Puedes ver este reporte abriendo el siguiente archivo en tu navegador web favorito:
`target/site/serenity/index.html`

