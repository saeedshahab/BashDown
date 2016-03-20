Feature: Database Wrapper operations

  Background: Ensure healthy and clean database
    When the database is pinged
    Then the database should be healthy
    And that the database of model is dropped

  Scenario: Drop database
    Given that a model with id: abcd1234 is inserted
    And that a model with id: qwerty0987 is inserted
    When that the database of model is dropped
    Then the model with id: abcd1234 should be absent
    And the model with id: qwerty0987 should be absent

  Scenario: Delete a document
    Given that a model with id: abcd1234 is inserted
    When the model with id: abcd1234 is deleted
    Then the model with id: abcd1234 should be present
    And the model with id: abcd1234 should be absent

  Scenario: Search for a document
    Given that a model with id: abcd1234 is inserted
    When a model with id: abcd1234 is looked up
    Then the model with id: abcd1234 should be present

  Scenario: Search and update a document
    Given that a model with id: abcd1234 is inserted
    When the model with id: abcd1234 is replaced with id: qwerty0987
    And a model with id: qwerty0987 is looked up
    Then the model with id: qwerty0987 should be present
    And the model with id: abcd1234 should be absent
    And the model with id: qwerty0987 should be deleted

