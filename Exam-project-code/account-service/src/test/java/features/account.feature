Feature: Registration

  #Gunn
  Scenario: Successful Registration to DTUPay
    When a "AccountRequested" event for a customer with name "Josephine", surname "Mellin", cpr "000000-1234", bank account "1234" is received
    And the account gets an account with id "1"
    Then the "AccountProvided" event is sent

  #Josephine
  Scenario: Existing account
    When a "AccountRequested" event for a customer with name "Josephine", surname "Mellin", cpr "000000-1234", bank account "1234" is received
    When a "AccountRequested" event for a customer with name "Josephine", surname "Mellin", cpr "000000-1234", bank account "1234" is received
    Then the "AccountExists" event is sent with error message "Account already exists"

  #Josephine
  Scenario: Delete account
    When a "AccountRequested" event for a customer with name "Florian", surname "Keste", cpr "000000-5321", bank account "1111" is received
    Then the account gets an account with id "1"
    When the AccountDeletionRequested event with accountId "1" is received
    Then the AccountDeleted event is sent

  #Florian
  Scenario: Account check on non existent account
    When a AccountCheckRequested event with accountId "0" is received
    Then the AccountCheckResultProvided event is sent with 0 as value

  #Florian
  Scenario: Account check on existent account
    When a "AccountRequested" event for a customer with name "Florian", surname "Keste", cpr "000000-5321", bank account "1111" is received
    Then the account gets an account with id "1"
    When a AccountCheckRequested event with accountId "1" is received
    Then the AccountCheckResultProvided event is sent with 1 as value