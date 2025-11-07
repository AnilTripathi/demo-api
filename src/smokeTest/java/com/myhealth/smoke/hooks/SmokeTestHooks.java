package com.myhealth.smoke.hooks;

import com.myhealth.smoke.config.SmokeTestConfig;
import com.myhealth.smoke.context.ScenarioContext;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class SmokeTestHooks {
    
    @Before
    public void setUp() {
        // Configure RestAssured base URI
        RestAssured.baseURI = SmokeTestConfig.getBaseUrl();
        
        // Clear scenario context
        ScenarioContext.getInstance().clear();
        
        // Verify application is running by checking health endpoint
        try {
            Response healthCheck = given()
                .when()
                .get("/actuator/health")
                .then()
                .extract().response();
            
            if (healthCheck.getStatusCode() != 200) {
                throw new RuntimeException("Application health check failed. Status: " + healthCheck.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to application at " + SmokeTestConfig.getBaseUrl() + 
                ". Ensure the application is running before executing smoke tests.", e);
        }
    }
}