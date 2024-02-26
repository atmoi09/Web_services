package DTOs;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Gunn
 */
@Data // Automatic getter and setters and equals etc
public class PaymentReportDTO implements Serializable {
    private static final long serialVersionUID = -2531884656471531975L;
    private String tokenId;
    private String paymentId;
    private String merchantId;
    private String customerId;
    private BigDecimal amount;
    private String description;
}

