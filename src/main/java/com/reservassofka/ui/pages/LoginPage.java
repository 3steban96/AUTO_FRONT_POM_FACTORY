package com.reservassofka.ui.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LoginPage - Page Object Model
 *
 * NOTE: The login form uses React controlled inputs (value + onChange).
 * Standard Selenium sendKeys only triggers keyboard events; React's state
 * won't update unless the native 'input' event is dispatched.
 * We use JavascriptExecutor to set the value and fire the event properly.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(id = "email")
    private WebElement emailField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js     = (JavascriptExecutor) driver;
    }

    /**
     * Sets a value into a React controlled input by dispatching the native
     * 'input' event so that React's onChange handler fires.
     */
    private void setReactInputValue(WebElement element, String value) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
        // Use the native input value setter so React sees the change
        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
                "window.HTMLInputElement.prototype, 'value').set;" +
            "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            element, value
        );
    }

    /**
     * Enters the email credential using a React-compatible approach.
     */
    public void enterEmail(String email) {
        setReactInputValue(emailField, email);
    }

    /**
     * Enters the password credential using a React-compatible approach.
     */
    public void enterPassword(String password) {
        setReactInputValue(passwordField, password);
    }

    /**
     * Clicks the login submit button.
     */
    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }

    /**
     * Full login flow: email + password + submit.
     */
    public void loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }
}

