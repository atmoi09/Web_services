package behaviourTests.steps;

import behaviourTests.DtuApiService;
import behaviourTests.dtos.*;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class ReportSteps {
    BankService bankService;
    DtuApiService dtuPayService;
    List<String> bankAccountIds = new ArrayList<>();
    AccountDTO merchantAccountDTO; // AccountDTO received for registration
    AccountDTO customerAccountDTO; // AccountDTO received for registration
    private CompletableFuture<AccountDTO> merchantAccountWithId = new CompletableFuture<>();
    private CompletableFuture<AccountDTO> customerAccountId = new CompletableFuture<>();
    private CompletableFuture<TokenIdDTO> customerToken = new CompletableFuture<>();
    private CompletableFuture<ReportDTO> customerReport = new CompletableFuture<>();
    private CompletableFuture<MerchantReportDTO> merchantReport = new CompletableFuture<>();
    private CompletableFuture<ReportDTO> managerReport = new CompletableFuture<>();
    ReportDTO customerReportReceived;
    MerchantReportDTO merchantReportReceived;
    ReportDTO managerReportReceived;

    List<String> accountIds = new ArrayList<>();
    AccountDTO merchantAccount;
    AccountDTO customerAccount;
    private List<String> tokens;
    Response customerResponse;
    Response merchantResponse;
    Response managerResponse;

    public ReportSteps() {
        bankService = new BankServiceService().getBankServicePort();
        dtuPayService = new DtuApiService();
    }

    /**
     * @author Josephine
     */
    @Given("A merchant {string} {string} with CPR {string} has a bank account with balance {int} and is registered to DTU pay")
    public void aMerchantWithCPRHasABankAccountWithBalanceAndIsRegisteredToDTUPay(String firstName, String lastName, String cpr, Integer balance) {
        merchantAccountDTO = new AccountDTO();
        merchantAccountDTO.setFirstname(firstName);
        merchantAccountDTO.setLastname(lastName);
        merchantAccountDTO.setCpr(cpr);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);

        try {
            List<AccountInfo> ids = bankService.getAccounts();
            for (var id : ids) {
                if (id.getUser().getCprNumber().equals(cpr)) {
                    bankService.retireAccount(id.getAccountId());
                }
            }

            String bankAccountId = bankService.createAccountWithBalance(user, BigDecimal.valueOf(balance));
            bankAccountIds.add(bankAccountId);
            merchantAccountDTO.setBankAccount(bankAccountId);


        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
        var response = dtuPayService.registerMerchantAccount(merchantAccountDTO);
        if (response.getStatus()==201){
            var accountDTO = response.readEntity(AccountDTO.class);
            merchantAccountWithId.complete(accountDTO);
        }
        else {
            response.close();
            merchantAccountWithId.cancel(true);
            fail("Response code: " + response.getStatus());
        }
        merchantAccount = merchantAccountWithId.join();
        accountIds.add(merchantAccount.getAccountId());
    }

    /**
     * @author Gunn
     */
    @Given("a customer {string} {string} with CPR {string} has a bank account with balance {int} and is registered to DTU pay")
    public void aCustomerWithCPRHasABankAccountWithBalanceAndIsRegisteredToDTUPay(String firstName, String lastName, String cpr, Integer balance) {
        customerAccountDTO = new AccountDTO();
        customerAccountDTO.setFirstname(firstName);
        customerAccountDTO.setLastname(lastName);
        customerAccountDTO.setCpr(cpr);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);

        try {
            List<AccountInfo> ids = bankService.getAccounts();
            for (var id : ids) {
                if (id.getUser().getCprNumber().equals(cpr)) {
                    bankService.retireAccount(id.getAccountId());
                }
            }

            String bankAccountId = bankService.createAccountWithBalance(user, BigDecimal.valueOf(balance));
            bankAccountIds.add(bankAccountId);
            customerAccountDTO.setBankAccount(bankAccountId);
            var response = dtuPayService.registerCustomerAccount(customerAccountDTO);
            if (response.getStatus()==201){
                var accountDTO = response.readEntity(AccountDTO.class);
                customerAccountId.complete(accountDTO);
            }
            else {
                response.close();
                customerAccountId.cancel(true);
                fail("Response code: " + response.getStatus());
            }
            customerAccount = customerAccountId.join();
            accountIds.add(customerAccount.getAccountId());
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Josephine
     */
    @Given("the customer has requested tokens")
    public void theCustomerHasRequestedTokens() {
        customerToken.complete(dtuPayService.requestToken(customerAccount.getAccountId(), 6));
        var tokenIdDTOReceived = customerToken.join();
        tokens = tokenIdDTOReceived.getTokenIdList();

    }

    /**
     * @author Gunn
     */
    @Given("one successful payment of {int} kr from customer to merchant has happened")
    public void oneSuccessfulPaymentFromCustomerToMerchantHasHappened(Integer amount) {
        PaymentDTO paymentDTO = new PaymentDTO();
        if (tokens.size() > 0){
            paymentDTO.setCustomerToken(tokens.get(0));
            tokens.remove(0);
        }
        else paymentDTO.setCustomerToken("No tokens");
        paymentDTO.setMerchantId(merchantAccount.getAccountId());
        paymentDTO.setAmount(BigDecimal.valueOf(amount));
        Response paymentResponse = dtuPayService.requestPayment(paymentDTO);
        assertEquals(201, paymentResponse.getStatus());
        paymentResponse.close();
    }


    /**
     * @author Josephine
     */
    @When("the customer request a report of the payments")
    public void theCustomerRequestAReportOfThePayments() {
        customerResponse = dtuPayService.requestCustomerReport(customerAccount.getAccountId());
    }

    /**
     * @author Josephine
     */
    @Then("the customer receives a report with at least {int} payments")
    public void theCustomerReceivesAReportWithPayment(Integer numberOfPayments) {
        if(customerResponse.getStatus() == 201){
            var reportDTO = customerResponse.readEntity(ReportDTO.class);

            customerReport.complete(reportDTO);
        } else if (customerResponse.getStatus() == 404) {
            var reportDTO = customerResponse.readEntity(ReportDTO.class);
            customerReport.complete(null);
            fail("ResponseCode: " + customerResponse.getStatus());
        }
        customerReportReceived = customerReport.join();
        assertTrue(customerReportReceived.getReportList().size() >= numberOfPayments);
    }

    /**
     * @author Gunn
     */
    @When("the merchant request a report of the payments")
    public void theMerchantRequestAReportOfThePayments() {
        System.out.println("merchant request payments");
        merchantResponse = dtuPayService.requestMerchantReport(merchantAccount.getAccountId());
    }


    /**
     * @author Gunn
     */
    @Then("the merchant receives a report with at least {int} payments")
    public void theMerchantReceivesAReportWithPayment(Integer numberOfPayments) {
        System.out.println("merchant receives payments");
        System.out.println("responseCode: " + merchantResponse.getStatus());
        if (merchantResponse.getStatus() == 201) {
            var reportDTO = merchantResponse.readEntity(MerchantReportDTO.class);
            System.out.println(reportDTO.getMerchantReportList().size());
            merchantReport.complete(reportDTO);
        }
        else if (merchantResponse.getStatus() == 404) {
            var reportDTO = merchantResponse.readEntity(MerchantReportDTO.class);
            merchantReport.complete(null);
            fail("ResponseCode: " + merchantResponse.getStatus());
        }
        merchantReportReceived = merchantReport.join();
        assertTrue(merchantReportReceived.getMerchantReportList().size() >= numberOfPayments);
    }


    /**
     * @author Gunn
     */
    @When("the manager request a report of the payments")
    public void theManagerRequestAReportOfThePayments() {
        managerResponse = dtuPayService.requestManagerReport();
    }


    /**
     * @author Gunn
     */
    @Then("the manager receives a report with payments")
    public void theManagerReceivesAReportWithPayments() {
            if (managerResponse.getStatus() == 201) {
                var reportDTO = managerResponse.readEntity(ReportDTO.class);
                managerReport.complete(reportDTO);
            } else if (managerResponse.getStatus() == 404) {
                var reportDTO = managerResponse.readEntity(ReportDTO.class);
                managerReport.complete(null);
                fail("ResponseCode: " + managerResponse.getStatus());
            }
        managerReportReceived = managerReport.join();
        assertTrue(managerReportReceived.getReportList().size() > 0);
    }


    /**
     * @author Gunn
     */
    @Then("the customer receives a empty report")
    public void theCustomerReceivesAEmptyReport() {
        customerReportReceived = customerResponse.readEntity(ReportDTO.class);
        assertEquals(customerResponse.getStatus(), 404);
        assertEquals(customerReportReceived.getReportList().size(), 0);
    }


    /**
     * @author Josephine
     */
    @Then("the merchant receives a empty report")
    public void theMerchantReceivesAEmptyReport() {
        merchantReportReceived = merchantResponse.readEntity(MerchantReportDTO.class);
        assertEquals(merchantResponse.getStatus(), 404);
        assertEquals(merchantReportReceived.getMerchantReportList().size(), 0);
    }


    /**
     * @author Gunn
     */
    @Then("the manager receives a empty report")
    public void theManagerReceivesAEmptyReport() {
        managerReportReceived = managerResponse.readEntity(ReportDTO.class);
        //managerReportReceived = managerReport.join();
        assertEquals(404, managerResponse.getStatus());
        assertEquals(managerReportReceived.getReportList().size(), 0);
    }


    @After
    public void removeAccounts() {
        for (String bankAccountId : bankAccountIds) {
            try {
                bankService.retireAccount(bankAccountId);
            } catch (BankServiceException_Exception e) {

            }
        }
        for (var accountId : accountIds) {
            dtuPayService.deleteCustomerAccount(accountId);
        }
    }
}
