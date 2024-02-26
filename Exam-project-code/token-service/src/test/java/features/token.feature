Feature: Token

  #Bence
  Scenario: Token creation
    Given The customerID is "Alice"
    And he has 1 tokens already
    When the token is created
    Then tokenID is valid

  #Tamas
  Scenario: Token creation 2 tokens
    Given The customerID is "Mary"
    When the token is created
    Given The customerID is "Yoss"
    When the token is created
    Then tokenID is valid

  #Bence
  Scenario: Token creation, too many tokens
    Given The customerID is "Bob"
    And he has 2 tokens already
    When the token is created
    Then the error message is "Too many tokens"

  #Tamas
  Scenario: Token validation
    Given The customerID is "George"
    And the token is created
    When his token is being checked
    Then the validation is "successful"

  #Bence
  Scenario: Token validation, failed
    Given The customerID is "Elisa"
    When his token is being checked
    Then the validation is "unsuccessful"

  #Tamas
  Scenario: Token deletion
    Given The customerID is "Karen"
    And the token is created
    When his token is being deleted
    Then the token is deleted

  #Bence
  Scenario: Token deletion, not found
    Given The customerID is "Leo"
    When his token is being deleted
    Then the token is deleted

  #Florian
  Scenario: Token request handling
    Given there is a user "Joe"
    When a "TokenCreationRequested" event is received
    Then a AccountCheckRequested event is published
    When a AccountCheckResultProvided event is received with true result
    And the "TokenProvided" event is sent



  Scenario: Account deletion request handling
    When a TokenCreationRequested event for userId "Florian" is received
    Then the user has 6 tokens
    When a AccountDeleted event for userId "Florian" is received
    Then the all tokens for that user are deleted

