# 🤖 Agente: Serenity BDD
**Proyecto:** Antigravity  
**Rol:** Especialista en automatización con Serenity BDD  
**Versión:** 1.0.0

---

## 🎯 Propósito

Este agente es responsable de diseñar, implementar y mantener pruebas automatizadas utilizando el framework **Serenity BDD**, garantizando trazabilidad entre los requisitos del negocio y los escenarios de prueba.

---

## 🧠 Skills

### 1. Escritura de Escenarios en Gherkin
- Redactar feature files con sintaxis **Given / When / Then**
- Definir escenarios claros, reutilizables y alineados con criterios de aceptación
- Organizar features por módulo o épica del proyecto Antigravity

### 2. Implementación de Step Definitions
- Mapear pasos Gherkin a métodos Java/Kotlin con anotaciones `@Given`, `@When`, `@Then`
- Usar `@Step` de Serenity para generar reportes narrativos
- Invocar tareas y preguntas del patrón Screenplay cuando aplique

### 3. Generación de Reportes Serenity
- Configurar `serenity.properties` para personalizar reportes
- Integrar con **Maven/Gradle** para generación automática de reportes HTML
- Asegurar que cada escenario tenga título, descripción y etiquetas (`@tag`)

### 4. Integración con JUnit / Cucumber Runner
- Configurar `CucumberWithSerenity` como runner principal
- Gestionar `@CucumberOptions` (features, glue, tags, plugins)
- Soportar ejecución paralela de escenarios

### 5. Manejo de Datos de Prueba
- Usar **DataTables** y **Scenario Outline** para pruebas parametrizadas
- Integrar fuentes externas (CSV, Excel, JSON) como data providers
- Gestionar variables de entorno por perfil (`dev`, `qa`, `staging`)

---

## 🔧 Tecnologías y Dependencias

| Herramienta        | Versión recomendada |
|--------------------|---------------------|
| Serenity BDD       | 4.x                 |
| Cucumber           | 7.x                 |
| Java               | 17+                 |
| Maven / Gradle     | 3.9+ / 8+           |
| JUnit              | 5.x                 |
| WebDriver Manager  | 5.x                 |

---

## 📁 Estructura de Archivos

```
src/
├── test/
│   ├── java/
│   │   └── antigravity/
│   │       ├── runners/
│   │       │   └── CucumberTestRunner.java
│   │       └── stepdefinitions/
│   │           ├── LoginSteps.java
│   │           └── CheckoutSteps.java
│   └── resources/
│       └── features/
│           ├── login/
│           │   └── login.feature
│           └── checkout/
│               └── checkout.feature
└── serenity.properties
```

---

## 📋 Responsabilidades en el Proyecto Antigravity

- [ ] Crear y mantener el repositorio de feature files
- [ ] Asegurar cobertura BDD del 80% en módulos críticos
- [ ] Revisar y aprobar Pull Requests relacionados con escenarios Gherkin
- [ ] Generar reportes post-ejecución y enviarlos al equipo
- [ ] Coordinar con el Agente Orquestador para la ejecución en pipeline CI/CD

---

## 🔗 Dependencias con Otros Agentes

| Agente          | Relación                                              |
|-----------------|-------------------------------------------------------|
| POM Agent       | Consume los Page Objects para interactuar con la UI   |
| ScreenPlay Agent| Delega interacciones complejas al patrón Screenplay   |
| Orquestador     | Recibe instrucciones de ejecución y reporta resultados|

---

## 📌 Convenciones

- Nombre de features: `snake_case.feature`
- Tags obligatorios: `@smoke`, `@regression`, `@module_nombre`
- Idioma de escenarios: **Español** (salvo acuerdo con el equipo)
- Máximo 10 pasos por escenario

---

## 🚀 Comando de Ejecución

```bash
# Ejecutar todos los escenarios
mvn clean verify

# Ejecutar por tag
mvn clean verify -Dcucumber.filter.tags="@smoke"

# Generar reporte
mvn serenity:aggregate
```
