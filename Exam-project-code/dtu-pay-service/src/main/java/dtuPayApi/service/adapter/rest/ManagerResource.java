package dtuPayApi.service.adapter.rest;

import dtuPayApi.service.factories.ReportFactory;
import dtuPayApi.service.services.ReportService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/manager")
public class ManagerResource {
    ReportService reportService = new ReportFactory().getService();


    /**
     * @author Gunn
     */
    @Path("/reports")
    @GET
    @Produces("application/json")
    public Response requestManagerReport() throws URISyntaxException {
        var reportDTO = reportService.requestManagerReport();
        return reportDTO.getReportList().size() == 0 ? Response.status(Response.Status.NOT_FOUND).entity(reportDTO).build()
                : Response.created(new URI("manager/reports/")).entity(reportDTO).build();
    }

}
