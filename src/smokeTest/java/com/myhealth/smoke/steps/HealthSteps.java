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

public class HealthSteps {
    
    private final ScenarioContext scenarioContext = ScenarioContext.getInstance();
    
    @Given("the endpoint details:")
    public void the_endpoint_details(DataTable dataTable) {
        Map<String, String> endpointDetails = DataTableUtil.toMap(dataTable);
        scenarioContext.setData("endpointDetails", endpointDetails);
    }
    
    @When("I call the health check endpoint")
    public void i_call_the_health_check_endpoint() {
        Map<String, String> details = scenarioContext.getData("endpointDetails", Map.class);
        String path = details.get("path");
        
        Response response = given()
        .when()
            .get(path)
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @When("I call the info endpoint")
    public void i_call_the_info_endpoint() {
        Map<String, String> details = scenarioContext.getData("endpointDetails", Map.class);
        String path = details.get("path");
        
        Response response = given()
        .when()
            .get(path)
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @Then("the response should contain expected fields:")
    public void the_response_should_contain_expected_fields(DataTable dataTable) {
        Response response = scenarioContext.getLastResponse();
        String body = response.getBody().asString();
        assertNotNull(body);
        Map<String, String> expectedFields = DataTableUtil.toMap(dataTable);
        for (Map.Entry<String, String> entry : expectedFields.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            assertTrue(body.contains("\"" + field + "\":\"" + value + "\""),
                "Response should contain " + field + ":" + value);
        }
    }
}