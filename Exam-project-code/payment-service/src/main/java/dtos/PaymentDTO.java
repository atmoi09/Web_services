package dtos;

import domain.CorrelationId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Bingkun
 */
@Data // Automatic getter and setters and equals etc
public class PaymentDTO implements Serializable {
    private static final long serialVersionUID = -2531884656471531975L;
    private String paymentId;
    private String merchantId;
    private String customerToken;
    private BigDecimal amount;
    private String errorDescription;
}

