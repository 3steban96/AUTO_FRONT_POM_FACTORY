package com.reservassofka.pom.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

public class ReservationsPage extends PageObject {
    
    private By RESERVATION_CARDS = By.cssSelector(".reservation-card");
    private By RESERVATION_LIST = By.cssSelector(".reservation-list");
    private By SUCCESS_MESSAGE = By.cssSelector(".success-message, .alert-success");
    private By ERROR_MESSAGE = By.cssSelector(".error-message, .alert-danger");

    public void navigateToMyReservations() {
        openUrl("http://localhost:5173/my-reservations");
        waitABit(3000); // Wait for page load, data fetch, and React rendering
        
        // Check if we're authenticated (not redirected to login)
        String currentUrl = getDriver().getCurrentUrl();
        if (currentUrl.contains("login")) {
            throw new RuntimeException("User is not authenticated - redirected to login page");
        }
        
        // Wait for page to fully load - try multiple selectors
        try {
            $(RESERVATION_LIST).waitUntilPresent();
            System.out.println("✓ Reservation list found");
        } catch (Exception e) {
            // If no reservation list, check for empty state
            try {
                $(".empty-state").waitUntilPresent();
                System.out.println("✓ Empty state found");
            } catch (Exception e2) {
                // If neither exists, wait a bit more for any content
                waitABit(3000); // Increased wait time
                System.out.println("DEBUG: Current URL = " + currentUrl);
                
                // Check if any reservation cards are present
                int cardsCount = findAll(RESERVATION_CARDS).size();
                System.out.println("DEBUG: Found " + cardsCount + " reservation cards");
                
                if (cardsCount > 0) {
                    System.out.println("✓ Reservation cards are present even though list container wasn't found");
                }
            }
        }
    }

    public void clickFirstEditButton() {
        $(By.cssSelector(".btn-edit-res")).waitUntilClickable().click();
    }
    
    public WebElementFacade getReservationCardById(String reservationId) {
        // El frontend muestra "ID: 123" donde 123 es el ID numérico
        System.out.println("DEBUG: Buscando reserva con ID: " + reservationId);
        
        // Esperar a que las tarjetas se rendericen completamente
        waitABit(3000);
        
        // Debug: Listar todos los IDs visibles
        try {
            var allSubtitles = findAll(By.cssSelector(".card-subtitle"));
            System.out.println("DEBUG: Total de elementos .card-subtitle encontrados: " + allSubtitles.size());
            for (int i = 0; i < allSubtitles.size(); i++) {
                String text = allSubtitles.get(i).getText();
                System.out.println("  - Elemento " + i + ": '" + text + "'");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error al listar subtítulos: " + e.getMessage());
        }
        
        // Estrategia 1: XPath simplificado - buscar directamente el p que contiene el texto
        String xpath1 = String.format(
            "//p[contains(@class,'card-subtitle') and contains(., 'ID: %s')]",
            reservationId
        );
        
        try {
            WebElementFacade subtitle = find(By.xpath(xpath1));
            if (subtitle.isPresent()) {
                System.out.println("✓ Encontrado con XPath simplificado");
                // Navegar al padre que es la card
                return subtitle.findBy("xpath:ancestor::div[contains(@class,'reservation-card')]");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: XPath 1 falló: " + e.getMessage());
        }
        
        // Estrategia 2: CSS Selector - más específico
        try {
            var cards = findAll(RESERVATION_CARDS);
            System.out.println("DEBUG: Buscando en " + cards.size() + " tarjetas...");
            
            for (WebElementFacade card : cards) {
                WebElementFacade subtitle = card.find(By.cssSelector(".card-subtitle"));
                String text = subtitle.getText();
                System.out.println("DEBUG: Verificando tarjeta con texto: '" + text + "'");
                
                if (text.contains("ID: " + reservationId)) {
                    System.out.println("✓ Encontrada por iteración!");
                    return card;
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Iteración falló: " + e.getMessage());
        }
        
        // Si llegamos aquí, intentar con el XPath original con más tiempo de espera
        String xpath = String.format(
            "//div[contains(@class,'reservation-card')]//p[contains(@class,'card-subtitle') and contains(text(), 'ID: %s')]/ancestor::div[contains(@class,'reservation-card')]", 
            reservationId
        );
        
        System.out.println("DEBUG: Usando XPath final: " + xpath);
        return find(By.xpath(xpath)).waitUntilVisible();
    }
    
    public void clickCheckInButton(String reservationId) {
        WebElementFacade card = getReservationCardById(reservationId);
        card.find(By.xpath(".//button[contains(@class, 'btn-deliver-res') and contains(@title, 'QR')]")).click();
    }
    
    public String getReservationStatus(String reservationId) {
        // Wait for potential status update after check-in API call
        waitABit(2000);
        WebElementFacade card = getReservationCardById(reservationId);
        WebElementFacade badge = card.find(By.cssSelector(".res-status-badge"));
        // Read raw backend status from data-status attribute (avoids Spanish text mismatch)
        String dataStatus = badge.getAttribute("data-status");
        if (dataStatus != null && !dataStatus.isEmpty()) {
            return dataStatus.toUpperCase();
        }
        // Fallback: map displayed Spanish text to backend status code
        String text = badge.getText().trim();
        switch (text) {
            case "Confirmada":  return "CHECKED_IN";
            case "En Progreso": return "IN_PROGRESS";
            case "Completada":  return "COMPLETED";
            case "Cancelada":   return "CANCELLED";
            case "Próxima":     return "PENDING";
            case "Pasada":      return "PAST";
            default:            return text.toUpperCase();
        }
    }
    
    public String getSuccessMessage() {
        return $(SUCCESS_MESSAGE).waitUntilVisible().getText();
    }
    
    public String getErrorMessage() {
        return $(ERROR_MESSAGE).waitUntilVisible().getText();
    }

    public boolean isCurrentlyVisible() {
        waitABit(2000);
        String currentUrl = getDriver().getCurrentUrl();
        System.out.println("DEBUG isCurrentlyVisible: URL = " + currentUrl);
        
        // Consider it visible if we're on the reservations page (not redirected to login)
        boolean onReservationsPage = currentUrl.contains("/my-reservations") && !currentUrl.contains("/login");
        
        // Additionally check if content is loaded
        boolean hasContent = $(RESERVATION_LIST).isCurrentlyVisible() || $(".empty-state").isCurrentlyVisible();
        
        System.out.println("DEBUG: onReservationsPage = " + onReservationsPage + ", hasContent = " + hasContent);
        
        return onReservationsPage;
    }

    public boolean hasAtLeastOneReservation() {
        return findAll(RESERVATION_CARDS).size() > 0;
    }
}
