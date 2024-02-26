package domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Bingkun
 */
@Data // Automatic getter and setters and equals etc
public class Payment implements Serializable {
    private static final long serialVersionUID = 821858579108456995L;
    private String paymentId;
    private String merchantId;
    private String customerToken;
    private BigDecimal amount;
    private String description;

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
