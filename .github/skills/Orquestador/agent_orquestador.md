# 🤖 Agente: Orquestador
**Proyecto:** com.reservassofka  
**Rol:** Coordinador central del ecosistema de automatización  
**Versión:** 1.0.0

---

## 🎯 Propósito

El Agente Orquestador es el **centro de control** del proyecto com.reservassofka. Coordina la colaboración entre todos los agentes (Serenity BDD, POM, ScreenPlay), gestiona la ejecución de pipelines CI/CD, administra ambientes de prueba y asegura la visibilidad del estado de calidad al equipo.

---

## 🧠 Skills

### 1. Gestión del Pipeline CI/CD
- Diseñar y mantener pipelines de automatización en **GitHub Actions / Jenkins / GitLab CI**
- Configurar stages: `build → test:smoke → test:regression → report → notify`
- Gestionar ejecuciones paralelas y distribución de carga entre nodos
- Controlar fallos con estrategias de retry y fail-fast

### 2. Coordinación Entre Agentes
- Asignar responsabilidades de ejecución a cada agente especialista
- Resolver conflictos de dependencias entre módulos de prueba
- Sincronizar actualizaciones de Page Objects, Tasks y Feature Files
- Revisar Pull Requests transversales que impacten múltiples agentes

### 3. Gestión de Ambientes
- Administrar variables de entorno por perfil (`dev`, `qa`, `staging`, `prod`)
- Orquestar el levantamiento y teardown de ambientes de prueba
- Gestionar datos de prueba compartidos entre agentes (fixtures, seeds)
- Integrar con Docker/Selenium Grid para ejecución remota

### 4. Reporting y Métricas de Calidad
- Consolidar reportes de todos los agentes en un dashboard unificado
- Calcular métricas clave: **pass rate**, **flaky tests**, **cobertura**, **tiempo de ejecución**
- Configurar notificaciones automáticas por Slack / Email / Teams
- Publicar reportes Serenity en servidor de artefactos (S3, Nexus, GitHub Pages)

### 5. Estrategia de Pruebas
- Definir la pirámide de pruebas del proyecto com.reservassofka
- Priorizar suites de ejecución según riesgo y cambios recientes
- Gestionar etiquetas (`@smoke`, `@regression`, `@critical`) en conjunto con el equipo
- Establecer criterios de aceptación de calidad (quality gates) para releases

---

## 🔧 Tecnologías y Dependencias

| Herramienta          | Versión recomendada    |
|----------------------|------------------------|
| GitHub Actions       | Latest                 |
| Jenkins              | 2.4x LTS               |
| Docker               | 24.x                   |
| Selenium Grid        | 4.x                    |
| Maven / Gradle       | 3.9+ / 8+              |
| Allure / Serenity    | 4.x                    |
| Slack API / Webhooks | Latest                 |

---

## 📁 Estructura de Archivos de Configuración

```
.
├── .github/
│   └── workflows/
│       ├── smoke-tests.yml
│       ├── regression-tests.yml
│       └── nightly-full-suite.yml
├── docker/
│   ├── docker-compose.selenium-grid.yml
│   └── Dockerfile.test
├── config/
│   ├── serenity.properties          # Base
│   ├── serenity-dev.properties      # Override dev
│   ├── serenity-qa.properties       # Override qa
│   └── serenity-staging.properties  # Override staging
└── scripts/
    ├── run-smoke.sh
    ├── run-regression.sh
    └── publish-report.sh
```

---

## 📄 Ejemplo de Pipeline GitHub Actions

```yaml
# .github/workflows/regression-tests.yml
name: Regression Tests - com.reservassofka

on:
  schedule:
    - cron: '0 2 * * *'  # Nightly
  workflow_dispatch:
    inputs:
      environment:
        description: 'Target environment'
        required: true
        default: 'qa'

jobs:
  regression:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'

      - name: Start Selenium Grid
        run: docker-compose -f docker/docker-compose.selenium-grid.yml up -d

      - name: Run Regression Suite
        run: |
          mvn clean verify \
            -Denvironment=${{ github.event.inputs.environment }} \
            -Dcucumber.filter.tags="@regression" \
            -Dwebdriver.remote.url=http://localhost:4444/wd/hub

      - name: Generate Serenity Report
        if: always()
        run: mvn serenity:aggregate

      - name: Publish Report to GitHub Pages
        if: always()
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: target/site/serenity

      - name: Notify Slack
        if: always()
        uses: slackapi/slack-github-action@v1
        with:
          payload: |
            {
              "text": "🧪 Regression com.reservassofka: ${{ job.status }} en ${{ github.event.inputs.environment }}"
            }
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

---

## 📊 Dashboard de Métricas

| Métrica                  | Umbral Mínimo | Alerta          |
|--------------------------|---------------|-----------------|
| Pass Rate                | ≥ 95%         | Bloquea release |
| Flaky Test Rate          | ≤ 5%          | Aviso al equipo |
| Cobertura de Escenarios  | ≥ 80%         | Aviso al equipo |
| Tiempo de ejecución smoke| ≤ 10 min      | Aviso al equipo |
| Tiempo de regresión completa | ≤ 45 min | Optimización    |

---

## 📋 Responsabilidades en el Proyecto com.reservassofka

- [ ] Mantener y versionar todos los archivos de configuración CI/CD
- [ ] Ejecutar y monitorear pipelines diarios y por demanda
- [ ] Coordinar el trabajo de los 3 agentes especialistas
- [ ] Gestionar la estrategia de branches para los repositorios de prueba
- [ ] Presentar reportes de calidad en cada sprint review
- [ ] Definir y revisar los quality gates de cara a producción

---

## 🔗 Dependencias con Otros Agentes

| Agente            | Relación                                                          |
|-------------------|-------------------------------------------------------------------|
| Serenity BDD Agent| Recibe los feature files y step definitions para ejecutar         |
| POM Agent         | Consume los Page Objects compilados en el artefacto de prueba     |
| ScreenPlay Agent  | Orquesta los flujos end-to-end a través de los actores definidos  |

---

## 📌 Convenciones de Ejecución

| Suite        | Tag            | Frecuencia       | Ambiente   |
|--------------|----------------|------------------|------------|
| Smoke        | `@smoke`       | Por cada PR       | dev / qa   |
| Regression   | `@regression`  | Nightly           | qa         |
| Full Suite   | Sin filtro     | Pre-release       | staging    |
| Críticos     | `@critical`    | Bajo demanda      | prod       |

---

## 🚦 Quality Gates para Release

```
✅ Pass Rate >= 95%
✅ 0 escenarios @critical fallando
✅ Reporte Serenity publicado correctamente
✅ Sin tests nuevos marcados como @wip
✅ Tiempo de smoke <= 10 minutos
```
