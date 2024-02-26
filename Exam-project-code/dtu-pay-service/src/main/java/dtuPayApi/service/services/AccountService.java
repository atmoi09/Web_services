package dtuPayApi.service.services;

import dtuPayApi.service.CorrelationId;
import dtuPayApi.service.dtos.AccountDTO;
import dtuPayApi.service.dtos.PaymentDTO;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {
    public static final String ACCOUNT_REQUESTED = "AccountRequested";
    public static final String ACCOUNT_PROVIDED = "AccountProvided";
    public static final String ACCOUNT_EXISTS = "AccountExists";
    public static final String ACCOUNT_DELETION_REQUESTED = "AccountDeletionRequested";
    public static final String ACCOUNT_DELETED = "AccountDeleted";
    public static final String ACCOUNT_NON_EXISTENT = "AccountNonExistent";
    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<AccountDTO>> pendingAccountRequests = new ConcurrentHashMap<>();
    private Map<CorrelationId, CompletableFuture<String>> pendingAccountDeletionRequests = new ConcurrentHashMap<>();


    public AccountService(MessageQueue q) {
        queue = q;
        queue.addHandler(ACCOUNT_PROVIDED, this::handleAccountProvided);
        queue.addHandler(ACCOUNT_EXISTS, this::handleAccountExists);
        queue.addHandler(ACCOUNT_DELETED, this::handleAccountDeleted);
        queue.addHandler(ACCOUNT_NON_EXISTENT, this::handleAccountNonExistent);

    }

    /**
     * @author Bingkun
     */
    public AccountDTO requestAccount(AccountDTO accountDTO) {
        var correlationId = CorrelationId.randomId();
        pendingAccountRequests.put(correlationId,new CompletableFuture<>());
        Event event = new Event(ACCOUNT_REQUESTED, new Object[] { accountDTO, correlationId });
        queue.publish(event);
        return pendingAccountRequests.get(correlationId).join();
    }

    /**
     * @author Florian
     */
    public String deleteAccount(String accountId) {
        var correlationId = CorrelationId.randomId();
        pendingAccountDeletionRequests.put(correlationId,new CompletableFuture<>());
        Event event = new Event(ACCOUNT_DELETION_REQUESTED, new Object[] { accountId, correlationId });
        queue.publish(event);
        return pendingAccountDeletionRequests.get(correlationId).join();
    }

    /**
     * @author Josephine
     */
    public void handleAccountProvided(Event e) {
        var accountDTO = e.getArgument(0, AccountDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        System.out.println(accountDTO);
        pendingAccountRequests.get(correlationId).complete(accountDTO);
    }

    /**
     * @author Gunn
     */
    public void handleAccountExists(Event e) {
        var accountDTO = e.getArgument(0, AccountDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        System.out.println(accountDTO);
        pendingAccountRequests.get(correlationId).complete(accountDTO);
    }

    /**
     * @author Florian
     */
    public void handleAccountDeleted(Event e) {
        var accountId = e.getArgument(0, String.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingAccountDeletionRequests.get(correlationId).complete(accountId);
    }

    /**
     * @author Tamas
     */
    public void handleAccountNonExistent(Event e) {
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingAccountDeletionRequests.get(correlationId).complete("Non Existing Account");
    }
}
