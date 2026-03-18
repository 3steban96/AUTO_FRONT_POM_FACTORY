package antigravity.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * MyReservationsPage - Page Object Model
 * Models the reservations dashboard page.
 */
public class MyReservationsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = ".reservation-list")
    private WebElement reservationListContainer;

    @FindBy(css = ".btn-edit-res")
    private List<WebElement> editButtons;

    public MyReservationsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Returns true when the reservation list container is visible.
     */
    public boolean isDashboardVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(reservationListContainer));
            return reservationListContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks the first available edit button on an upcoming reservation.
     */
    public void clickFirstEditButton() {
        wait.until(ExpectedConditions.visibilityOfAllElements(editButtons));
        if (!editButtons.isEmpty()) {
            editButtons.get(0).click();
        } else {
            throw new RuntimeException("No upcoming reservations with an edit button were found.");
        }
    }
}
