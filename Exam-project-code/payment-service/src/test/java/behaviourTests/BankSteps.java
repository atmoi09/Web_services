package behaviourTests;

import domain.UserRest;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Before;
import transaction.service.BankTransactionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Florian
 */
public class BankSteps {

    String cid, mid, customerAccountId;
    List<String> accountIds = new ArrayList<>();
    //List<Payment> paymentList;
    UserRest customer, merchant;
    BankService bankService = new BankServiceService().getBankServicePort();
    BankTransactionService bankTransactionService = new BankTransactionService();

    /**
     * @author Florian
     */
    @Given("the {string} {string} {string} with CPR {string} has a bank account with balance {int}")
    public void createCustomerAccount(String userType, String firstName, String lastName, String CPR, int balance) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(CPR);
        try {
            if (userType.equals("customer")) {
                customerAccountId = bankService.createAccountWithBalance(user, BigDecimal.valueOf(balance));
                this.accountIds.add(customerAccountId);
                this.customer = new UserRest();
                this.customer.setFirstName(firstName);
                this.customer.setLastName(lastName);
                this.customer.setCprNumber(CPR);
                this.customer.setBankAccountId(customerAccountId);
            } else if (userType.equals("merchant")) {
                customerAccountId = bankService.createAccountWithBalance(user, BigDecimal.valueOf(balance));
                accountIds.add(customerAccountId);
                merchant = new UserRest();
                merchant.setFirstName(firstName);
                merchant.setLastName(lastName);
                merchant.setCprNumber(CPR);
                merchant.setBankAccountId(customerAccountId);
            }
        }
        catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Florian
     */
    @When("that the {string} is registered with DTU Pay")
    public void customerIsRegistered(String userType) {
    }

    /**
     * @author Bingkun
     */
    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) throws BankServiceException_Exception {
        bankTransactionService.transferMoney(customer.getBankAccountId(), merchant.getBankAccountId(), BigDecimal.valueOf(amount));
    }

    /**
     * @author Bingkun
     */
    @Then("the balance of the {string} at the bank is {int} kr")
    public void thePaymentIsSuccessful(String userType, int balance) throws BankServiceException_Exception {
        if (userType.equals("customer")) {
            assertEquals(balance, Integer.parseInt(bankTransactionService.getBalance(customer.getBankAccountId())));
        } else if (userType.equals("merchant")) {
            assertEquals(balance, Integer.parseInt(bankTransactionService.getBalance(merchant.getBankAccountId())));
        }
    }

    /**
     * @author Bingkun
     */
    @After
    public void removeAccounts() {
        for (String accountId : accountIds) {
            try {
                bankService.retireAccount(accountId);
            } catch (BankServiceException_Exception e) {

            }
        }
    }
}
