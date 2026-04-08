package com.reservassofka.pom.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class QrScannerModal extends PageObject {

    // The modal inner container uses class "qr-scan-modal" (alongside handover-modal)
    private static final String MODAL_SELECTOR = ".qr-scan-modal";

    public boolean isModalOpen() {
        try {
            return $(By.cssSelector(MODAL_SELECTOR)).waitUntilVisible().isCurrentlyVisible();
        } catch (Exception e) {
            System.out.println("⚠ QR modal not visible (selector: " + MODAL_SELECTOR + "): " + e.getMessage());
            return false;
        }
    }

    public boolean isModalClosed() {
        try {
            $(By.cssSelector(MODAL_SELECTOR)).waitUntilNotVisible();
        } catch (Exception ignored) {
            // Element may already be gone from DOM
        }
        return !$(By.cssSelector(MODAL_SELECTOR)).isCurrentlyVisible();
    }

    /**
     * html5-qrcode manages camera permissions with its own UI.
     * If a "Start Scanning" button is present, click it; otherwise wait for the scanner to initialize.
     */
    public void grantCameraPermissions() {
        waitABit(1500); // Let html5-qrcode initialize
        // html5-qrcode renders a file/camera mode selection. The camera option
        // is an anchor/button; try to click it if visible.
        try {
            By cameraBtn = By.cssSelector("#html5_qrcoderegion__button_camera_start_stop," +
                    "#html5-qrcode-button-camera-permission," +
                    "[id*='html5'][id*='camera']");
            if ($(cameraBtn).isCurrentlyVisible()) {
                $(cameraBtn).click();
                waitABit(1000);
            }
        } catch (Exception e) {
            System.out.println("  Camera start button not required / not found: " + e.getMessage());
        }
    }

    /**
     * Simulates a QR scan by dispatching the 'qr-scanned' custom event on window.
     * ModalScanQr.jsx must have a listener registered for this event (automation hook).
     *
     * @param token The QR token content (spaceName for integration test, JWT for real scenarios)
     */
    public void scanValidQrCode(String token) {
        System.out.println("  Dispatching qr-scanned event with token: " + token);
        evaluateJavascript(
            "window.dispatchEvent(new CustomEvent('qr-scanned', { detail: { token: arguments[0] } }));",
            token
        );
        waitABit(2000); // Wait for check-in API call to complete
    }
}
