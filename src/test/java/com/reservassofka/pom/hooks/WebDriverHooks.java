package com.reservassofka.pom.hooks;

import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverHooks {
    
    @Before
    public void setupWebDriver() {
        WebDriverManager.chromedriver().setup();
    }
}
