package account.service.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Josephine
 */
@Data
public class Account implements Serializable {
    private static final long serialVersionUID = 9024242488284806610L;
    private String firstname;
    private String lastname;
    private String cpr;
    private String accountId;
    private String bankAccount;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account)) {
            return false;
        }
        var c = (Account) o;
        return firstname != null && firstname.equals(c.getFirstname()) &&
                lastname != null && lastname.equals(c.getLastname()) &&
                cpr != null && cpr.equals(c.getCpr()) &&
                bankAccount != null && bankAccount.equals(c.getBankAccount()) ||
                firstname == null && c.getFirstname() == null &&
                        lastname == null && c.getLastname() == null &&
                        cpr == null && c.getCpr() == null &&
                        bankAccount == null && c.getBankAccount() == null;
    }

    @Override
    public int hashCode() {
        return cpr == null ? 0 : cpr.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Account:  %s", firstname);
    }
}
