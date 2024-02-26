package domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author Josephine
 */
@Data
public class MerchantPayment {
    private String paymentId;
    private String merchantId;
    private String tokenId;
    private BigDecimal amount;
    private String description;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MerchantPayment)) {
            return false;
        }
        var c = (MerchantPayment) o;
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



