package com.reservassofka.ui.components;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * UpdateReservationModal - Page Object Component
 * Models the "Actualizar Reserva" modal dialog.
 *
 * NOTE: Time inputs are React controlled (value + onChange).
 * Uses JavascriptExecutor to set values and dispatch native events.
 */
public class UpdateReservationModal {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(css = ".update-modal")
    private WebElement modalContainer;

    @FindBy(css = "input[type='time']")
    private List<WebElement> timeInputs;

    @FindBy(css = ".btn-handover-confirm")
    private WebElement submitButton;

    @FindBy(xpath = "//div[contains(text(),'La hora de inicio debe ser menor que la hora de fin')]")
    private WebElement errorMessageBlock;

    public UpdateReservationModal(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js     = (JavascriptExecutor) driver;
    }

    /**
     * Sets a value into a React controlled input via JavascriptExecutor
     * so React's onChange handler fires properly.
     */
    private void setReactInputValue(WebElement element, String value) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
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
     * Waits until the modal is visible.
     */
    public void waitForModal() {
        wait.until(ExpectedConditions.visibilityOf(modalContainer));
    }

    /**
     * Sets the start time field (first time input in the modal).
     */
    public void setStartTime(String time) {
        if (!timeInputs.isEmpty()) {
            setReactInputValue(timeInputs.get(0), time);
        }
    }

    /**
     * Sets the end time field (second time input in the modal).
     */
    public void setEndTime(String time) {
        if (timeInputs.size() > 1) {
            setReactInputValue(timeInputs.get(1), time);
        }
    }

    /**
     * Clicks the "Guardar Cambios" button to submit the form.
     */
    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();
    }

    /**
     * Returns the error message text when validation fails.
     */
    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessageBlock));
        return errorMessageBlock.getText();
    }

    /**
     * Returns true if the modal is no longer visible.
     * Used to verify a successful save operation closed the modal.
     */
    public boolean isModalClosed() {
        try {
            wait.until(ExpectedConditions.invisibilityOf(modalContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

