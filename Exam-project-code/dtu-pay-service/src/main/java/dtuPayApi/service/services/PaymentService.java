package dtuPayApi.service.services;

import dtuPayApi.service.CorrelationId;
import dtuPayApi.service.dtos.PaymentDTO;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Bingkun
 */
public class PaymentService {

    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String PAYMENT_INITIATED = "PaymentInitiated";
    public static final String PAYMENT_ERROR = "PaymentError";
    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<PaymentDTO>> pendingPayments; // payments wait for processing
    public PaymentService(MessageQueue q) {
        queue = q;
        queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompleted);
        queue.addHandler(PAYMENT_ERROR, this::handlePaymentError);
        pendingPayments = new ConcurrentHashMap<>();
    }

    /**
     * @author Bingkun
     */
    public PaymentDTO addPayment(PaymentDTO paymentDTO) {
        var correlationId = CorrelationId.randomId();
        pendingPayments.put(correlationId, new CompletableFuture<>());
        Event event = new Event(PAYMENT_INITIATED, new Object[] {paymentDTO, correlationId});
        queue.publish(event);
        return pendingPayments.get(correlationId).join();
    }

    /**
     * @author Bingkun
     */
    public void handlePaymentCompleted(Event e) {
        PaymentDTO paymentDTO = e.getArgument(0, PaymentDTO.class);
        CorrelationId correlationId = e.getArgument(1, CorrelationId.class);
        pendingPayments.get(correlationId).complete(paymentDTO);
    }

    /**
     * @author Florian
     */
    public void handlePaymentError(Event e) {
        PaymentDTO paymentDTO = e.getArgument(0, PaymentDTO.class);
        CorrelationId correlationId = e.getArgument(1, CorrelationId.class);
        pendingPayments.get(correlationId).complete(paymentDTO);
    }
}