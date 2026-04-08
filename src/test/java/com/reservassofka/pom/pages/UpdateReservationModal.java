package com.reservassofka.pom.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class UpdateReservationModal extends PageObject {

    private By MODAL_CONTAINER = By.cssSelector(".handover-modal.update-modal");
    private By SAVE_BUTTON = By.cssSelector(".btn-handover-confirm");

    public void setStartTime(String time) {
        setReactInputValue(By.cssSelector("input[type='time']:first-of-type"), time);
    }

    public void setEndTime(String time) {
        setReactInputValue(By.xpath("(//input[@type='time'])[2]"), time);
    }

    public void clickSave() {
        $(SAVE_BUTTON).waitUntilClickable().click();
    }

    public String getErrorMessage() {
        // Error message is displayed inline in the modal with specific styling
        By errorDiv = By.xpath("//div[contains(@style,'fff5f5') and contains(text(),'hora')]");
        return $(errorDiv).waitUntilVisible().getText();
    }

    public void waitForModalToClose() {
        waitABit(1000); // Wait for animation
        $(MODAL_CONTAINER).waitUntilNotVisible();
    }

    public boolean isModalVisible() {
        return $(MODAL_CONTAINER).isCurrentlyVisible();
    }

    private void setReactInputValue(By selector, String value) {
        $(selector).waitUntilVisible().click();
        evaluateJavascript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
            "window.HTMLInputElement.prototype, 'value').set;" +
            "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            $(selector), value
        );
    }
}
