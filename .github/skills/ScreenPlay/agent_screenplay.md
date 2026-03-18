# 🤖 Agente: ScreenPlay
**Proyecto:** com.reservassofka  
**Rol:** Especialista en el patrón de automatización Screenplay  
**Versión:** 1.0.0

---

## 🎯 Propósito

Este agente diseña e implementa la capa de automatización de **com.reservassofka** siguiendo el patrón **Screenplay**, que modela el comportamiento del sistema desde la perspectiva del actor (usuario), promoviendo alta cohesión, bajo acoplamiento y máxima reutilización.

---

## 🧠 Skills

### 1. Diseño de Actores (Actors)
- Crear actores que representan roles de usuario del sistema (ej. `Cliente`, `Administrador`)
- Asignar habilidades (Abilities) a los actores: `BrowseTheWeb`, `CallAnApi`, `ManageFiles`
- Configurar actores en el contexto de Serenity con `OnStage` y `Cast`

### 2. Implementación de Tasks
- Modelar acciones de alto nivel como tareas de negocio reutilizables
- Implementar la interfaz `Task` con el método `performAs(Actor actor)`
- Componer tareas complejas desde tareas atómicas más simples
- Anotar con `@Step` para visibilidad en reportes

### 3. Implementación de Interactions
- Crear interacciones de bajo nivel con la UI: `Click`, `Enter`, `Select`
- Usar las clases built-in de Serenity: `Click.on()`, `Enter.theValue().into()`
- Crear interacciones personalizadas para casos no cubiertos por el framework

### 4. Implementación de Questions
- Definir preguntas para extraer el estado actual de la aplicación
- Implementar la interfaz `Question<T>` con el método `answeredBy(Actor actor)`
- Combinar preguntas con `Ensure` para assertions declarativas

### 5. Targets y UI Locators
- Definir `Target` como abstracción de locators en Screenplay
- Organizar `Targets` en clases de tipo `*Page` o `*Component`
- Mantener nombres semánticos y orientados al negocio

---

## 🔧 Tecnologías y Dependencias

| Herramienta             | Versión recomendada |
|-------------------------|---------------------|
| Serenity Screenplay     | 4.x                 |
| Serenity REST Assured   | 4.x (para API tests)|
| Java                    | 17+                 |
| Cucumber                | 7.x                 |
| AssertJ / Serenity Ensure| 4.x               |

---

## 📁 Estructura de Archivos

```
src/
├── test/
│   └── java/
│       └── com.reservassofka/
│           └── screenplay/
│               ├── actors/
│               │   └── com.reservassofkaActors.java
│               ├── abilities/
│               │   └── Managecom.reservassofkaSession.java
│               ├── tasks/
│               │   ├── Login.java
│               │   ├── AddProductToCart.java
│               │   └── CompleteCheckout.java
│               ├── interactions/
│               │   └── SelectDropdownOption.java
│               ├── questions/
│               │   ├── CartTotal.java
│               │   └── ConfirmationMessage.java
│               └── ui/
│                   ├── LoginPageUI.java
│                   ├── HomePageUI.java
│                   └── CheckoutPageUI.java
```

---

## 📄 Ejemplos de Implementación

### Task: Login
```java
// Login.java
public class Login implements Task {

    private final String username;
    private final String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Login as(String username, String password) {
        return Tasks.instrumented(Login.class, username, password);
    }

    @Override
    @Step("{0} inicia sesión como #username")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Navigate.to("/login"),
            Enter.theValue(username).into(LoginPageUI.USERNAME),
            Enter.theValue(password).into(LoginPageUI.PASSWORD),
            Click.on(LoginPageUI.LOGIN_BUTTON)
        );
    }
}
```

### Target UI
```java
// LoginPageUI.java
public class LoginPageUI {
    public static final Target USERNAME =
        Target.the("campo usuario").locatedBy("[data-testid='username-input']");
    public static final Target PASSWORD =
        Target.the("campo contraseña").locatedBy("[data-testid='password-input']");
    public static final Target LOGIN_BUTTON =
        Target.the("botón ingresar").locatedBy("[data-testid='login-btn']");
}
```

### Question
```java
// ConfirmationMessage.java
public class ConfirmationMessage implements Question<String> {
    public static ConfirmationMessage displayed() {
        return new ConfirmationMessage();
    }

    @Override
    public String answeredBy(Actor actor) {
        return Text.of(CheckoutPageUI.CONFIRMATION_MSG).answeredBy(actor);
    }
}
```

### Step Definition
```java
// En el Step Definition
@When("{actor} completa el proceso de login")
public void loginStep(Actor actor) {
    actor.attemptsTo(Login.as("user@com.reservassofka.com", "pass123"));
}

@Then("{actor} ve el mensaje de bienvenida")
public void verifyWelcome(Actor actor) {
    actor.attemptsTo(
        Ensure.that(ConfirmationMessage.displayed()).isEqualTo("Bienvenido a com.reservassofka")
    );
}
```

---

## 📋 Responsabilidades en el Proyecto com.reservassofka

- [ ] Implementar Tasks para todos los flujos funcionales principales
- [ ] Crear el catálogo de Questions para validaciones reutilizables
- [ ] Definir los Targets de UI en clases organizadas por módulo
- [ ] Mantener la separación estricta entre Tasks, Interactions y Questions
- [ ] Capacitar al equipo en el patrón Screenplay

---

## 🔗 Dependencias con Otros Agentes

| Agente            | Relación                                                          |
|-------------------|-------------------------------------------------------------------|
| POM Agent         | Los Targets de Screenplay reemplazan o complementan Page Objects  |
| Serenity BDD Agent| Los Step Definitions invocan Tasks y Questions de Screenplay      |
| Orquestador       | Orquesta la ejecución de flujos end-to-end usando los actores     |

---

## 📌 Convenciones

- Nombres de Tasks: **infinitivos de negocio** (`Login`, `AddProduct`, `Checkout`)
- Nombres de Questions: **sustantivos** que describen lo que se consulta (`CartTotal`, `PageTitle`)
- Nombres de Targets: **descriptivos desde el punto de vista del usuario**
- Un archivo por Task, Question o UI class
- Sin lógica de aserción dentro de las Tasks

---

## 🎭 Principios del Patrón

```
Actor → tiene → Abilities
Actor → intenta → Tasks
Tasks → se componen de → Interactions
Actor → hace → Questions
Questions → retornan → Estado de la aplicación
```
