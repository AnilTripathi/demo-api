@smoke
Feature: Health Check Endpoints
  As a system administrator
  I want to verify that health endpoints are working
  So that I can monitor the application status

  Scenario Outline: Health endpoint verification
    Given the endpoint details:
      | path   | <path>   |
      | method | <method> |
    When I call the health check endpoint
    Then the response status should be <status>
    And the response should contain expected fields:
      | status | <expectedStatus> |

    Examples:
      | path             | method | status | expectedStatus |
      | /actuator/health | GET    | 200    | UP             |

  Scenario Outline: Info endpoint verification
    Given the endpoint details:
      | path          | <path>   |
      | method        | <method> |
    When I call the info endpoint
    Then the response status should be <status>

    Examples:
      | path           | method | status |
      | /actuator/info | GET    | 500    |