package dtuPayApi.service.services;

import dtuPayApi.service.CorrelationId;
import dtuPayApi.service.dtos.MerchantReportDTO;
import dtuPayApi.service.dtos.ReportDTO;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ReportService {

    public static final String CUSTOMER_REPORT_REQUESTED = "CustomerReportRequested";
    public static final String CUSTOMER_REPORT_PROVIDED = "CustomerReportProvided";
    public static final String MERCHANT_REPORT_REQUESTED = "MerchantReportRequested";
    public static final String MERCHANT_REPORT_PROVIDED = "MerchantReportProvided";
    public static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    public static final String MANAGER_REPORT_PROVIDED = "ManagerReportProvided";
    public static final String REQUEST_CUSTOMER_REPORT_ERROR = "RequestCustomerReportErrorProvided";
    public static final String REQUEST_MERCHANT_REPORT_ERROR = "RequestMerchantReportErrorProvided";
    public static final String REQUEST_MANAGER_REPORT_ERROR = "RequestManagerReportErrorProvided";


    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<ReportDTO>> pendingCustomerReportRequest = new ConcurrentHashMap<>();
    private Map<CorrelationId, CompletableFuture<MerchantReportDTO>> pendingMerchantReportRequest = new ConcurrentHashMap<>();
    private Map<CorrelationId, CompletableFuture<ReportDTO>> pendingManagerReportRequest = new ConcurrentHashMap<>();


    public ReportService(MessageQueue q) {
        queue = q;
        queue.addHandler(CUSTOMER_REPORT_PROVIDED, this::handleCustomerReportProvided);
        queue.addHandler(MERCHANT_REPORT_PROVIDED, this::handleMerchantReportProvided);
        queue.addHandler(MANAGER_REPORT_PROVIDED, this::handleManagerReportProvided);
        queue.addHandler(REQUEST_CUSTOMER_REPORT_ERROR, this::handleCustomerErrorReportProvided);
        queue.addHandler(REQUEST_MERCHANT_REPORT_ERROR, this::handleMerchantErrorReportProvided);
        queue.addHandler(REQUEST_MANAGER_REPORT_ERROR, this::handleManagerErrorReportProvided);
    }

    /**
     * @author Josephine
     */
    public ReportDTO requestCustomerReport(String customerAccountId) {
        var correlationId = CorrelationId.randomId();
        pendingCustomerReportRequest.put(correlationId,new CompletableFuture<>());
        Event event = new Event(CUSTOMER_REPORT_REQUESTED, new Object[] { customerAccountId, correlationId });
        queue.publish(event);
        return pendingCustomerReportRequest.get(correlationId).join();
    }

    /**
     * @author Gunn
     */
    public ReportDTO requestManagerReport() {
        var correlationId = CorrelationId.randomId();
        pendingManagerReportRequest.put(correlationId,new CompletableFuture<>());
        Event event = new Event(MANAGER_REPORT_REQUESTED, new Object[] { correlationId });
        queue.publish(event);
        return pendingManagerReportRequest.get(correlationId).join();
    }

    /**
     * @author Josephine
     */
    public MerchantReportDTO requestMerchantReport(String merchantAccountId) {
        var correlationId = CorrelationId.randomId();
        pendingMerchantReportRequest.put(correlationId,new CompletableFuture<>());
        Event event = new Event(MERCHANT_REPORT_REQUESTED, new Object[] { merchantAccountId, correlationId });
        queue.publish(event);
        return pendingMerchantReportRequest.get(correlationId).join();
    }

    /**
     * @author Josephine
     */
    public void handleCustomerReportProvided(Event e) {
        var receivedReports = e.getArgument(0, ReportDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingCustomerReportRequest.get(correlationId).complete(receivedReports);
    }

    /**
     * @author Josephine
     */
    public void handleMerchantReportProvided(Event e) {
        var receivedReports = e.getArgument(0, MerchantReportDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingMerchantReportRequest.get(correlationId).complete(receivedReports);
    }

    /**
     * @author Gunn
     */
    public void handleManagerReportProvided(Event e) {
        var receivedReports = e.getArgument(0, ReportDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingManagerReportRequest.get(correlationId).complete(receivedReports);
    }

    /**
     * @author Gunn
     */
    public void handleCustomerErrorReportProvided(Event e) {
        var receivedError = e.getArgument(0, ReportDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingCustomerReportRequest.get(correlationId).complete(receivedError);
    }

    /**
     * @author Josephine
     */
    public void handleMerchantErrorReportProvided(Event e) {
        var receivedError = e.getArgument(0, MerchantReportDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingMerchantReportRequest.get(correlationId).complete(receivedError);
    }

    /**
     * @author Gunn
     */
    public void handleManagerErrorReportProvided(Event e) {
        var receivedError = e.getArgument(0, ReportDTO.class);
        var correlationId = e.getArgument(1, CorrelationId.class);
        pendingManagerReportRequest.get(correlationId).complete(receivedError);
    }
}
