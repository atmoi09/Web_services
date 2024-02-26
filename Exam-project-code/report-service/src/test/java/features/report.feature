Feature: Report

  #Josephine
  Scenario: Add payment to report request handling
    Given a payment with paymentId "0", customerId "1", merchantId "2", amount 100, and description "test"
    When a "PaymentCompletedForReport" event is received for the payment
    Then the payment is added to the merchant report
    And the payment is added to the customer report
    And the payment is added to the manager report

#Josephine
  Scenario: Report requested for customer, merchant and manager
    Given a payment with paymentId "0", customerId "1", merchantId "2", amount 100, and description "test"
    When a "PaymentCompletedForReport" event is received for the payment
    Then the payment is added to the merchant report
    And the payment is added to the customer report
    And the payment is added to the manager report
    When a "CustomerReportRequested" event is received for the customer report
    Then the "CustomerReportProvided" event is sent to customer

#Gunn
  Scenario: Report requested for customer with no payment
    When a "CustomerReportRequested" event is received for a customer with no payments
    Then the "CustomerReportProvided" event is sent to customer with no payments

  #Gunn
  Scenario: Report requested for merchant with no payment
    When a "MerchantReportRequested" event is received for a merchant with no payments
    Then the "MerchantReportProvided" event is sent to merchant with no payments

#Gunn
  Scenario: Report requested for manager with no payment
    When a "ManagerReportRequested" event is received for a manager with no payments
    Then the "ManagerReportProvided" event is sent to manager with no payments




