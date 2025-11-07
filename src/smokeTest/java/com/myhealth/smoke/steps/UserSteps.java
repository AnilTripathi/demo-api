package com.myhealth.smoke.steps;

import com.myhealth.smoke.context.ScenarioContext;
import com.myhealth.smoke.util.DataTableUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class UserSteps {
    
    private final ScenarioContext scenarioContext = ScenarioContext.getInstance();
    private final AuthSteps authSteps = new AuthSteps();
    
    @Given("the login credentials:")
    public void the_login_credentials(DataTable dataTable) {
        Map<String, String> credentials = DataTableUtil.toMap(dataTable);
        scenarioContext.setData("loginCredentials", credentials);
    }
    
    @Given("I am authenticated with these credentials")
    public void i_am_authenticated_with_these_credentials() {
        Map<String, String> credentials = scenarioContext.getData("loginCredentials", Map.class);
        authSteps.the_login_payload(io.cucumber.datatable.DataTable.create(
            List.of(
                List.of("username", credentials.get("username")),
                List.of("password", credentials.get("password"))
            )
        ));
        authSteps.i_call_the_login_endpoint();
        authSteps.the_response_should_contain_access_token();
    }
    
    @When("I request my profile")
    public void i_request_my_profile() {
        String token = scenarioContext.getAccessToken();
        
        Response response = given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/me")
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @When("I request all users without authentication")
    public void i_request_all_users_without_authentication() {
        Response response = given()
        .when()
            .get("/api/users")
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @When("I request all users")
    public void i_request_all_users() {
        String token = scenarioContext.getAccessToken();
        
        Response response = given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users")
        .then()
            .extract().response();
            
        scenarioContext.setLastResponse(response);
    }
    
    @Then("the response should contain profile fields:")
    public void the_response_should_contain_profile_fields(DataTable dataTable) {
        Response response = scenarioContext.getLastResponse();
        String body = response.getBody().asString();
        assertNotNull(body);
        List<String> expectedFields = dataTable.asList(String.class);
        for (String field : expectedFields) {
            assertTrue(body.contains(field), "Response should contain field: " + field);
        }
    }
    
    @Then("the response should contain a list of users")
    public void the_response_should_contain_a_list_of_users() {
        Response response = scenarioContext.getLastResponse();
        String body = response.getBody().asString();
        assertNotNull(body);
        assertTrue(body.startsWith("["));
    }
}