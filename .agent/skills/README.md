# 🧠 Agentes de Automatización — Antigravity

Bienvenido al directorio de skills del proyecto **Antigravity**. Esta carpeta contiene la definición de los agentes especializados que conforman el ecosistema de automatización de calidad del proyecto.

---

## 📂 Estructura

```
.agent/
└── skills/
    ├── README.md                  ← estás aquí
    ├── Orquestador/
    │   └── agent_orquestador.md
    ├── POM/
    │   └── agent_pom.md
    ├── ScreenPlay/
    │   └── agent_screenplay.md
    └── SerenityBdd/
        └── agent_serenity_bdd.md
```

---

## 🤖 Agentes

### 🟠 Orquestador
**Archivo:** `Orquestador/agent_orquestador.md`

Centro de control del ecosistema. Coordina a los demás agentes, gestiona los pipelines CI/CD en GitHub Actions, administra los ambientes de prueba (`dev`, `qa`, `staging`) y publica reportes de calidad al equipo.

**Responsabilidades clave:**
- Diseño y mantenimiento de pipelines CI/CD
- Definición de quality gates por release
- Consolidación de métricas: pass rate, flaky tests, cobertura
- Orquestación de Selenium Grid y Docker
- Notificaciones automáticas vía Slack / Email

---

### 🟢 POM — Page Object Model
**Archivo:** `POM/agent_pom.md`

Modela cada pantalla y componente de la aplicación como objetos Java reutilizables. Desacopla la lógica de interacción con la UI de los escenarios de prueba.

**Responsabilidades clave:**
- Creación y mantenimiento de Page Objects por módulo
- Gestión de locators robustos con `@FindBy` y `data-testid`
- Inicialización con `PageFactory` y `AjaxElementLocatorFactory`
- Componentes reutilizables: NavBar, Footer, Modals

---

### 🔵 ScreenPlay
**Archivo:** `ScreenPlay/agent_screenplay.md`

Implementa el patrón Screenplay modelando el sistema desde la perspectiva del actor (usuario). Promueve alta cohesión, bajo acoplamiento y máxima reutilización de código de prueba.

**Responsabilidades clave:**
- Diseño de Actores con habilidades (`BrowseTheWeb`, `CallAnApi`)
- Implementación de Tasks de negocio reutilizables
- Definición de Questions para validaciones declarativas
- Gestión de Targets como abstracción de locators

---

### 🔴 Serenity BDD
**Archivo:** `SerenityBdd/agent_serenity_bdd.md`

Diseña e implementa las pruebas en lenguaje Gherkin, conecta los escenarios con los step definitions y genera los reportes narrativos de Serenity.

**Responsabilidades clave:**
- Escritura de feature files con sintaxis Given / When / Then
- Implementación de Step Definitions con anotaciones `@Step`
- Configuración del runner `CucumberWithSerenity`
- Generación y publicación de reportes HTML de Serenity

---

## 🔗 Relación entre agentes

```
┌─────────────────────────────────────────────────────┐
│                    ORQUESTADOR                       │
│         Pipeline · Ambientes · Métricas              │
└──────┬─────────────────────────┬────────────────────┘
       │                         │
       ▼                         ▼
┌─────────────┐         ┌─────────────────┐
│ SERENITY BDD│         │   SCREENPLAY    │
│ Features    │ ──────► │ Tasks · Questions│
│ Steps       │         │ Interactions    │
└──────┬──────┘         └────────┬────────┘
       │                         │
       └──────────┬──────────────┘
                  ▼
          ┌──────────────┐
          │     POM      │
          │ Page Objects │
          │ Locators     │
          └──────────────┘
```

**Flujo de dependencias:**
1. El **Orquestador** dispara la ejecución en el pipeline y coordina ambientes
2. **Serenity BDD** recibe los feature files y ejecuta los step definitions
3. Los steps invocan **Tasks** y **Questions** del agente ScreenPlay
4. Tanto ScreenPlay como Serenity consumen los **Page Objects** del agente POM

---

## 🏷️ Convenciones de etiquetas (tags)

| Tag           | Descripción                          | Frecuencia de ejecución |
|---------------|--------------------------------------|--------------------------|
| `@smoke`      | Pruebas críticas de humo             | Por cada PR              |
| `@regression` | Suite completa de regresión          | Nightly                  |
| `@critical`   | Flujos bloqueantes para producción   | Pre-release              |
| `@wip`        | En desarrollo, no se ejecuta en CI   | Manual                   |

---

## 🚦 Quality Gates

Para que una rama pueda ser promovida a producción, debe cumplir:

```
✅ Pass Rate >= 95%
✅ 0 escenarios @critical fallando
✅ Flaky Test Rate <= 5%
✅ Reporte Serenity publicado correctamente
✅ Sin tests marcados como @wip en rama main
✅ Tiempo de ejecución smoke <= 10 minutos
```

---

## 🛠️ Stack tecnológico

| Tecnología          | Versión    | Agente principal  |
|---------------------|------------|-------------------|
| Java                | 17+        | Todos             |
| Serenity BDD        | 4.x        | Serenity BDD      |
| Cucumber            | 7.x        | Serenity BDD      |
| Selenium WebDriver  | 4.x        | POM               |
| Serenity Screenplay | 4.x        | ScreenPlay        |
| Maven               | 3.9+       | Orquestador       |
| GitHub Actions      | Latest     | Orquestador       |
| Docker / Sel. Grid  | 24.x / 4.x | Orquestador       |

---

## 📬 Contacto del equipo

> Actualiza esta sección con los responsables de cada agente en tu equipo.

| Agente       | Responsable | Contacto |
|--------------|-------------|----------|
| Orquestador  | —           | —        |
| POM          | —           | —        |
| ScreenPlay   | —           | —        |
| Serenity BDD | —           | —        |

---

*Última actualización: v1.0.0 — Proyecto Antigravity*
