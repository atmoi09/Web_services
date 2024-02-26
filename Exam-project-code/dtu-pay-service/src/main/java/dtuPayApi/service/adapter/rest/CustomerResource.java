package dtuPayApi.service.adapter.rest;

import dtuPayApi.service.dtos.AccountDTO;
import dtuPayApi.service.factories.AccountFactory;
import dtuPayApi.service.factories.ReportFactory;
import dtuPayApi.service.factories.TokenFactory;
import dtuPayApi.service.services.AccountService;
import dtuPayApi.service.services.ReportService;
import dtuPayApi.service.services.TokenService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/customer")
public class CustomerResource {
    AccountService accountService = new AccountFactory().getService();
    TokenService tokenService = new TokenFactory().getService();
    ReportService reportService = new ReportFactory().getService();

    /**
     * @author Josephine
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
     * @author Bingkun
     */
    @Path("/accounts/{accountId}")
    @DELETE
    public Response deleteAccount(@PathParam("accountId") String accountId){
        var accountIdProvided = accountService.deleteAccount(accountId);
        return accountIdProvided.equals(accountId) ? Response.noContent().build()
                : Response.status(Response.Status.CONFLICT).entity(accountId).build();
    }


    /**
     * @author Tamas
     */
    @Path("/tokens/{customerId}/tokens/{tokenAmount}")
    @GET
    @Produces("application/json")
    public Response requestArbitraryTokens(@PathParam("customerId") String customerId, @PathParam("tokenAmount") int tokenAmount) throws URISyntaxException {
        System.out.println(tokenAmount+ ", arbitrary tokens received==================================");
        System.out.println("Facade customer id: " + customerId);
        var tokenIdDTO = tokenService.requestArbitraryAmountTokenId(customerId, tokenAmount);
        return Response.status(Response.Status.OK)
                .entity(tokenIdDTO)
                .build();
    }


    /**
     * @author Gunn
     */
    @Path("/reports/{customerId}")
    @GET
    @Produces("application/json")
    public Response requestCustomerReport(@PathParam("customerId") String customerId) throws URISyntaxException {
        var reportDTO = reportService.requestCustomerReport(customerId);
        return reportDTO.getReportList().size() == 0 ? Response.status(Response.Status.NOT_FOUND).entity(reportDTO).build()
                : Response.created(new URI("customer/reports/" + customerId)).entity(reportDTO).build();
    }
}
