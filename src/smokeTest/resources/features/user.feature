@smoke
Feature: User Profile Management
  As an authenticated user
  I want to manage my profile
  So that I can keep my information up to date

  Scenario Outline: Get current user profile with authentication
    Given the login credentials:
      | username | <username> |
      | password | <password> |
    And I am authenticated with these credentials
    When I request my profile
    Then the response status should be <status>
    And the response should contain profile fields:
      | email     |
      | firstName |
      | lastName  |

    Examples:
      | username              | password  | status |
      | smokeuser@test.com    | smokepass | 200    |
      | smokeadmin@test.com   | adminpass | 200    |

  Scenario: Get all users requires authentication
    When I request all users without authentication
    Then the response status should be 403

  Scenario Outline: Get all users with authentication
    Given the login credentials:
      | username | <username> |
      | password | <password> |
    And I am authenticated with these credentials
    When I request all users
    Then the response status should be <status>
    And the response should contain a list of users

    Examples:
      | username              | password  | status |
      | smokeuser@test.com    | smokepass | 200    |
      | smokeadmin@test.com   | adminpass | 200    |