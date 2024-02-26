package account.service.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Josephine
 */
@Data
public class BankAccountRequestDTO implements Serializable {
    private static final long serialVersionUID = 6044723800171007774L;
    private String correlationId;
    private String customerId;
    private String merchantId;
    private String customerBankAccount;
    private String merchantBankAccount;
    private String errorMessage;
}
