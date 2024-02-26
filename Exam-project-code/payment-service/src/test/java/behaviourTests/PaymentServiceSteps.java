package behaviourTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import domain.CorrelationId;
import dtos.BankAccountRequestDTO;
import dtos.PaymentDTO;
import dtos.TokenValidationDTO;
import domain.Payment;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mappers.Mapper;
import messaging.Event;
import messaging.MessageQueue;
import payment.service.PaymentService;
import transaction.service.BankTransactionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bingkun
 */
public class PaymentServiceSteps {
    static int id = 0;
    private String nextId() {
        id++;
        return Integer.toString(id);
    }

    MessageQueue queue = mock(MessageQueue.class);
    PaymentService paymentService = new PaymentService(queue);
    Payment payment;
    String merchantId, customerId;
    String merchantBankAccountId, customerBankAccountId;
    TokenValidationDTO tokenValidationDTO;
    BankAccountRequestDTO bankAccountRequestDTO;
    BankService bankService = new BankServiceService().getBankServicePort();
    BankTransactionService bankTransactionService = new BankTransactionService();
    List<String> accountIds = new ArrayList<>();
    CorrelationId correlationId;

    /**
     * @author Florian
     */
    @Given("a merchant {string} {string} {string} {string} has a bank account with {int} kr")
    public void merchantHasBank(String merchantId, String firstName, String lastName, String cpr, Integer amount) {
        this.merchantId = merchantId;
        User user = new User();
        user.setCprNumber(cpr);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        try {
            this.merchantBankAccountId = bankService.createAccountWithBalance(user, BigDecimal.valueOf(amount));
            accountIds.add(this.merchantBankAccountId);
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Florian
     */
    @Given("a customer {string} {string} {string} {string} has a bank account with {int} kr")
    public void customerHasBank(String customerId, String firstName, String lastName, String cpr, Integer amount) {
        this.customerId = customerId;
        User user = new User();
        user.setCprNumber(cpr);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        try {
            this.customerBankAccountId = bankService.createAccountWithBalance(user, BigDecimal.valueOf(amount));
            accountIds.add(this.customerBankAccountId);
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Bingkun
     */
    @When("a {string} event is received with {int} kr payment amount")
    public void aEventForPayment(String eventType, Integer amount) {
        this.correlationId = CorrelationId.randomId();
        payment = new Payment();
        payment.setCustomerToken("1234");
        payment.setMerchantId(merchantId);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentId(nextId());
        assertNull(payment.getDescription());
        PaymentDTO paymentDTO = new PaymentDTO();
        Mapper.mapPaymentToDTO(payment, paymentDTO);
        paymentService.handlePaymentInitiated(new Event(eventType, new Object[] {paymentDTO, correlationId}));
    }

    /**
     * @author Florian
     */
    @Then("the {string} event is sent to validate the token")
    public void theTokenEventIsSent(String eventType) {
        tokenValidationDTO = new TokenValidationDTO();
        Mapper.mapTokenValidationDTO(this.payment, tokenValidationDTO);
        var event = new Event(eventType, new Object[] {tokenValidationDTO, correlationId});
        verify(queue).publish(event);
    }

    /**
     * @author Bingkun
     */
    @When("the {string} event is received with non-empty customerId")
    public void theEventIsReceivedWithCustomerId(String eventType) {
        tokenValidationDTO.setCustomerId(customerId);
        paymentService.handleTokenValidated(new Event(eventType, new Object[] {tokenValidationDTO, correlationId}));
    }

    /**
     * @author Bingkun
     */
    @Then("the {string} event is sent to inquire the bankAccountId")
    public void theEventIsSentToInquiryBankAccount(String eventType) {
        bankAccountRequestDTO = new BankAccountRequestDTO();
        Mapper.mapBankAccountRequestDTO(payment, tokenValidationDTO, bankAccountRequestDTO);
        var event = new Event(eventType, new Object[] {bankAccountRequestDTO, correlationId});
        verify(queue).publish(event);
    }

    /**
     * @author Bingkun
     */
    @When("the {string} event is received with non-empty bankAccountIds")
    public void theEventIsReceivedWithBankAccount(String eventType) {
        bankAccountRequestDTO.setCustomerBankAccount(customerBankAccountId);
        bankAccountRequestDTO.setMerchantBankAccount(merchantBankAccountId);
        paymentService.handleBankAccountProvided(new Event(eventType, new Object[] {bankAccountRequestDTO, correlationId}));
    }

    /**
     * @author Bingkun
     */
    @Then("the {string} event is sent and payment completes")
    public void thePaymentEventIsSentAndNotPending(String eventType) {
        PaymentDTO paymentDTO = new PaymentDTO();
        Mapper.mapPaymentToDTO(payment, paymentDTO);
        var event = new Event(eventType, new Object[] {paymentDTO, correlationId});
        verify(queue).publish(event);
    }

    /**
     * @author Bingkun
     */
    @Then("the balance of merchant {string} at the bank is {int} kr")
    public void checkMerchantBalance(String merchantId, Integer balance) {
        try {
            assertEquals(balance, Integer.parseInt(bankTransactionService.getBalance(this.merchantBankAccountId)));
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Bingkun
     */
    @Then("the balance of customer {string} at the bank is {int} kr")
    public void checkCustomerBalance(String customerId, Integer balance) {
        try {
            assertEquals(balance, Integer.parseInt(bankTransactionService.getBalance(this.customerBankAccountId)));
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Florian
     */
    @When("the {string} event is received with null customerId")
    public void theTokenInvalidEventIsReceived(String eventType) {
        TokenValidationDTO tokenValidationDTO = new TokenValidationDTO();
        Mapper.mapTokenValidationDTO(payment, tokenValidationDTO);
        Event event = new Event(eventType, new Object[] {tokenValidationDTO, correlationId});
        paymentService.handleTokenInvalid(event);
    }

    /**
     * @author Florian
     */
    @Then("the {string} event is sent with error message")
    public void thePaymentTokenInvalidIsSent(String eventType) {
        PaymentDTO paymentDTO = new PaymentDTO();
        Mapper.mapPaymentToDTO(payment, paymentDTO);
        paymentDTO.setErrorDescription("Token invalid");
        Event event = new Event(eventType, new Object[] {paymentDTO, correlationId});
        verify(queue).publish(event);
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
