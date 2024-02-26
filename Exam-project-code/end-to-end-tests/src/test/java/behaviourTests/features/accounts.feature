Feature: AccountRequestFeature

  #Gunn
  Scenario: Account Registration
    Given person with name "Gunn" "Gunn" with cpr "000000-1234", bank accountId "56741"
    When the user is being registered
    Then the user is registered
    And has a non empty id

  #Florian
  Scenario: Account Registration with existing cpr
    Given person with name "Florian" "Kesten" with cpr "000000-1234", bank accountId "56741"
    When the user is being registered
    Then the user is registered
    And has a non empty id
    Given person with name "Florian" "Kesten" with cpr "000000-1234", bank accountId "56741"
    When the user is being registered
    Then an "Account already exists" error message is returned

  #Gunn
  Scenario: Account Registration Race Condition
    Given person with name "Yoss" "Mellin" with cpr "000000-5497", bank accountId "56741"
    And second person with name "Bingkun" "Wu" with cpr "000000-5678", bank accountId "45897"
    When the two accounts are registered at the same time
    Then the first account has a non empty id
    And the second account has a non empty id different from the first student

  #Florian
  Scenario: Account Deletion
    Given person with name "Flo" "Ki" with cpr "000000-5555", bank accountId "56741"
    When the user is being registered
    Then the user is registered
    And has a non empty id
    When user deletion is requested
    Then the account is deleted

  #Josephin
  Scenario: Account Deletion of a non existing user
    Given person with name "Yoss" "Mellin" with cpr "000000-0000", bank accountId "56741"
    When user deletion is requested
    Then the account is not found
