package behaviourTests;

import DTOs.MerchantReportDTO;
import DTOs.PaymentReportDTO;
import DTOs.ReportDTO;
import domain.CorrelationId;
import domain.MerchantPayment;
import domain.Payment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mappers.Mapper;
import messaging.Event;
import messaging.MessageQueue;
import report.service.ReportRepository;
import report.service.ReportService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReportSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private ReportRepository repository = new ReportRepository();
    private ReportService service = new ReportService(queue, repository);
    PaymentReportDTO payment;
    CorrelationId customerCorrelationId;
    CorrelationId merchantCorrelationId;
    CorrelationId managerCorrelationId;

    /**
     *
     * @author Josephine
     */
    @Given("a payment with paymentId {string}, customerId {string}, merchantId {string}, amount {int}, and description {string}")
    public void aPaymentWithPaymentIdCustomerIdMerchantIdAmountAndDescription(String paymentId, String customerId, String merchantId, int amount, String description) {
        payment = new PaymentReportDTO();
        payment.setPaymentId(paymentId);
        payment.setMerchantId(merchantId);
        payment.setCustomerId(customerId);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setDescription(description);
    }


    /**
     *
     * @author Josephine
     */
    @When("a {string} event is received for the payment")
    public void anEventIsReceivedForThePayment(String event) {
        service.handlePaymentSucceeded(new Event(event, new Object[]{ payment}));
    }


    /**
     *
     * @author Gunn
     */
    @Then("the payment is added to the merchant report")
    public void thePaymentIsAddedToTheMerchantReport() throws Exception {
        List<MerchantPayment> merchantPayments = repository.getMerchantReportById(payment.getMerchantId());
        System.out.println(merchantPayments.size());
        MerchantPayment payment = merchantPayments.get(merchantPayments.size()-1);
        MerchantPayment expectedPayment = new MerchantPayment();
        Mapper.PaymentReportDTOtoMerchantPaymentMapper(this.payment, expectedPayment);
        System.out.println(payment);
        assertEquals(expectedPayment, payment);
    }

    /**
     *
     * @author Gunn
     */
    @Then("the payment is added to the customer report")
    public void thePaymentIsAddedToTheCustomerReport() throws Exception {
        List<Payment> customerPayments = repository.getCustomerReportById(payment.getCustomerId());
        Payment payment = customerPayments.get(customerPayments.size()-1);
        Payment expectedPayment = new Payment();
        Mapper.PaymentReportDTOtoPaymentMapper(this.payment, expectedPayment);
        assertEquals(expectedPayment, payment);
    }


    /**
     *
     * @author Josephine
     */
    @Then("the payment is added to the manager report")
    public void thePaymentIsAddedToTheManagerReport() throws Exception {
        List<Payment> managerPayments = repository.getManagerReport();
        Payment payment = managerPayments.get(managerPayments.size()-1);
        Payment expectedPayment = new Payment();
        Mapper.PaymentReportDTOtoPaymentMapper(this.payment, expectedPayment);
        assertEquals(expectedPayment, payment);
    }


    /**
     *
     * @author Gunn
     */
    @When("a {string} event is received for the customer report")
    public void anEventIsReceivedForTheCustomerReport(String eventName) {
        customerCorrelationId = CorrelationId.randomId();
        service.handleCustomerReportRequested(new Event(eventName, new Object[]{ payment.getCustomerId(), customerCorrelationId}));
    }

    /**
     *
     * @author Josephine
     */
    @Then("the {string} event is sent to customer")
    public void theEventIsSentToCustomer(String eventName) throws Exception {
        List<Payment> customerPayments = repository.getCustomerReportById(payment.getCustomerId());
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReportList(customerPayments);
        Event event = new Event(eventName, new Object[] { reportDTO, customerCorrelationId });
        verify(queue).publish(event);
    }


    /**
     *
     * @author Josephine
     */
    @When("a {string} event is received for the merchant report")
    public void anEventIsReceivedForTheMerchantReport(String eventName) {
        merchantCorrelationId = CorrelationId.randomId();
        service.handleMerchantReportRequested(new Event(eventName, new Object[]{ payment.getMerchantId(), merchantCorrelationId}));

    }

    /**
     *
     * @author Josephine
     */
    @Then("the {string} event is sent to merchant")
    public void theEventIsSentToMerchant(String eventName) throws Exception {
        List<MerchantPayment> merchantPayments = repository.getMerchantReportById(payment.getMerchantId());
        MerchantReportDTO reportDTO = new MerchantReportDTO();
        reportDTO.setMerchantReportList(merchantPayments);
        Event event = new Event(eventName, new Object[] { reportDTO, merchantCorrelationId });
        verify(queue).publish(event);

    }

    /**
     *
     * @author Gunn
     */
    @When("a {string} event is received for the manager report")
    public void anEventIsReceivedForTheManagerReport(String eventName) {
        managerCorrelationId = CorrelationId.randomId();
        service.handleManagerReportRequested(new Event(eventName, new Object[]{ managerCorrelationId}));
    }

    /**
     *
     * @author Gunn
     */
    @Then("the {string} event is sent to manager")
    public void theEventIsSentToManager(String eventName) throws Exception {
        List<Payment> managerPayments = repository.getManagerReport();
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReportList(managerPayments);
        Event event = new Event(eventName, new Object[] { reportDTO, managerCorrelationId });
        verify(queue).publish(event);
    }


    /**
     *
     * @author Josephine
     */
    @When("a {string} event is received for a customer with no payments")
    public void anEventReceivedForCustomerWithNoPayments(String eventName) {
        customerCorrelationId = CorrelationId.randomId();
        service.handleCustomerReportRequested(new Event(eventName, new Object[] {"testCustomer", customerCorrelationId}));
    }


    /**
     *
     * @author Gunn
     */
    @Then("the {string} event is sent to customer with no payments")
    public void anEventProvidedForCustomerWithNoPayments(String eventName) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReportList(new ArrayList<>());
        Event event = (new Event(eventName, new Object[] {reportDTO, customerCorrelationId}));
        verify(queue).publish(event);
    }

    /**
     *
     * @author Gunn
     */
    @When("a {string} event is received for a merchant with no payments")
    public void anEventReceivedForMerchantWithNoPayments(String eventName) {
        merchantCorrelationId = CorrelationId.randomId();
        service.handleMerchantReportRequested(new Event(eventName, new Object[] {"test merchant", merchantCorrelationId}));
    }

    /**
     *
     * @author Gunn
     */
    @Then("the {string} event is sent to merchant with no payments")
    public void anEventProvidedForMerchantWithNoPayments(String eventName) {
        MerchantReportDTO reportDTO = new MerchantReportDTO();
        reportDTO.setMerchantReportList(new ArrayList<>());
        Event event = (new Event(eventName, new Object[] {reportDTO, merchantCorrelationId}));
        verify(queue).publish(event);
    }

    /**
     *
     * @author Gunn
     */
    @When("a {string} event is received for a manager with no payments")
    public void anEventReceivedForManagerWithNoPayments(String eventName) {
        managerCorrelationId = CorrelationId.randomId();
        service.handleManagerReportRequested(new Event(eventName, new Object[] {managerCorrelationId}));
    }


    /**
     *
     * @author Josephine
     */
    @Then("the {string} event is sent to manager with no payments")
    public void anEventProvidedForManagerWithNoPayments(String eventName) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReportList(new ArrayList<>());
        Event event = (new Event(eventName, new Object[] {reportDTO, managerCorrelationId}));
        verify(queue).publish(event);
    }
}
