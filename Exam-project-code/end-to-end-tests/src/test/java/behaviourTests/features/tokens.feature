Feature: TokenRequestFeature

  #Bence
  Scenario: Registered Customer with 6 tokens request a token
    Given person with name "Tam" "As" with cpr "000000-5555", bank accountId "56741" is registered
    When the customer asks for a token
    Then the customer receives 6 tokens
    When the customer asks again for a token
    Then the customer receives 0 tokens response

  #Tamas
  Scenario: Registered customer asks for a token
    Given person with name "Flo" "Ki" with cpr "000000-5555", bank accountId "56741" is registered
    When the customer asks for a token
    Then the customer receives 6 tokens

  #Bence
  Scenario: Unregistered customer asks for tokens
    Given person with id "56741" is not registered
    When the customer asks for a token
    Then the customer receives 0 tokens

  #Florian
  Scenario: Account Deletion deletes tokens
    Given person with name "Flo" "Ki" with cpr "000000-5555", bank accountId "56741" is registered
    When the customer asks for a token
    Then the customer receives 6 tokens
    When account is deleted
    And the customer asks again for a token
    Then the customer receives 0 tokens response

  #Tamas
  Scenario: Arbitrary number of tokens requested
    Given person with name "Flo" "Ki" with cpr "000000-5555", bank accountId "56741" is registered
    When the customer asks for 3 tokens
    Then the customer receives 3 tokens
