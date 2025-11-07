@smoke
Feature: Authentication Flow
  As a user
  I want to authenticate with the system
  So that I can access protected resources

  Scenario Outline: User registration with test data
    Given the registration payload:
      | email      | <email>      |
      | password   | <password>   |
      | firstname  | <firstname>  |
      | lastname   | <lastname>   |
    When I call the registration endpoint
    Then the response status should be <status>

    Examples:
      | email              | password    | firstname | lastname | status |
      | smoke1@test.com    | testpass123 | Smoke     | User1    | 409    |
      | smoke2@test.com    | testpass456 | Smoke     | User2    | 409    |

  Scenario Outline: User login with valid credentials
    Given the login payload:
      | username | <username> |
      | password | <password> |
    When I call the login endpoint
    Then the response status should be <status>
    And the response should contain access token
    And the response should contain refresh token

    Examples:
      | username              | password   | status |
      | smokeuser@test.com    | smokepass  | 200    |
      | smokeadmin@test.com   | adminpass  | 200    |

  Scenario Outline: Login fails with invalid credentials
    Given the login payload:
      | username | <username> |
      | password | <password> |
    When I call the login endpoint
    Then the response status should be <status>

    Examples:
      | username | password | status |
      | invalid  | invalid  | 400    |
      | wrong    | wrong    | 400    |