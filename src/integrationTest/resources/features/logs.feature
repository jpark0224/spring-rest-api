Feature: Workout Log Completion

  Scenario: Completing a log sets end time, generates PRs, sends message, assembles a log model, and returns a response entity
    Given a log exists in the log repository
    And the log contains one or more sets
    And one or more personal records can be achieved
    When the log is completed
    Then the log should have an end time
    And the personal record(s) should be saved
    And a message should be sent to the SQS queue
    And a log model should be assembled
    And a response entity should be returned