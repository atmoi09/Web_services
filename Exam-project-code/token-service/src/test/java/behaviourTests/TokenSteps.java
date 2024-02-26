package behaviourTests;

import DTOs.TokenIdDTO;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.junit.Assert;
import token.service.CorrelationId;
import token.service.TokenRepository;
import token.service.TokenService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TokenSteps {
    private String customerID;
    private String tokenID;
    private List<String> tokenIDList = new ArrayList<>();;
    private boolean valid;
    private Exception error;

    private MessageQueue queue = mock(MessageQueue.class);
    private TokenRepository mockRepository = mock(TokenRepository.class);
    private TokenRepository repository = new TokenRepository();
    private TokenService service = new TokenService(queue, repository);
    private TokenService service2 = new TokenService(queue, mockRepository);
    private CorrelationId correlationId;
    private CorrelationId correlationAccountCheckId;

    private List<String> tokenIds = new ArrayList<>();
    private Map<String, CompletableFuture<Event>> accountCheckEventsPublished = new HashMap<>();
    private Map<CorrelationId, CompletableFuture<Event>> otherEventsPublished = new HashMap<>();

    /**
     * @author Florian, based on Huberts implementation
     */
    private MessageQueue customQueue = new MessageQueue() {
        @Override
        public void publish(Event event) {
            if (event.getType().equals("AccountCheckRequested")){
                var customerId = event.getArgument(0, String.class);
                correlationAccountCheckId = event.getArgument(1, CorrelationId.class);
                System.out.println(customerId + ", cid: " + correlationAccountCheckId.toString());
                accountCheckEventsPublished.get(customerId).complete(event);
            }
            else {
                var correlationId = event.getArgument(1, CorrelationId.class);
                otherEventsPublished.get(correlationId).complete(event);
            }
        }
        @Override
        public void addHandler(String eventType, Consumer<Event> handler) {
        }
    };

    private TokenService service3 = new TokenService(customQueue, mockRepository);


    /**
     *
     * @author Bence
     */
    @Given("The customerID is {string}")
    public void the_customer_id_is(String customerID) {
        this.customerID = customerID;
    }

    /**
     *
     * @author Bence
     */

    @When("the token is created")
    public void the_token_is_created() {
        try{
            var tokens = service.createToken(this.customerID);
            System.out.println("CustomerId: " + customerID + "\ntoken list size steps: " + tokens.getTokenIdList().size());
            this.tokenID = tokens.getTokenIdList().get(0);
            assertNotNull(this.tokenID);
            tokenIDList.add( this.tokenID );
        }
        catch (Exception e){
            this.error = e;
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @author Bence
     */

    @Then("tokenID is valid")
    public void token_id_is() {
        System.out.println(this.tokenID);
        assertNotNull(this.tokenID);
        Assert.assertEquals("5e6050e9-319e-42ec-bc32-132f567452ba".length(), this.tokenID.length());
    }

    /**
     *
     * @author Tamas
     */
    @When("his token is being checked")
    public void his_token_is_being_checked() {
        this.valid = service.checkToken(this.tokenID);
    }

    /**
     *
     * @author Tamas
     */
    @Then("the validation is {string}")
    public void the_result_is_true(String success) {
        String result;
        if (this.valid) result = "successful";
        else result = "unsuccessful";
        Assert.assertEquals(success, result);
    }

    /**
     *
     * @author Tamas
     */
    @When("his token is being deleted")
    public void his_token_is_being_deleted() {
        try{
            service.deleteToken(this.tokenID);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            this.error = e;
        }
    }

    /**
     *
     * @author Tamas
     */
    @Then("the token is deleted")
    public void the_token_is_deleted() {
        Assert.assertEquals(false, service.checkToken(this.tokenID));
        if (this.error != null) {
            Assert.assertEquals("Token not found", this.error.getMessage());
        }
    }

    /**
     *
     * @author Bence
     */
    @Given("he has {int} tokens already")
    public void he_has_tokens(Integer numberOfTokens) {
        try{
            List<String> tokens = service.createToken(this.customerID).getTokenIdList();
            for (int i = 0; i < (6 - numberOfTokens); i++){
                service.deleteToken(tokens.get(i));
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @author Bence
     */
    @Then("the error message is {string}")
    public void the_message_is(String message) {
        Assert.assertEquals(message, error.getMessage());
    }


    /**
     *
     * @author Florian
     */
    @Given("there is a user {string}")
    public void thereIsAUser(String customerId) throws Exception {
        this.customerID = customerId;
        List<String> mockList = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6"));
        when(mockRepository.getTokenIdList(customerId)).thenReturn(mockList);
        correlationId = CorrelationId.randomId();
        System.out.println("correlation id: " + correlationId);
        accountCheckEventsPublished.put(customerID, new CompletableFuture<>());
        otherEventsPublished.put(correlationId, new CompletableFuture<>());
    }

    /**
     *
     * @author Florian
     */
    @When("a {string} event is received")
    public void aEventForACustomerAccountIsReceived(String eventName) throws Exception {
        Event eventReceived = new Event(eventName,new Object[] {customerID, correlationId});
        var thread = new Thread(() -> {
            service3.handleTokenCreationRequested(eventReceived);
        }
        );
        thread.start();
    }

    /**
     * @author Florian
     */
    @Then("a AccountCheckRequested event is published")
    public void aEventIsPublished() {
        Event event = accountCheckEventsPublished.get(customerID).join();
        assertEquals("AccountCheckRequested", event.getType());
        assertEquals(customerID, event.getArgument(0, String.class));
    }

    /**
     * @author Florian
     */
    @When("a AccountCheckResultProvided event is received with true result")
    public void aAccountCheckResultProvidedEventIsReceivedWithTrueResult() {
        Event eventAccountCheckProvided = new Event("AccountCheckResultProvided", new Object[]{Boolean.TRUE, correlationAccountCheckId});
        service3.handleAccountCheckResultProvided(eventAccountCheckProvided);
    }

    /**
     * @author Florian
     */
    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        List<String> expectedList = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6"));
        TokenIdDTO tokenIdDTO = new TokenIdDTO();
        tokenIdDTO.setTokenIdList(expectedList);
        var event = new Event(eventName, new Object[] {tokenIdDTO, correlationId});
        var eventPublished = otherEventsPublished.get(correlationId).join();
        assertEquals(event, eventPublished);
    }

    /**
     * @author Florian
     */
    @When("a TokenCreationRequested event for userId {string} is received")
    public void aTokenCreationRequestedEventForUserIdIsReceived(String userId) {
        customerID = userId;
        try{
            var tokens = service.createToken(this.customerID);
            tokenIds = tokens.getTokenIdList();
            assertEquals(6, tokenIds.size());
        }
        catch (Exception e){
            this.error = e;
            System.out.println(e.getMessage());
        }
    }

    /**
     * @author Florian
     */
    @When("the user has {int} tokens")
    public void theUserHasNTokens(int noTokens) {
        assertEquals(noTokens, service.getNumberOfTokensForUser(customerID));
    }

    /**
     * @author Florian
     */
    @When("a AccountDeleted event for userId {string} is received")
    public void aAccountDeletedEventForUserIdIsReceived(String userId) {
        Event event = new Event("AccountDeleted", new Object[]{userId, "222"});
        service.handleAccountDeleted(event);
    }

    /**
     * @author Florian
     */
    @Then("the all tokens for that user are deleted")
    public void theAllTokensForThatUserAreDeleted() {
        var tokenCount = service.getNumberOfTokensForUser(customerID);
        assertEquals(0, tokenCount);
    }

    @After
    public void deleteUserIDsFromDTUPay(){
        for ( String ID : tokenIDList){
            try{
                service.deleteToken(ID);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
