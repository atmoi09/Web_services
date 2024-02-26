package dtuPayApi.service.services;

import dtuPayApi.service.CorrelationId;
import dtuPayApi.service.dtos.TokenIdDTO;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {
    public static final String TOKEN_REQUESTED = "TokenCreationRequested";
    public static final String TOKEN_PROVIDED = "TokenProvided";
    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<TokenIdDTO>> pendingTokenRequests = new ConcurrentHashMap<>();

    public TokenService(MessageQueue q) {
        queue = q;
        queue.addHandler(TOKEN_PROVIDED, this::handleTokenProvided);
    }

    /**
     * @author Tamas
     */
    public TokenIdDTO requestArbitraryAmountTokenId(String customerAccountId, int tokenAmount) {
        var correlationId = CorrelationId.randomId();
        pendingTokenRequests.put(correlationId,new CompletableFuture<>());
        Event event = new Event(TOKEN_REQUESTED, new Object[] { customerAccountId, correlationId, tokenAmount });
        queue.publish(event);
        return pendingTokenRequests.get(correlationId).join();
    }

    /**
     * @author Bence
     */
    public void handleTokenProvided(Event e) {
        var receivedTokens = e.getArgument(0, TokenIdDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        System.out.println(receivedTokens);
        pendingTokenRequests.get(correlationId).complete(receivedTokens);
    }
}
