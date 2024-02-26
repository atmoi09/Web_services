package dtos;

import domain.CorrelationId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Bingkun
 */
@Data
public class BankAccountRequestDTO implements Serializable {
    private static final long serialVersionUID = 6044723800171007774L;
    private String customerId;
    private String merchantId;
    private String customerBankAccount;
    private String merchantBankAccount;
    private String errorMessage;
}
