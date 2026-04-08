package com.reservassofka.pom.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class LoginPage extends PageObject {

    public void openLoginPage() {
        openUrl("http://localhost:5173");
    }

    public void loginAs(String email, String password) {
        openUrl("http://localhost:5173");
        waitABit(1000); // Wait for page to load
        $(By.id("email")).waitUntilVisible().clear();
        $(By.id("email")).type(email);
        $(By.id("password")).clear();
        $(By.id("password")).type(password);
        $(By.cssSelector("button[type='submit']")).click();
        
        // Wait for authentication to complete (redirect or error message)
        waitABit(3000);
        
        String currentUrl = getDriver().getCurrentUrl();
        System.out.println("DEBUG after login: URL = " + currentUrl);
        
        // If still on login page, check for error message
        if (currentUrl.contains("/login") || currentUrl.equals("http://localhost:5173/")) {
            boolean hasError = $(".error-message").isCurrentlyVisible();
            if (hasError) {
                String errorMsg = $(".error-message").getText();
                throw new RuntimeException("Login failed: " + errorMsg);
            }
            System.out.println("DEBUG: Still on login page but no error visible");
        }
    }
}
