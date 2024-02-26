package dtuPayApi.service.adapter.rest;

import dtuPayApi.service.dtos.AccountDTO;
import dtuPayApi.service.dtos.MerchantReportDTO;
import dtuPayApi.service.dtos.PaymentDTO;
import dtuPayApi.service.dtos.ReportDTO;
import dtuPayApi.service.factories.AccountFactory;
import dtuPayApi.service.factories.PaymentFactory;
import dtuPayApi.service.factories.ReportFactory;
import dtuPayApi.service.services.AccountService;
import dtuPayApi.service.services.PaymentService;
import dtuPayApi.service.services.ReportService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/merchant")
public class MerchantResource {
    AccountService accountService = new AccountFactory().getService();
    PaymentService paymentService = new PaymentFactory().getService();
    ReportService reportService = new ReportFactory().getService();

    /**
     * @author Bence
     */
    @Path("/accounts")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccount(AccountDTO accountDTO) throws URISyntaxException {
        var accountDTOProvided = accountService.requestAccount(accountDTO);
        return accountDTOProvided.getErrorMessage() == null ? Response.created(new URI("customer/accounts/" + accountDTO.getAccountId())).entity(accountDTOProvided).build()
                : Response.status(Response.Status.CONFLICT).entity(accountDTOProvided.getErrorMessage()).build();
    }

    /**
     * @author Florian
     */
    @Path("/accounts/{accountId}")
    @DELETE
    public Response deleteAccount(@PathParam("accountId") String accountId) throws URISyntaxException {
        var accountIdProvided = accountService.deleteAccount(accountId);
        return accountIdProvided.equals(accountId) ? Response.noContent().build()
                : Response.status(Response.Status.CONFLICT).entity(accountId).build();
    }

    /**
     * @author Bingkun
     */
    @Path("/payments/{merchantId}/payments")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPayment(@PathParam("merchantId") String merchantId, PaymentDTO paymentDTO) throws URISyntaxException {
        PaymentDTO returnedPaymentDTO = paymentService.addPayment(paymentDTO); // successful payment returns a paymentDTO with id and description

        return returnedPaymentDTO.getErrorDescription() == null ? Response.created(new URI("/payments/" + returnedPaymentDTO.getPaymentId())).build()
                : Response.status(Response.Status.BAD_REQUEST).entity(returnedPaymentDTO.getErrorDescription()).build();
    }

    /**
     * @author Tamas
     */
    @Path("/reports/{merchantId}")
    @GET
    @Produces("application/json")
    public Response requestCustomerReport(@PathParam("merchantId") String merchantId) throws URISyntaxException {
        var reportDTO = reportService.requestMerchantReport(merchantId);
        return reportDTO.getMerchantReportList().size() == 0 ? Response.status(Response.Status.NOT_FOUND).entity(reportDTO).build()
                : Response.created(new URI("merchant/reports/" + merchantId)).entity(reportDTO).build();
    }
}
