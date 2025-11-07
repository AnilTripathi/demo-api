package com.myhealth.smoke.steps;

import com.myhealth.smoke.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonSteps {
    
    private final ScenarioContext scenarioContext = ScenarioContext.getInstance();
    
    @Then("the response status should be {int}")
    public void the_response_status_should_be(int expectedStatus) {
        Response response = scenarioContext.getLastResponse();
        assertEquals(expectedStatus, response.getStatusCode());
    }
}