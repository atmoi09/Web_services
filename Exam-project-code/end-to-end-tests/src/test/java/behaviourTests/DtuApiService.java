package behaviourTests;

import behaviourTests.dtos.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class DtuApiService {

    WebTarget baseUrl;
    Client client;

    public DtuApiService() {
        client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/dtuPayApi");
    }

    /**
     * @author Gunn
     */
    public Response registerCustomerAccount(AccountDTO accountDTO) {
        var response = baseUrl.path("/customer/accounts")
                .request()
                .post(Entity.json(accountDTO));
        return response;
    }

    /**
     * @author Florian
     */
    public Response registerMerchantAccount(AccountDTO accountDTO) {
        var response = baseUrl.path("/merchant/accounts").request().post(Entity.json(accountDTO));
        return response;
    }

    /**
     * @author Bence
     */
    public TokenIdDTO requestToken(String customerId, int amount) {
        var response = baseUrl.path("/customer/tokens/" + customerId + "/tokens/" +amount)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TokenIdDTO.class);
        return response;
    }

    /**
     * @author Tamas
     */
    public Response requestCustomerReport(String customerId) {
        var response = baseUrl.path("/customer/reports/" + customerId).request(MediaType.APPLICATION_JSON_TYPE).get();
        return response;
    }

    /**
     * @author Josephine
     */
    public Response requestMerchantReport(String merchantId) {
        var response = baseUrl.path("/merchant/reports/" + merchantId).request(MediaType.APPLICATION_JSON_TYPE).get();
        return response;
    }

    /**
     * @author Bingkun
     */
    public Response requestManagerReport() {
        var response = baseUrl.path("/manager/reports/").request(MediaType.APPLICATION_JSON_TYPE).get();
        return response;
    }

    /**
     * @author Bingkun
     */
    public Response requestPayment(PaymentDTO paymentDTO) {
        var merchantId = paymentDTO.getMerchantId();
        var response = baseUrl.path("/merchant/payments/" + merchantId + "/payments")
                .request()
                .post(Entity.json(paymentDTO));
        return response;
    }

    /**
     * @author Florian
     */
    public Response deleteCustomerAccount(String accountId) {
        var response = baseUrl.path("/customer/accounts/" + accountId )
                .request()
                .delete();
        return response;
    }

    /**
     * @author Florian
     */
    public Response deleteMerchantAccount(String accountId) {
        var response = baseUrl.path("/merchant/accounts/" + accountId )
                .request()
                .delete();
        return response;
    }
}
