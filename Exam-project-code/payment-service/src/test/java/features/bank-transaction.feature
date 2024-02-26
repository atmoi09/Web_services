Feature: Transaction
  #To test the bank service and get it up and running

  #Florian
  Scenario: Successful payment
    Given the "customer" "Florian" "Kesten" with CPR "000000-1234" has a bank account with balance 1000
    And that the "customer" is registered with DTU Pay
    Given the "merchant" "Fakta" "Faktorial" with CPR "111111-5672" has a bank account with balance 2000
    And that the "merchant" is registered with DTU Pay
    When the merchant initiates a payment for 100 kr by the customer
    Then the balance of the "customer" at the bank is 900 kr
    And the balance of the "merchant" at the bank is 2100 kr