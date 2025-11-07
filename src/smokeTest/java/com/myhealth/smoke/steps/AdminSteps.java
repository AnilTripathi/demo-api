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

public class AdminSteps {
    
    private final ScenarioContext scenarioContext = ScenarioContext.getInstance();
    
    @Given("the admin user creation payload:")
    public void the_admin_user_creation_payload(DataTable dataTable) {
        Map<String, String> payload = DataTableUtil.toMap(dataTable);
        scenarioContext.setData("adminPayload", payload);
    }
    
    @When("I call the admin user creation endpoint")
    public void i_call_the_admin_user_creation_endpoint() {
        String token = scenarioContext.getAccessToken();
        Map<String, String> payload = scenarioContext.getData("adminPayload", Map.class);
        
        Response response = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(payload)
        .when()
            .post("/api/admin/user")
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @Then("the response should contain created user profile")
    public void the_response_should_contain_created_user_profile() {
        Response response = scenarioContext.getLastResponse();
        String body = response.getBody().asString();
        assertNotNull(body);
        assertTrue(body.contains("email"));
        assertTrue(body.contains("firstName"));
    }
}