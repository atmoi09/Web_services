package dtuPayApi.service;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Gunn
 */
@Data // Automatic getter and setters and equals etc
public class Payment {

    private String paymentId;
    private String merchantId;
    private String customerId;
    private BigDecimal amount;
    private String description;
    private String tokenId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Payment)) {
            return false;
        }
        var c = (Payment) o;
        return paymentId != null && paymentId.equals(c.getPaymentId()) ||
                paymentId == null && c.getPaymentId() == null;
    }

    @Override
    public int hashCode() {
        return paymentId == null ? 0 : paymentId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("PaymentId:  %s", paymentId);
    }
}
