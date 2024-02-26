Feature: Request report feature


  #Josephine
  Scenario: Successful customer request of report
    Given A merchant "Micro" "Service" with CPR "112233-2345" has a bank account with balance 400 and is registered to DTU pay
    And a customer "Gunn" "Hentze" with CPR "122892-1234" has a bank account with balance 200 and is registered to DTU pay
    And the customer has requested tokens
    And one successful payment of 100 kr from customer to merchant has happened
    When the customer request a report of the payments
    Then the customer receives a report with at least 1 payments

  #Gunn
  Scenario: Successful merchant request of report
    Given A merchant "Service" "Micro" with CPR "332211-2345" has a bank account with balance 400 and is registered to DTU pay
    And a customer "Hentze" "Gunn" with CPR "435216-1234" has a bank account with balance 200 and is registered to DTU pay
    And the customer has requested tokens
    And one successful payment of 100 kr from customer to merchant has happened
    When the merchant request a report of the payments
    Then the merchant receives a report with at least 1 payments

  #Josephine
  Scenario: Successful manager request of report
    Given A merchant "Service" "Micro" with CPR "125497-2345" has a bank account with balance 400 and is registered to DTU pay
    And a customer "Yoss" "Mellin" with CPR "150291-1234" has a bank account with balance 500 and is registered to DTU pay
    And the customer has requested tokens
    And one successful payment of 100 kr from customer to merchant has happened
    When the manager request a report of the payments
    Then the manager receives a report with payments

  #Josephine
  Scenario: A customer requests a report and receives an empty one
    Given a customer "Yoss" "Mellin" with CPR "141491-1234" has a bank account with balance 500 and is registered to DTU pay
    When the customer request a report of the payments
    Then the customer receives a empty report

  #Gunn
  Scenario: A merchant requests a report and receives an empty one
    Given A merchant "Service" "Micro" with CPR "421567-2345" has a bank account with balance 400 and is registered to DTU pay
    When the merchant request a report of the payments
    Then the merchant receives a empty report