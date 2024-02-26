package behaviourTests.steps.accountSteps;

import behaviourTests.DtuApiService;
import behaviourTests.dtos.AccountDTO;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;


/* Hint:
 * The step classes do not do the HTTP requests themselves.
 * Instead, the tests use the class HelloService, which encapsulates the
 * HTTP requests. This abstractions help to write easier and more understandable
 * test classes.
 */

public class AccountSteps {
    DtuApiService service = new DtuApiService();
    private AccountDTO account1;
    private CompletableFuture<AccountDTO> result1 = new CompletableFuture<>();
    private AccountDTO accountReceived1;
    private AccountDTO account2;
    private CompletableFuture<AccountDTO> result2 = new CompletableFuture<>();
    private AccountDTO accountReceived2;
    private Response response1;
    private Response response2;
    private String accountId;
    private List<String> accountIds = new ArrayList<>();

    /**
     * @author Josephine
     */
    @Given("person with name {string} {string} with cpr {string}, bank accountId {string}")
    public void personWithNameWithCprBankAccountId(String firstName, String lastName, String cpr, String bankAccountId) {
        account1 = new AccountDTO();
        account1.setFirstname(firstName);
        account1.setLastname(lastName);
        account1.setCpr(cpr);
        account1.setBankAccount(bankAccountId);
        assertNull(account1.getAccountId());
    }

    /**
     * @author Josephine
     */
    @When("the user is being registered")
    public void theUserIsBeingRegistered() {
        response1 = service.registerCustomerAccount(account1);
    }

    /**
     * @author Josephine
     */
    @Then("the user is registered")
    public void theUserIsRegistered() {
        if (response1.getStatus()==201){
            var accountDTO = response1.readEntity(AccountDTO.class);
            result1.complete(accountDTO);
        }
        else {
            response1.close();
            fail("Response code: " + response1.getStatus());
        }
        accountReceived1 = result1.join();
    }

    /**
     * @author Gunn
     */
    @Then("an {string} error message is returned")
    public void anErrorMessageIsReturned(String errorMsg) {
        var receivedError = response1.readEntity(String.class);
        assertEquals(errorMsg, receivedError);
    }

    /**
     * @author Gunn
     */
    @Then("has a non empty id")
    public void hasANonEmptyId() {
        accountId = accountReceived1.getAccountId();
        accountIds.add(accountId);
        assertNotNull(accountId);
    }

    /**
     * @author Gunn
     */
    @Given("second person with name {string} {string} with cpr {string}, bank accountId {string}")
    public void secondPersonWithNameWithCprBankAccountId(String firstName, String lastName, String cpr, String bankAccountId) {
        account2 = new AccountDTO();
        account2.setFirstname(firstName);
        account2.setLastname(lastName);
        account2.setCpr(cpr);
        account2.setBankAccount(bankAccountId);
        assertNull(account2.getAccountId());
    }

    /**
     * @author Florian
     */
    @When("the two accounts are registered at the same time")
    public void theTwoAccountsAreRegisteredAtTheSameTime() {
        var thread1 = new Thread(() -> {
            response1 = service.registerCustomerAccount(account1);
            if (response1.getStatus()==201){
                var accountDTO = response1.readEntity(AccountDTO.class);
                result1.complete(accountDTO);
            }
            else {
                response1.close();
                // Try catch doesnt seem to work for Cancellation Exception
                result1.cancel(true);
                fail("Response code for account 1: " + response1.getStatus());
            }
        });
        var thread2 = new Thread(() -> {
            response2 = service.registerCustomerAccount(account2);
            if (response2.getStatus()==201){
                var accountDTO = response2.readEntity(AccountDTO.class);
                result2.complete(accountDTO);
            }
            else {
                response2.close();
                result2.cancel(true);
                fail("Response code for account 2: " + response2.getStatus());
            }
        });
        thread1.start();
        thread2.start();
    }

    /**
     * @author Gunn
     */
    @Then("the first account has a non empty id")
    public void theFirstAccountHasANonEmptyId() {
        var accountDTO = result1.join();
        assertEquals(account1.getFirstname(), accountDTO.getFirstname());
        assertNotNull(accountDTO.getAccountId());
        accountIds.add(accountDTO.getAccountId());
        System.out.println("Accountids when the first account:" + accountIds);
    }

    /**
     * @author Josephine
     */
    @Then("the second account has a non empty id different from the first student")
    public void theSecondAccountHasANonEmptyIdDifferentFromTheFirstStudent() {
        var accountDTO1 = new AccountDTO();
        var accountDTO2 = new AccountDTO();
        try {
            accountDTO1 = result1.join();
        } catch (CompletionException e) {
            fail("Response code for first account: " + response1.getStatus());
        }
        try {
            accountDTO2 = result2.join();
        } catch (CompletionException e) {
            fail("Response code for second account: " + response2.getStatus());
        }
        assertEquals(account2.getFirstname(), accountDTO2.getFirstname());
        assertNotNull(accountDTO2.getAccountId());
        accountIds.add(accountDTO2.getAccountId());
        System.out.println("Accountids when the second account:" + accountIds);
        System.out.println("Account 1 id: " + accountDTO1.getAccountId());
        System.out.println("Account 2 id: " + accountDTO2.getAccountId());
        assertNotEquals(accountDTO1.getAccountId(), accountDTO2.getAccountId());
    }

    /**
     * @author Florian
     */
    @When("user deletion is requested")
    public void userDeletionIsRequested() {
        response1 = service.deleteCustomerAccount(accountId);
    }
    @Then("the account is deleted")
    public void theAccountIsDeleted() {
        assertEquals(response1.getStatus(), 204);
        accountIds.remove(accountId);
    }

    /**
     * @author Florian
     */
    @Then("the account is not found")
    public void theAccountIsNotFound() {
        assertEquals(response1.getStatus(), 409);
    }

    /**
     * @author Florian
     */
    @After
    public void deleteAccounts(){
        for (String id : accountIds) {
            service.deleteCustomerAccount(id);
        }
        if (response1 != null) {
            response1.close();
        }
        if (response2 != null) {
            response2.close();
        }
    }
}
