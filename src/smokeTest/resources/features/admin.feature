@smoke
Feature: Admin Access Control
  As a system administrator
  I want to verify admin endpoints are properly secured
  So that only authorized users can access admin functions

  Scenario Outline: Admin endpoint access control
    Given the login credentials:
      | username | <username> |
      | password | <password> |
    And I am authenticated with these credentials
    And the admin user creation payload:
      | email     | <email>     |
      | firstname | <firstname> |
      | lastname  | <lastname>  |
      | gender    | <gender>    |
    When I call the admin user creation endpoint
    Then the response status should be <status>

    Examples:
      | username              | password  | email           | firstname | lastname | gender | status |
      | smokeuser@test.com    | smokepass | test@admin.com  | Test      | User     | Male   | 403    |
      | smokeadmin@test.com   | adminpass | admin@test.com  | Admin     | Test     | Female | 403    |