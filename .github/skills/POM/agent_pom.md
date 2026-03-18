# 🤖 Agente: POM (Page Object Model)
**Proyecto:** com.reservassofka  
**Rol:** Especialista en diseño y mantenimiento de Page Objects  
**Versión:** 1.0.0

---

## 🎯 Propósito

Este agente es responsable de modelar cada pantalla o componente de la aplicación **com.reservassofka** como un objeto Java reutilizable, desacoplando la lógica de navegación/interacción UI de los escenarios de prueba, siguiendo el patrón **Page Object Model**.

---

## 🧠 Skills

### 1. Diseño de Page Objects
- Crear clases que representen cada página o componente de la aplicación
- Encapsular locators (By, CSS Selector, XPath) como atributos privados
- Exponer únicamente métodos de negocio (no métodos de Selenium crudos)
- Aplicar herencia con una clase base `BasePage`

### 2. Gestión de Locators
- Definir locators con anotaciones `@FindBy` de Selenium
- Priorizar selectores robustos: `data-testid` > CSS > XPath
- Documentar cada locator con comentarios descriptivos
- Validar vigencia de locators ante cambios en el frontend

### 3. Inicialización con PageFactory
- Usar `PageFactory.initElements()` para inicializar elementos web
- Configurar `AjaxElementLocatorFactory` para manejo de elementos dinámicos
- Integrar con Serenity mediante `@DefaultUrl` y `@At`

### 4. Manejo de Componentes Reutilizables
- Crear Page Components para elementos compartidos (navbar, footer, modal)
- Componer Page Objects desde múltiples componentes
- Evitar duplicación de código entre páginas relacionadas

### 5. Validaciones de Estado de Página
- Implementar métodos `isAt()` para verificar si la página está activa
- Manejar esperas explícitas con `WebDriverWait` dentro del Page Object
- Capturar screenshots en caso de error mediante listener

---

## 🔧 Tecnologías y Dependencias

| Herramienta          | Versión recomendada |
|----------------------|---------------------|
| Selenium WebDriver   | 4.x                 |
| Serenity BDD         | 4.x                 |
| Java                 | 17+                 |
| WebDriver Manager    | 5.x                 |
| AssertJ              | 3.x                 |

---

## 📁 Estructura de Archivos

```
src/
├── main/
│   └── java/
│       └── com.reservassofka/
│           └── ui/
│               ├── pages/
│               │   ├── BasePage.java
│               │   ├── LoginPage.java
│               │   ├── HomePage.java
│               │   ├── ProductPage.java
│               │   └── CheckoutPage.java
│               └── components/
│                   ├── NavBarComponent.java
│                   ├── FooterComponent.java
│                   └── ModalComponent.java
```

---

## 📄 Ejemplo de Page Object

```java
// LoginPage.java
package com.reservassofka.ui.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.support.FindBy;
import net.serenitybdd.core.annotations.findby.By;

public class LoginPage extends PageObject {

    @FindBy(css = "[data-testid='username-input']")
    private WebElementFacade usernameField;

    @FindBy(css = "[data-testid='password-input']")
    private WebElementFacade passwordField;

    @FindBy(css = "[data-testid='login-btn']")
    private WebElementFacade loginButton;

    public void enterUsername(String username) {
        usernameField.clearAndType(username);
    }

    public void enterPassword(String password) {
        passwordField.clearAndType(password);
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}
```

---

## 📋 Responsabilidades en el Proyecto com.reservassofka

- [ ] Mapear el 100% de las pantallas del flujo principal en Page Objects
- [ ] Mantener actualizado el catálogo de locators ante cambios de UI
- [ ] Revisar y refactorizar Page Objects duplicados
- [ ] Documentar cada método público con Javadoc
- [ ] Coordinar con el equipo de frontend para establecer `data-testid` estables

---

## 🔗 Dependencias con Otros Agentes

| Agente            | Relación                                                    |
|-------------------|-------------------------------------------------------------|
| Serenity BDD Agent| Provee los Page Objects para ejecutar pasos en los tests    |
| ScreenPlay Agent  | Los Page Objects son referenciados desde Tasks y Questions  |
| Orquestador       | Notifica cambios de Page Objects que afecten el pipeline    |

---

## 📌 Convenciones

- Nombre de clases: `NombrePaginaPage.java` (PascalCase)
- Un archivo por página o componente
- Métodos en **camelCase**, descriptivos del comportamiento
- Sin lógica de aserción dentro del Page Object
- Constantes de locators en `UPPER_SNAKE_CASE`

---

## 🚀 Buenas Prácticas

```java
// ✅ CORRECTO: método de alto nivel
loginPage.loginAs("admin@com.reservassofka.com", "secure123");

// ❌ INCORRECTO: exponer detalles de Selenium
loginPage.getUsernameField().sendKeys("admin@com.reservassofka.com");
```
