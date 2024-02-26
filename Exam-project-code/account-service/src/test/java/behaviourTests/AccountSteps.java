package behaviourTests;

import account.service.AccountRepository;
import account.service.domain.Account;
import account.service.AccountService;
import account.service.dtos.AccountDTO;
import account.service.CorrelationId;
import account.service.dtos.Mapper;
import io.cucumber.java.an.E;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccountSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private AccountRepository accountRepository = new AccountRepository();
    private AccountService accountService = new AccountService(queue, accountRepository);
    private CorrelationId correlationId;
    Account customerAccount;
    String accountId;
    List<String> accountIds = new ArrayList<>();


    /**
     *
     * @author Gunn
     */
    @When("a {string} event for a customer with name {string}, surname {string}, cpr {string}, bank account {string} is received")
    public void anEventForAnCustomerAccountIsReceived(String eventName, String name, String surname,
                                                      String cpr, String bankAcc) {
        customerAccount = new Account();
        customerAccount.setFirstname(name);
        customerAccount.setLastname(surname);
        customerAccount.setCpr(cpr);
        customerAccount.setBankAccount(bankAcc);
        assertNull(customerAccount.getAccountId());
        AccountDTO accountDTO = new AccountDTO();
        Mapper.mapAccountToDTO(customerAccount, accountDTO);
        correlationId = CorrelationId.randomId();
        accountService.handleAccountRequested(new Event(eventName,new Object[] {accountDTO, correlationId}));
    }

    /**
     *
     * @author Gunn
     */
    @Then("the account gets an account with id {string}")
    public void theAccountGetsId(String id) {
        customerAccount.setAccountId(id);
        accountIds.add(id);
        var actual = accountRepository.getAccount(id);
        assertEquals(customerAccount, actual);
    }

    /**
     *
     * @author Josephine
     */
    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        var accountDTO = new AccountDTO();
        Mapper.mapAccountToDTO(customerAccount, accountDTO);
        var event = new Event(eventName, new Object[] {accountDTO, correlationId});
        verify(queue).publish(event);
    }

    /**
     *
     * @author Gunn
     */
    @Then("the {string} event is sent with error message {string}")
    public void the_event_is_sent_with_error_message(String eventType, String errorMsg) {
        AccountDTO accountDTO = new AccountDTO();
        Mapper.mapAccountToDTO(customerAccount, accountDTO);
        accountDTO.setErrorMessage(errorMsg);
        var event =  new Event(eventType, new Object[]{accountDTO, correlationId});
        verify(queue).publish(event);
    }

    /**
     *
     * @author Josephine
     */
    @When("the AccountDeletionRequested event with accountId {string} is received")
    public void the_event_with_account_id_is_received(String accountId) {
        correlationId = CorrelationId.randomId();
        this.accountId = accountId;
        accountService.handleAccountDeletionRequested(new Event("AccountDeletionRequested",new Object[] {accountId, correlationId}));
    }

    /**
     *
     * @author Gunn
     */
    @Then("the AccountDeleted event is sent")
    public void the_account_deleted_event_is_sent() {
        Event event = new Event("AccountDeleted", new Object[]{accountId, correlationId});
        accountIds.remove(accountId);
        verify(queue).publish(event);
    }

    /**
     *
     * @author Florian
     */
    @When("a AccountCheckRequested event with accountId {string} is received")
    public void a_account_check_requested_event_with_account_id_is_received(String accountId) {
        correlationId = CorrelationId.randomId();
        this.accountId = accountId;
        Event event = new Event("AccountCheckRequested",new Object[] {accountId, correlationId});
        accountService.handleAccountCheckRequested(event);
    }

    /**
     *
     * @author Florian
     */
    @Then("the AccountCheckResultProvided event is sent with {int} as value")
    public void the_account_check_result_provided_event_is_sent_with_bool_as_value(int intBool) {
        Boolean b;
        b = intBool == 0 ? false : true;
        Event event = new Event("AccountCheckResultProvided",new Object[] {b, correlationId});
        verify(queue).publish(event);
    }
}