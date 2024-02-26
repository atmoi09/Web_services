Feature: Payment service feature

  #Bingkun
  Scenario: Payment Conducted
    Given a merchant "mid1" "Bingkun" "Wu" "270791" has a bank account with 100 kr
    And a customer "cid1" "Trump" "Donald" "231298" has a bank account with 100 kr
    When a "PaymentInitiated" event is received with 100 kr payment amount
    Then the "TokenValidationRequested" event is sent to validate the token
    When the "TokenValidated" event is received with non-empty customerId
    Then the "BankAccountRequested" event is sent to inquire the bankAccountId
    When the "BankAccountReceived" event is received with non-empty bankAccountIds
    Then the "PaymentCompleted" event is sent and payment completes
    And the balance of merchant "mid" at the bank is 200 kr
    And the balance of customer "cid" at the bank is 0 kr

  #Bingkun
  Scenario: Token Invalid
    Given a merchant "mid2" "A" "B" "564446" has a bank account with 100 kr
    And a customer "cid2" "C" "D" "232293" has a bank account with 100 kr
    When a "PaymentInitiated" event is received with 100 kr payment amount
    Then the "TokenValidationRequested" event is sent to validate the token
    When the "TokenInvalid" event is received with null customerId
    Then the "PaymentError" event is sent with error message