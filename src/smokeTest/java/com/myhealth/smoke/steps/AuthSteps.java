package com.myhealth.smoke.steps;

import com.myhealth.smoke.context.ScenarioContext;
import com.myhealth.smoke.util.DataTableUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AuthSteps {
    
    private final ScenarioContext scenarioContext = ScenarioContext.getInstance();
    
    @Given("the registration payload:")
    public void the_registration_payload(DataTable dataTable) {
        Map<String, String> payload = DataTableUtil.toMap(dataTable);
        scenarioContext.setData("registrationPayload", payload);
    }
    
    @Given("the login payload:")
    public void the_login_payload(DataTable dataTable) {
        Map<String, String> payload = DataTableUtil.toMap(dataTable);
        scenarioContext.setData("loginPayload", payload);
    }
    
    @When("I call the registration endpoint")
    public void i_call_the_registration_endpoint() {
        Map<String, String> payload = scenarioContext.getData("registrationPayload", Map.class);
        
        Response response = given()
            .contentType("application/json")
            .body(payload)
        .when()
            .post("/api/auth/register")
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @When("I call the login endpoint")
    public void i_call_the_login_endpoint() {
        Map<String, String> payload = scenarioContext.getData("loginPayload", Map.class);
        
        Response response = given()
            .contentType("application/json")
            .body(payload)
        .when()
            .post("/api/auth/login")
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @Then("the response should contain user information")
    public void the_response_should_contain_user_information() {
        Response response = scenarioContext.getLastResponse();
        String body = response.getBody().asString();
        assertNotNull(body);
        assertTrue(body.contains("username") || body.contains("email"));
    }
    
    @Then("the response should contain access token")
    public void the_response_should_contain_access_token() {
        Response response = scenarioContext.getLastResponse();
        String accessToken = response.jsonPath().getString("accessToken");
        assertNotNull(accessToken);
        scenarioContext.setAccessToken(accessToken);
    }
    
    @Then("the response should contain refresh token")
    public void the_response_should_contain_refresh_token() {
        Response response = scenarioContext.getLastResponse();
        String refreshToken = response.jsonPath().getString("refreshToken");
        assertNotNull(refreshToken);
    }
}