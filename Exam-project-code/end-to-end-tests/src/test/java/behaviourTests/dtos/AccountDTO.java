package behaviourTests.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Josephine
 */
@Data
public class AccountDTO implements Serializable {
    private static final long serialVersionUID = -1005048725971420371L;
    private String firstname;
    private String lastname;
    private String cpr;
    private String accountId;
    private String bankAccount;
    private String errorMessage;
}


